package com.facebook.tracery.database;

import com.facebook.tracery.thrift.query.Aggregation;
import com.facebook.tracery.thrift.query.BinaryExpression;
import com.facebook.tracery.thrift.query.BinaryOperation;
import com.facebook.tracery.thrift.query.Expression;
import com.facebook.tracery.thrift.query.Grouping;
import com.facebook.tracery.thrift.query.Ordering;
import com.facebook.tracery.thrift.query.Query;
import com.facebook.tracery.thrift.query.QueryResult;
import com.facebook.tracery.thrift.query.ResultColumn;
import com.facebook.tracery.thrift.query.TrinaryExpression;
import com.facebook.tracery.thrift.query.TrinaryOperation;
import com.facebook.tracery.thrift.query.UnaryExpression;
import com.facebook.tracery.thrift.query.UnaryOperation;
import com.facebook.tracery.thrift.query.ValueExpression;
import com.healthmarketscience.sqlbuilder.CustomCondition;
import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.custom.mysql.MysLimitClause;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracery file database connection wrapper.
 */
public class Database {
  private static final int DEFAULT_STATEMENT_TIMEOUT_SEC = 30;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private File file;
  private Connection connection;

  private DbSpec dbSpec;
  private DbSchema dbSchema;

  private Map<String, Table> tableNameMap = new HashMap<>();

  public enum Access {
    READ_ONLY,
    READ_WRITE
  }

  /**
   * In-memory database.
   *
   * @throws ClassNotFoundException on failure initialize SQLite JDBC binding
   */
  public Database() throws ClassNotFoundException {
    // Load the sqlite-JDBC driver using the current class loader.
    Class.forName("org.sqlite.JDBC");
  }

  /**
   * File store database.
   *
   * @param file database backing file
   * @throws ClassNotFoundException on failure initialize SQLite JDBC binding
   */
  public Database(File file) throws ClassNotFoundException {
    this();
    this.file = file;
  }

  public void connect(Access access) throws SQLException {
    if (connection == null) {
      SQLiteConfig config = new SQLiteConfig();
      config.setReadOnly(access != Access.READ_WRITE);

      String dbUrl;
      if (file != null) {
        dbUrl = "jdbc:sqlite:" + file.toString();
      } else {
        dbUrl = "jdbc:sqlite::memory:";
      }
      connection = DriverManager.getConnection(dbUrl, config.toProperties());

      dbSpec = new DbSpec();
      dbSchema = dbSpec.addDefaultSchema();
    }
  }

  public boolean isConnected() {
    return connection != null;
  }

  public Connection getConnection() {
    return connection;
  }

  public DbSpec getDbSpec() {
    return dbSpec;
  }

  public DbSchema getDbSchema() {
    return dbSchema;
  }

  public boolean isReadOnly() throws SQLException {
    checkConnection();
    return connection.isReadOnly();
  }

