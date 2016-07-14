package com.facebook.tracery.database;

import com.facebook.tracery.database.trace.MasterTraceTable;
import com.facebook.tracery.thrift.table.TableColumnInfo;
import com.facebook.tracery.thrift.table.TableColumnType;
import com.facebook.tracery.thrift.table.TableInfo;
import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Top-level database table wrapper.
 */
public abstract class Table {
  private final Database db;

  private final DbTable dbTable;
  private final List<Column> columns = new ArrayList<>();

  /**
   * Constructor for automatically binding table name to type.
   */
  public Table(Database db) {
    this.db = db;
    dbTable = db.getDbSchema().addTable(getTableName(getClass()));
  }

  /**
   * Constructor for specifying a custom name.
   */
  public Table(Database db, String tableName) {
    this.db = db;
    dbTable = db.getDbSchema().addTable(tableName);
  }

  public Database getDatabase() {
    return db;
  }

  public String getName() {
    return dbTable.getName();
  }

  public boolean create() throws SQLException {
    CreateTableQuery query = new CreateTableQuery(dbTable, true);
    String sql = query.validate().toString();
    try (Statement statement = db.createStatement()) {
      return statement.execute(sql);
    }
  }

  public boolean drop() throws SQLException {
    String sql = String.format("DROP TABLE IF EXISTS \"%s\"", dbTable.getTableNameSQL());
    try (Statement statement = db.createStatement()) {
      return statement.execute(sql);
    }
  }

  public List<Column> getColumns() {
    return Collections.unmodifiableList(columns);
  }

  public int getRowCount() throws SQLException {
    // SELECT Count(*) FROM trace_a8yb6qc5fwir459dc9ajdilvc_diskio
    String sql = new SelectQuery()
        .addCustomColumns(new CustomSql("count(*)"))
        .addCustomFromTable(dbTable.getTableNameSQL())
        .validate().toString();

    int result = -1;
    try (Statement statement = db.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {

      if (resultSet.next()) {
        result = resultSet.getInt(1);
      }
    }

    return result;
  }

  /**
   * Return information about the specified trace table.
   */
  public TableInfo getTableInfo()
      throws SQLException {

    TableInfo tableInfo = new TableInfo();
    tableInfo.setName(dbTable.getName());

    int rowCount = getRowCount();
    tableInfo.setRowCount(rowCount);

    List<TableColumnInfo> tableColumns = new ArrayList<>();

    for (Column column : columns) {
      TableColumnInfo columnInfo = new TableColumnInfo();
      columnInfo.setName(column.getName());
      columnInfo.setType(column.getType());
      tableColumns.add(columnInfo);
    }

    tableInfo.setColumns(tableColumns);

    return tableInfo;
  }

  public Column addColumn(String name, TableColumnType type) {
    Column column = new Column(dbTable, name, type);
    columns.add(column);
    return column;
  }

  public DbTable getDbTable() {
    return dbTable;
  }

  public static String getTableName(Class<? extends Table> clazz) {
    String className = clazz.getCanonicalName();
    if (className.contains("_")) {
      throw new IllegalArgumentException("Table class names must not contain '_'.");
    }
    return className.replace('.', '_');
  }

  public static Class<? extends Table> getTableClass(String tableName) throws
      ClassNotFoundException {
    String className = tableName.replace('_', '.');
    return (Class<? extends Table>) Class.forName(className);
  }

  public static Table createTable(Database db, String tableClassName) throws ClassNotFoundException,
      IllegalAccessException, InstantiationException, NoSuchMethodException,
      InvocationTargetException {
    Class<? extends Table> tableClass = getTableClass(tableClassName);
    return tableClass.getDeclaredConstructor(Database.class).newInstance(db);
  }
}
