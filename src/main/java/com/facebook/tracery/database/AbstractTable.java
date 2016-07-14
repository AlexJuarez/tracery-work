package com.facebook.tracery.database;

import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Top-level database table wrapper.
 */
public abstract class AbstractTable {
  protected final Database db;

  protected DbTable dbTable;

  public enum ColumnAffinity {
    NONE,
    INT,
    TEXT,
    FLOAT,
    BLOB
  }

  public AbstractTable(Database db) {
    this.db = db;
  }

  public DbTable getTable() {
    return dbTable;
  }

  public boolean create() throws SQLException {
    String sql = new CreateTableQuery(dbTable, true).validate().toString();
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
}

