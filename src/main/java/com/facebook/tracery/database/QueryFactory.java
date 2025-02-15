package com.facebook.tracery.database;

import com.facebook.tracery.thrift.query.Aggregation;
import com.facebook.tracery.thrift.query.BinaryExpression;
import com.facebook.tracery.thrift.query.BinaryOperation;
import com.facebook.tracery.thrift.query.Expression;
import com.facebook.tracery.thrift.query.Grouping;
import com.facebook.tracery.thrift.query.Ordering;
import com.facebook.tracery.thrift.query.Query;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class QueryFactory {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final Database db;

  public QueryFactory(Database db) {
    this.db = db;
  }

  public SelectQuery createSelectQuery(Query query) throws SQLException {
    SelectQuery selectQuery = new SelectQuery();
    applyQuerySelect(query, selectQuery);
    applyQueryDistinct(query, selectQuery);
    applyQueryFrom(query, selectQuery);
    applyQueryWhere(query, selectQuery);
    applyQueryGroupBy(query, selectQuery);
    applyQueryHaving(query, selectQuery);
    applyQueryOrderBy(query, selectQuery);
    applyQueryOffsetLimit(query, selectQuery);
    return selectQuery;
  }

  /* package */ static Map<Aggregation, String> aggregationSqlMap =
      new EnumMap<>(Aggregation.class);

  static {
    aggregationSqlMap.put(Aggregation.AVG, "avg");
    aggregationSqlMap.put(Aggregation.MIN, "min");
    aggregationSqlMap.put(Aggregation.MAX, "max");
    aggregationSqlMap.put(Aggregation.SUM, "sum");
    aggregationSqlMap.put(Aggregation.COUNT, "count");
  }

  private void applyQuerySelect(Query query, SelectQuery selectQuery) {
    List<ResultColumn> resultColumns = query.getResultSet();
    if (resultColumns != null && !resultColumns.isEmpty()) {
      for (ResultColumn resultColumn : resultColumns) {
        String expression = formatExpressionTree(resultColumn.getExpression());

        Aggregation aggregation = resultColumn.getAggregation();
        if (aggregation != Aggregation.NONE) {
          String aggregrationSql = aggregationSqlMap.get(aggregation);
          if (aggregrationSql == null) {
            throw new UnsupportedOperationException("Unsupported aggregation: " + aggregation);
          }
          expression = aggregation.name().toLowerCase() + "(" + expression + ")";
        }

        String alias = resultColumn.getResultAlias();
        if (alias != null && !alias.isEmpty()) {
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

  private String formatExpressionTree(Expression expression) {
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

  /* package */ static Map<UnaryOperation, String> unaryOpSqlMap =
      new EnumMap<>(UnaryOperation.class);

  static {
    unaryOpSqlMap.put(UnaryOperation.NEGATE, "-");
    unaryOpSqlMap.put(UnaryOperation.NOT, "NOT");
  }

  private String formatUnaryExpresssion(UnaryExpression unaryExpression) {
    UnaryOperation unaryOp = unaryExpression.getOperation();
    String sqlUnaryOp = unaryOpSqlMap.get(unaryOp);
    if (sqlUnaryOp == null) {
      throw new UnsupportedOperationException("Unsupported unary operation: " + unaryOp);
    }

    String operand = formatExpressionTree(unaryExpression.getOperand());
    return sqlUnaryOp + operand;
  }

  /* package */ static Map<BinaryOperation, String> binaryOpSqlMap =
      new EnumMap<>(BinaryOperation.class);

  static {
    binaryOpSqlMap.put(BinaryOperation.CONCATENATE, "||");
    binaryOpSqlMap.put(BinaryOperation.MULTIPLY, "*");
    binaryOpSqlMap.put(BinaryOperation.DIVIDE, "/");
    binaryOpSqlMap.put(BinaryOperation.MODULO, "%");
    binaryOpSqlMap.put(BinaryOperation.ADD, "+");
    binaryOpSqlMap.put(BinaryOperation.SUBTRACT, "-");
    binaryOpSqlMap.put(BinaryOperation.SHIFT_LEFT, "<<");
    binaryOpSqlMap.put(BinaryOperation.SHIFT_RIGHT, ">>");
    binaryOpSqlMap.put(BinaryOperation.BITWISE_AND, "&");
    binaryOpSqlMap.put(BinaryOperation.BITWISE_OR, "|");
    binaryOpSqlMap.put(BinaryOperation.LT, "<");
    binaryOpSqlMap.put(BinaryOperation.LE, "<=");
    binaryOpSqlMap.put(BinaryOperation.GE, ">=");
    binaryOpSqlMap.put(BinaryOperation.GT, ">");
    binaryOpSqlMap.put(BinaryOperation.EQ, "==");
    binaryOpSqlMap.put(BinaryOperation.NEQ, "!=");
    binaryOpSqlMap.put(BinaryOperation.IN, "IN");
    binaryOpSqlMap.put(BinaryOperation.LIKE, "LIKE");
    binaryOpSqlMap.put(BinaryOperation.GLOB, "GLOB");
    binaryOpSqlMap.put(BinaryOperation.REGEXP, "REGEXP");
    binaryOpSqlMap.put(BinaryOperation.AND, "AND");
    binaryOpSqlMap.put(BinaryOperation.OR, "OR");
  }

  private String formatBinaryExpression(BinaryExpression binaryExpression) {
    BinaryOperation binaryOp = binaryExpression.getOperation();
    String sqlBinaryOp = binaryOpSqlMap.get(binaryOp);
    if (sqlBinaryOp == null) {
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

  /* package */ static Map<TrinaryOperation, String> trinaryOpSqlMap =
      new EnumMap<>(TrinaryOperation.class);

  static {
    trinaryOpSqlMap.put(TrinaryOperation.BETWEEN, "BETWEEN");
  }

  private String formatTrinaryExpression(TrinaryExpression trinaryExpression) {
    TrinaryOperation trinaryOp = trinaryExpression.getOperation();
    String sqlTrinaryOp = trinaryOpSqlMap.get(trinaryOp);
    if (sqlTrinaryOp == null) {
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
