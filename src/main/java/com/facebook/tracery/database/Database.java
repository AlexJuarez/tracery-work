package com.facebook.tracery.database;

import com.facebook.tracery.thrift.query.Query;
import com.facebook.tracery.thrift.query.QueryResult;
import com.facebook.tracery.thrift.query.QueryResultRow;
import com.facebook.tracery.thrift.table.TableColumnType;
import com.healthmarketscience.sqlbuilder.SelectQuery;
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
  public static final int DATABASE_SCHEMA_VERSION = 1;

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

  public static class VersionMisatchException extends RuntimeException {
    static final long serialVersionUID = -1L;

    private final int expectedVersion;
    private final int actualVersion;

    public VersionMisatchException(int expectedVersion, int actualVersion) {
      super(String.format(String.format("Version mismatch - expected version %d but was %d.",
          expectedVersion, actualVersion)));
      this.expectedVersion = expectedVersion;
      this.actualVersion = actualVersion;
    }

    public int getExpectedVersion() {
      return expectedVersion;
    }

    public int getActualVersion() {
      return actualVersion;
    }
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

      int dbVersion = getDatabaseSchemaVersion();
      if (dbVersion <= 0 && !connection.isReadOnly()) {
        // new (writeable) database
        updateDatabaseSchemaVersion();
      } else if (dbVersion != DATABASE_SCHEMA_VERSION) {
        throw new VersionMisatchException(DATABASE_SCHEMA_VERSION, dbVersion);
      }
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

  public int getDatabaseSchemaVersion() {
    String sql = "PRAGMA user_version;";

    try (Statement statement = createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {
      return resultSet.getInt(1);
    } catch (SQLException ex) {
      return -1;
    }
  }

  private void updateDatabaseSchemaVersion() throws SQLException {
    String sql = String.format("PRAGMA user_version = %d;", DATABASE_SCHEMA_VERSION);

    try (Statement statement = createStatement()) {
      statement.execute(sql);
    }
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
  public QueryResult doSelectQuery(Query query) throws SQLException {
    QueryFactory queryFactory = new QueryFactory(this);
    SelectQuery selectQuery = queryFactory.createSelectQuery(query);
    String sql = selectQuery.validate().toString();
    logger.info("Select Query SQL: " + sql);

    List<String> resultColumnNames = new ArrayList<>();
    List<TableColumnType> resultColumnTypes = new ArrayList<>();
    List<QueryResultRow> resultRows = new ArrayList<>();
    try (Statement statement = createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {
      statement.setEscapeProcessing(false);

      ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

      final int numColumns = resultSetMetaData.getColumnCount();
      for (int columnIdx = 1; columnIdx <= numColumns; columnIdx++) {
        String columnName = resultSetMetaData.getColumnName(columnIdx);
        resultColumnNames.add(columnName);
        String columnTypeName = resultSetMetaData.getColumnTypeName(columnIdx);
        TableColumnType columnType = Column.decodeType(columnTypeName);
        resultColumnTypes.add(columnType);
      }

      while (resultSet.next()) {
        List<String> row = new ArrayList<>(numColumns);
        for (int columnIdx = 1; columnIdx <= numColumns; columnIdx++) {
          row.add(String.valueOf(resultSet.getObject(columnIdx)));
        }
        QueryResultRow resultRow = new QueryResultRow(row);
        resultRows.add(resultRow);
      }
    }

    QueryResult result = new QueryResult();
    result.setColumnNames(resultColumnNames);
    result.setColumnTypes(resultColumnTypes);
    result.setRows(resultRows);

    return result;
  }
}

