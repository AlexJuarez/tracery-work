package com.facebook.tracery.database.trace;

import com.facebook.tracery.database.Database;
import com.facebook.tracery.parse.diskio.DiskTraceItem;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Store for disk I/O trace data.
 */
public class DiskPhysOpTable extends AbstractTraceTable {
  public static final String TRACE_INDEX_COLUMN_NAME = "trace_idx";
  public static final String BEGIN_TIME_COLUMN_NAME = "begin_time";
  public static final String END_TIME_COLUMN_NAME = "end_time";
  public static final String EVENT_TYPE_COLUMN_NAME = "event_type";
  public static final String THREAD_NAME_COLUMN_NAME = "thread_name";
  public static final String CPU_COLUMN_NAME = "cpu";
  public static final String FILE_NAME_COLUMN_NAME = "file_name";
  public static final String PAGE_COUNT_COLUMN_NAME = "page_count";
  public static final String PAGES_COLUMN_NAME = "pages";
  public static final String SECTORS_COLUMN_NAME = "sectors";

  private final DbColumn dbColumnTraceIndex;
  private final DbColumn dbColumnBeginTime;
  private final DbColumn dbColumnEndTime;
  private final DbColumn dbColumnEventType;
  private final DbColumn dbColumnThreadName;
  private final DbColumn dbColumnCpu;
  private final DbColumn dbColumnFileName;
  private final DbColumn dbColumnPageCount;
  private final DbColumn dbColumnPages;
  private final DbColumn dbColumnSectors;

  public DiskPhysOpTable(Database db) {
    super(db);

    dbColumnTraceIndex = dbTable.addColumn(TRACE_INDEX_COLUMN_NAME, ColumnAffinity.INT.name(),
        null);
    dbTable.foreignKey("[Master trace table trace ID column constraint.]",
        new String[] {dbColumnTraceIndex.getName()},
        MasterTraceTable.TABLE_NAME,
        new String[] {MasterTraceTable.TRACE_INDEX_COLUMN_NAME});

    dbColumnBeginTime = dbTable.addColumn(BEGIN_TIME_COLUMN_NAME, ColumnAffinity.INT.name(), null);
    dbColumnEndTime = dbTable.addColumn(END_TIME_COLUMN_NAME, ColumnAffinity.INT.name(), null);
    dbColumnEventType = dbTable.addColumn(EVENT_TYPE_COLUMN_NAME, ColumnAffinity.TEXT.name(), null);
    dbColumnThreadName = dbTable.addColumn(THREAD_NAME_COLUMN_NAME, ColumnAffinity.TEXT.name(),
        null);
    dbColumnCpu = dbTable.addColumn(CPU_COLUMN_NAME, ColumnAffinity.INT.name(), null);
    dbColumnFileName = dbTable.addColumn(FILE_NAME_COLUMN_NAME, ColumnAffinity.TEXT.name(), null);
    dbColumnPageCount = dbTable.addColumn(PAGE_COUNT_COLUMN_NAME, ColumnAffinity.INT.name(), null);
    dbColumnPages = dbTable.addColumn(PAGES_COLUMN_NAME, ColumnAffinity.TEXT.name(), null);
    dbColumnSectors = dbTable.addColumn(SECTORS_COLUMN_NAME, ColumnAffinity.TEXT.name(), null);
  }

  public DbColumn getTraceIndexColumn() {
    return dbColumnTraceIndex;
  }

  public DbColumn getBeginTimeColumn() {
    return dbColumnBeginTime;
  }

  public DbColumn getEndTimeColumn() {
    return dbColumnEndTime;
  }

  public DbColumn getEventTypeColumn() {
    return dbColumnEventType;
  }

  public DbColumn getThreadNameColumn() {
    return dbColumnThreadName;
  }

  public DbColumn getCpuColumn() {
    return dbColumnCpu;
  }

  public DbColumn getFileNameColumn() {
    return dbColumnFileName;
  }

  public DbColumn getPageCountColumn() {
    return dbColumnPageCount;
  }

  public DbColumn getPagesColumn() {
    return dbColumnPages;
  }

  public DbColumn getSectorsColumn() {
    return dbColumnSectors;
  }

  public void insertBatch(Statement statement, int traceIdx, DiskTraceItem item) throws
      SQLException {
    String insertQuery =
        new InsertQuery(dbTable)
            .addColumn(dbColumnTraceIndex, traceIdx)
            .addColumn(dbColumnBeginTime, Double.valueOf(1.0E6 * item.getTimestamp()).longValue())
            .addColumn(dbColumnEndTime, Double.valueOf(1.0E6 * (item.getTimestamp() + item
                .getDuration())).longValue())
            .addColumn(dbColumnEventType, item.getType())
            .addColumn(dbColumnThreadName, item.getThreadName())
            .addColumn(dbColumnCpu, item.getCpu())
            .addColumn(dbColumnFileName, item.getFileName())
            .addColumn(dbColumnPageCount, item.getPageCount())
            .addColumn(dbColumnPages, Arrays.toString(item.getPages())) // FIXME: array encoding
            .addColumn(dbColumnSectors, Arrays.toString(item.getSectors())) // FIXME: array encoding
            .validate().toString();
    statement.addBatch(insertQuery);
  }
}
