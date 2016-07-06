package com.facebook.tracery.database.trace;

import com.facebook.tracery.Util;
import com.facebook.tracery.database.AbstractTable;
import com.facebook.tracery.database.Database;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Directory of trace data and associated tables.
 */
public class MasterTraceTable extends AbstractTable {
  public static final String TABLE_NAME = "trace_master";

  public static final String TRACE_INDEX_COLUMN_NAME = "trace_idx";
  public static final String URL_COLUMN_NAME = "trace_url";
  public static final String BEGIN_TIME_COLUMN_NAME = "begin_time";
  public static final String END_TIME_COLUMN_NAME = "end_time";
  public static final String DESCRIPTION_COLUMN_NAME = "description";

  private final DbColumn dbColumnTraceIndex;
  private final DbColumn dbColumnTraceUrl;
  private final DbColumn dbColumnBeginTime;
  private final DbColumn dbColumnEndTime;
  private final DbColumn dbColumnDescription;

  public MasterTraceTable(Database db) {
    super(db);

    dbTable = db.getDbSchema().addTable(TABLE_NAME);

    dbColumnTraceIndex = dbTable.addColumn(TRACE_INDEX_COLUMN_NAME, ColumnAffinity.INT.name(),
        null);
    // Integer primary key implies UNIQUE and AUTOINCREMENT.
    dbTable.primaryKey("[Trace id primary key constraint]", dbColumnTraceIndex.getName());

    dbColumnTraceUrl = dbTable.addColumn(URL_COLUMN_NAME, ColumnAffinity.TEXT.name(), null);
    dbColumnBeginTime = dbTable.addColumn(BEGIN_TIME_COLUMN_NAME, ColumnAffinity.INT.name(), null);
    dbColumnEndTime = dbTable.addColumn(END_TIME_COLUMN_NAME, ColumnAffinity.INT.name(), null);
    dbColumnDescription = dbTable.addColumn(DESCRIPTION_COLUMN_NAME, ColumnAffinity.TEXT.name(),
        null);
  }

  public DbColumn getTraceIndexColumn() {
    return dbColumnTraceIndex;
  }

  public DbColumn getTraceUrlColumn() {
    return dbColumnTraceUrl;
  }

  public DbColumn getBeginTimeColumn() {
    return dbColumnBeginTime;
  }

  public DbColumn getEndTimeColumn() {
    return dbColumnEndTime;
  }

  public DbColumn getDescriptionColumn() {
    return dbColumnDescription;
  }

  /**
   * Add an entry in the database for a new trace file. Returns the unique trace ID.
   *
   * @param url trace file URL
   * @param beginTime begin time of trace (microseconds since epoch) or null if unknown.
   * @param endTime end time of trace (microseconds since epoch) or null if unknown.
   * @param description human-readable description of trace or null if none.
   * @return trace index
   * @throws SQLException on failure
   */
  public int addTrace(URL url, Long beginTime, Long endTime, String description)
      throws SQLException {
    String sql =
        new InsertQuery(dbTable)
            .addColumn(dbColumnTraceIndex, null)
            .addColumn(dbColumnTraceUrl, url != null ? url.toString() : null)
            .addColumn(dbColumnBeginTime, beginTime)
            .addColumn(dbColumnEndTime, endTime)
            .addColumn(dbColumnDescription, description)
            .validate().toString();
    Statement statement = db.createStatement();
    statement.executeUpdate(sql);

    ResultSet resultSet = statement.getGeneratedKeys();
    int rowid = resultSet.getInt(1);
    return rowid;
  }

}

