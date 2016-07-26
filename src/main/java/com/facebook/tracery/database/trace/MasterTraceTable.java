package com.facebook.tracery.database.trace;

import com.facebook.tracery.database.Column;
import com.facebook.tracery.database.Database;
import com.facebook.tracery.database.JsonCoder;
import com.facebook.tracery.database.Table;
import com.facebook.tracery.thrift.TraceInfo;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Directory of trace data and associated tables.
 */
public class MasterTraceTable extends Table {
  public static final String TRACE_INDEX_COLUMN_NAME = "trace_idx";
  public static final String URL_COLUMN_NAME = "trace_url";
  public static final String BEGIN_TIME_COLUMN_NAME = "begin_time";
  public static final String END_TIME_COLUMN_NAME = "end_time";
  public static final String DESCRIPTION_COLUMN_NAME = "description";
  public static final String TRACE_TABLE_NAMES_COLUMN_NAME = "tables";

  private Column columnTraceIndex;
  private Column columnTraceUrl;
  private Column columnBeginTime;
  private Column columnEndTime;
  private Column columnDescription;
  private Column columnTraceTableNames;

  public MasterTraceTable(Database db) throws SQLException {
    super(db);
  }

  public Column getTraceIndexColumn() {
    return columnTraceIndex;
  }

  public Column getTraceUrlColumn() {
    return columnTraceUrl;
  }

  public Column getBeginTimeColumn() {
    return columnBeginTime;
  }

  public Column getEndTimeColumn() {
    return columnEndTime;
  }

  public Column getDescriptionColumn() {
    return columnDescription;
  }

  public Column getTraceTableNamesColumn() {
    return columnTraceTableNames;
  }

  @Override
  protected void setupColumns() {
    columnTraceIndex = addColumn(TRACE_INDEX_COLUMN_NAME, Column.INDEX_COLUMN_TYPE);
    // Integer primary key implies UNIQUE and AUTOINCREMENT.
    columnTraceIndex.addPrimaryKeyConstraint("[Trace id primary key constraint]");

    columnTraceUrl = addColumn(URL_COLUMN_NAME, Column.URL_COLUMN_TYPE);
    columnBeginTime = addColumn(BEGIN_TIME_COLUMN_NAME, Column.TIMESTAMP_COLUMN_TYPE);
    columnEndTime = addColumn(END_TIME_COLUMN_NAME, Column.TIMESTAMP_COLUMN_TYPE);
    columnDescription = addColumn(DESCRIPTION_COLUMN_NAME, Column.TEXT_COLUMN_TYPE);
    columnTraceTableNames = addColumn(TRACE_TABLE_NAMES_COLUMN_NAME, Column.NAME_ARRAY_COLUMN_TYPE);
  }

  /**
   * Add an entry in the database for a new trace file. Returns the unique trace ID_COLUMN_TYPE.
   *
   * @param url         trace file URL_COLUMN_TYPE
   * @param beginTime   begin time of trace (microseconds since epoch) or null if unknown.
   * @param endTime     end time of trace (microseconds since epoch) or null if unknown.
   * @param description human-readable description of trace or null if none.
   * @param tableNames  names of database tables that contain data for this trace
   * @return trace index
   * @throws SQLException on failure
   */
  public int addTrace(URL url, Long beginTime, Long endTime, String description, List<String>
      tableNames)
      throws SQLException {
    String sql =
        new InsertQuery(getDbTable())
            .addColumn(columnTraceIndex.getDbColumn(), null)
            .addColumn(columnTraceUrl.getDbColumn(), url != null ? url.toString() : null)
            .addColumn(columnBeginTime.getDbColumn(), beginTime)
            .addColumn(columnEndTime.getDbColumn(), endTime)
            .addColumn(columnDescription.getDbColumn(), description)
            .addColumn(columnTraceTableNames.getDbColumn(),
                JsonCoder.getInstance().encodeList(tableNames))
            .validate().toString();
    Statement statement = getDatabase().createStatement();
    statement.executeUpdate(sql);

    ResultSet resultSet = statement.getGeneratedKeys();
    int rowid = resultSet.getInt(1);
    return rowid;
  }

  /**
   * Return the list of known traces.
   *
   * @return list of traces
   * @throws SQLException on failure
   */
  public List<TraceInfo> getTraceInfos() throws SQLException {
    List<TraceInfo> result = new ArrayList<>();

    String sql =
        new SelectQuery()
            .addFromTable(getDbTable())
            .addColumns(columnTraceUrl.getDbColumn(),
                columnBeginTime.getDbColumn(),
                columnEndTime.getDbColumn(),
                columnDescription.getDbColumn(),
                columnTraceTableNames.getDbColumn())
            .validate().toString();


    try (Statement statement = getDatabase().createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {
      while (resultSet.next()) {
        TraceInfo traceInfo = new TraceInfo();
        int traceIndex = resultSet.getRow();
        String traceUrl = resultSet.getString(1);
        long beginTime = resultSet.getLong(2);
        long endTime = resultSet.getLong(3);
        String description = resultSet.getString(4);
        List<String> traceTableNames = JsonCoder.getInstance().decodeList(resultSet.getString(5));

        traceInfo.setTraceId(Integer.toString(traceIndex));
        traceInfo.setTraceUrl(traceUrl);
        traceInfo.setBeginTime(beginTime);
        traceInfo.setEndTime(endTime);
        traceInfo.setDescription(description);
        traceInfo.setTableNames(traceTableNames);

        result.add(traceInfo);
      }
    }

    return result;
  }
}