  public void disconnect() {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException ex) {
        logger.error("Failed to disconnect from database.", ex);
      }
      connection = null;
      dbSpec = null;
      dbSchema = null;
      tableNameMap.clear();
    }
  }

  @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE")
  public Statement createStatement() throws SQLException {
    checkConnection();

    Statement statement = connection.createStatement();
    statement.setQueryTimeout(DEFAULT_STATEMENT_TIMEOUT_SEC);

    return statement;
  }

  @SuppressWarnings("unchecked")
  public <T extends Table> T getTableByClass(Class<T> tableClass) {
    String tableName = Table.getTableName(tableClass);
    return (T) getTableByName(tableName);
  }

  public Table getTableByName(String tableName) {
    Table result = tableNameMap.get(tableName);
    if (result == null) {
      try {
        result = Table.createTable(this, tableName);
        tableNameMap.put(tableName, result);
      } catch (Exception ex) {
        throw new IllegalArgumentException("Unknown table '" + tableName + "'", ex);
      }
    }
    return result;
  }

  private void checkConnection() {
    if (!isConnected()) {
      throw new IllegalStateException("Not connected to database.");
    }
  }

  @SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
  public QueryResult doQuery(Query query) throws SQLException {
    SelectQuery selectQuery = new SelectQuery();

    applyQuerySelect(query, selectQuery);
    applyQueryDistinct(query, selectQuery);
    applyQueryFrom(query, selectQuery);
    applyQueryWhere(query, selectQuery);
    applyQueryGroupBy(query, selectQuery);
    applyQueryHaving(query, selectQuery);
    applyQueryOrderBy(query, selectQuery);
    applyQueryOffsetLimit(query, selectQuery);

    String sql = selectQuery.validate().toString();
    logger.info("SQL Select: " + sql);

    List<String> resultColumnNames = new ArrayList<>();
    List<List<String>> resultRows = new ArrayList<>();
    try (Statement statement = createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {
      statement.setEscapeProcessing(false);

      ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

      final int numColumns = resultSetMetaData.getColumnCount();
      for (int columnIdx = 1; columnIdx <= numColumns; columnIdx++) {
        String columnName = resultSetMetaData.getColumnName(columnIdx);
        resultColumnNames.add(columnName);
      }

      while (resultSet.next()) {
        List<String> row = new ArrayList<>(numColumns);
        for (int columnIdx = 1; columnIdx <= numColumns; columnIdx++) {
          row.add(resultSet.getObject(columnIdx).toString());
        }
        resultRows.add(row);
      }
    }

    QueryResult result = new QueryResult();
    result.setColumnNames(resultColumnNames);
    result.setRows(resultRows);

    return result;
  }

  private void applyQuerySelect(Query query, SelectQuery selectQuery) {
    List<ResultColumn> resultColumns = query.getResultSet();
    if (resultColumns != null && !resultColumns.isEmpty()) {
      for (ResultColumn resultColumn : resultColumns) {
        String expression = formatExpressionTree(resultColumn.getExpression());

        Aggregation aggregation = resultColumn.getAggregation();
        switch (aggregation) {
          case NONE:
            break;
          case AVG:
          case MIN:
          case MAX:
          case SUM:
          case COUNT:
            expression = aggregation.name().toLowerCase() + "(" + expression + ")";
            break;
          default:
            break;
        }

        String alias = resultColumn.getResultAlias();
        if (alias != null && alias.isEmpty()) {
          expression += " AS " + alias;
        }

        selectQuery.addCustomColumns(new CustomSql(expression));
      }
    } else {
      selectQuery.addAllColumns();
    }
  }

  private void applyQueryDistinct(Query query, SelectQuery selectQuery) {
    if (query.isDistinct()) {
      selectQuery.setIsDistinct(true);
    }
  }

  private void applyQueryFrom(Query query, SelectQuery selectQuery) {
    for (String tableName : query.getSourceTables()) {
      selectQuery.addCustomFromTable(tableName);
    }
  }

  private void applyQueryWhere(Query query, SelectQuery selectQuery) {
    Expression whereFilterExpression = query.getWhere();
    if (whereFilterExpression != null) {
      String expression = formatExpressionTree(whereFilterExpression);
      selectQuery.addCondition(new CustomCondition(expression));
    }
  }

  private void applyQueryGroupBy(Query query, SelectQuery selectQuery) {
    List<Grouping> groupings = query.getGroupBy();
    if (groupings != null) {
      for (Grouping grouping : groupings) {
        String columnName = grouping.getColumnNameOrIndex();
        selectQuery.addCustomGroupings(columnName);
      }
    }
  }

  private void applyQueryHaving(Query query, SelectQuery selectQuery) {
    Expression havingFilterExpression = query.getHaving();
    if (havingFilterExpression != null) {
      String expression = formatExpressionTree(havingFilterExpression);
      selectQuery.addHaving(new CustomCondition(expression));
    }
  }

  private void applyQueryOrderBy(Query query, SelectQuery selectQuery) {
    List<Ordering> orderings = query.getOrderBy();
    if (orderings != null) {
      for (Ordering ordering : orderings) {
        String columnName = ordering.getColumnName();
        boolean ascending = ordering.isAscending();
        selectQuery.addCustomOrdering(columnName, ascending ? OrderObject.Dir.ASCENDING :
            OrderObject.Dir.DESCENDING);
      }
    }
  }

  private void applyQueryOffsetLimit(Query query, SelectQuery selectQuery) {
    int offset = query.getOffset();
    int limit = query.getLimit();
    if (offset >= 0) {
      if (limit < 0) {
        throw new IllegalArgumentException("OFFSET requires a LIMIT");
      }
      selectQuery.addCustomization(new MysLimitClause(offset, limit));
    } else if (limit >= 0) {
      selectQuery.addCustomization(new MysLimitClause(limit));
    }
  }

  protected String formatExpressionTree(Expression expression) {
    String result;

    if (expression.isSetValueExpression()) {
      ValueExpression valueExpression = expression.getValueExpression();
      result = formatValueExpression(valueExpression);
    } else if (expression.isSetUnaryExpression()) {
      UnaryExpression unaryExpression = expression.getUnaryExpression();
      result = formatUnaryExpresssion(unaryExpression);
    } else if (expression.isSetBinaryExpression()) {
      BinaryExpression binaryExpression = expression.getBinaryExpression();
      result = formatBinaryExpression(binaryExpression);
    } else if (expression.isSetTrinaryExpression()) {
      TrinaryExpression trinaryExpression = expression.getTrinaryExpression();
      result = formatTrinaryExpression(trinaryExpression);
    } else {
      throw new IllegalArgumentException("No expression set: " + expression);
    }

    return "(" + result.toString() + ")";
  }

  private String formatValueExpression(ValueExpression valueExpression) {
    return valueExpression.getValue();
  }

  private String formatUnaryExpresssion(UnaryExpression unaryExpression) {
    String sqlUnaryOp;
    UnaryOperation unaryOp = unaryExpression.getOperation();
    switch (unaryOp) {
      case NEGATE:
        sqlUnaryOp = "-";
        break;
      case NOT:
        sqlUnaryOp = "NOT";
        break;
      default:
        throw new UnsupportedOperationException("Unsupported unary operation: " + unaryOp);
    }
    String operand = formatExpressionTree(unaryExpression.getOperand());
    return sqlUnaryOp + operand;
  }

  private String formatBinaryExpression(BinaryExpression binaryExpression) {
    String sqlBinaryOp;
    BinaryOperation binaryOp = binaryExpression.getOperation();
    switch (binaryOp) {
      case LT:
        sqlBinaryOp = "<";
        break;
      case LE:
        sqlBinaryOp = "<=";
        break;
      case GE:
        sqlBinaryOp = ">=";
        break;
      case GT:
        sqlBinaryOp = ">";
        break;
      case EQ:
        sqlBinaryOp = "==";
        break;
      case NEQ:
        sqlBinaryOp = "!=";
        break;
      default:
        throw new UnsupportedOperationException("Unsupported binary operation: " + binaryOp);
    }
    String leftOperand = formatExpressionTree(binaryExpression.getLeftOperand());
    StringBuilder result = new StringBuilder();
    result.append(leftOperand);
    result.append(" ");
    result.append(sqlBinaryOp);
    result.append(" ");
    String rightOperand = formatExpressionTree(binaryExpression.getRightOperand());
    result.append(rightOperand);
    return result.toString();
  }

  private String formatTrinaryExpression(TrinaryExpression trinaryExpression) {
    String sqlTrinaryOp;
    TrinaryOperation trinaryOp = trinaryExpression.getOperation();
    switch (trinaryOp) {
      case BETWEEN:
        sqlTrinaryOp = "BETWEEN";
        break;
      default:
        throw new UnsupportedOperationException("Unsupported trinary operation: " + trinaryOp);
    }

    String middleOperand = formatExpressionTree(trinaryExpression.getRightOperand0());
    StringBuilder result = new StringBuilder();
    result.append(middleOperand);
    result.append(" ");
    result.append(sqlTrinaryOp);
    result.append(" ");
    String leftOperand = formatExpressionTree(trinaryExpression.getLeftOperand());
    result.append(leftOperand);
    result.append(" AND ");
    String rightOperand = formatExpressionTree(trinaryExpression.getRightOperand1());
    result.append(rightOperand);
    return result.toString();
  }
}

