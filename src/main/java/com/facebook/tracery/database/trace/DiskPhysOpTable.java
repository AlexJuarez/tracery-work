package com.facebook.tracery.database.trace;

import com.facebook.tracery.database.Column;
import com.facebook.tracery.database.Database;
import com.facebook.tracery.database.JsonCoder;
import com.facebook.tracery.database.Table;
import com.facebook.tracery.parse.diskio.DiskTraceItem;
import com.healthmarketscience.sqlbuilder.InsertQuery;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Store for disk I/O trace data.
 */
public class DiskPhysOpTable extends Table {
  public static final String TRACE_INDEX_COLUMN_NAME = "trace_idx";
  public static final String BEGIN_TIME_COLUMN_NAME = "begin_time";
  public static final String END_TIME_COLUMN_NAME = "end_time";
  public static final String FILE_OP_COLUMN_NAME = "file_op";
  public static final String THREAD_NAME_COLUMN_NAME = "thread_name";
  public static final String CPU_COLUMN_NAME = "cpu";
  public static final String FILE_NAME_COLUMN_NAME = "file_name";
  public static final String PAGE_COUNT_COLUMN_NAME = "page_count";
  public static final String PAGES_COLUMN_NAME = "pages";
  public static final String SECTORS_COLUMN_NAME = "sectors";

  private Column columnTraceIndex;
  private Column columnBeginTime;
  private Column columnEndTime;
  private Column columnFileOp;
  private Column columnThreadName;
  private Column columnCpu;
  private Column columnFileName;
  private Column columnPageCount;
  private Column columnPages;
  private Column columnSectors;

  public DiskPhysOpTable(Database db) throws SQLException {
    super(db);
  }

  public Column getTraceIndexColumn() {
    return columnTraceIndex;
  }

  public Column getBeginTimeColumn() {
    return columnBeginTime;
  }

  public Column getEndTimeColumn() {
    return columnEndTime;
  }

  public Column getEventTypeColumn() {
    return columnFileOp;
  }

  public Column getThreadNameColumn() {
    return columnThreadName;
  }

  public Column getCpuColumn() {
    return columnCpu;
  }

  public Column getFileNameColumn() {
    return columnFileName;
  }

  public Column getPageCountColumn() {
    return columnPageCount;
  }

  public Column getPagesColumn() {
    return columnPages;
  }

  public Column getSectorsColumn() {
    return columnSectors;
  }

  @Override
  protected void setupColumns() {
    columnTraceIndex = addColumn(TRACE_INDEX_COLUMN_NAME, Column.INDEX_COLUMN_TYPE);
    columnTraceIndex.addForeignKeyConstraint("[Master trace table id column constraint.]",
        Table.getTableName(MasterTraceTable.class),
        MasterTraceTable.TRACE_INDEX_COLUMN_NAME);

    columnBeginTime = addColumn(BEGIN_TIME_COLUMN_NAME, Column.TIMESTAMP_COLUMN_TYPE);
    columnEndTime = addColumn(END_TIME_COLUMN_NAME, Column.TIMESTAMP_COLUMN_TYPE);
    columnFileOp = addColumn(FILE_OP_COLUMN_NAME, Column.NAME_COLUMN_TYPE);
    columnThreadName = addColumn(THREAD_NAME_COLUMN_NAME, Column.NAME_COLUMN_TYPE);
    columnCpu = addColumn(CPU_COLUMN_NAME, Column.ID_COLUMN_TYPE);
    columnFileName = addColumn(FILE_NAME_COLUMN_NAME, Column.PATH_COLUMN_TYPE);
    columnPageCount = addColumn(PAGE_COUNT_COLUMN_NAME, Column.COUNT_COLUMN_TYPE);
    columnPages = addColumn(PAGES_COLUMN_NAME, Column.ID_ARRAY_COLUMN_TYPE);
    columnSectors = addColumn(SECTORS_COLUMN_NAME, Column.ID_ARRAY_COLUMN_TYPE);
  }

  public void insertBatch(Statement statement, int traceIdx, DiskTraceItem item) throws
      SQLException {
    String insertQuery =
        new InsertQuery(getDbTable())
            .addColumn(columnTraceIndex.getDbColumn(), traceIdx)
            .addColumn(columnBeginTime.getDbColumn(),
                Double.valueOf(1.0E6 * item.getTimestamp()).longValue())
            .addColumn(columnEndTime.getDbColumn(),
                Double.valueOf(1.0E6 * (item.getTimestamp() + item.getDuration())).longValue())
            .addColumn(columnFileOp.getDbColumn(), item.getType())
            .addColumn(columnThreadName.getDbColumn(), item.getThreadName())
            .addColumn(columnCpu.getDbColumn(), item.getCpu())
            .addColumn(columnFileName.getDbColumn(), item.getFileName())
            .addColumn(columnPageCount.getDbColumn(), item.getPageCount())
            .addColumn(columnPages.getDbColumn(),
                JsonCoder.getInstance().encodeList(item.getPages()))
            .addColumn(columnSectors.getDbColumn(),
                JsonCoder.getInstance().encodeList(item.getSectors()))
            .validate().toString();
    statement.addBatch(insertQuery);
  }
}
