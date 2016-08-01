package com.facebook.tracery.database.trace;

import com.facebook.tracery.database.Database;
import com.facebook.tracery.database.TableTest;

import java.sql.SQLException;

public class DiskPhysOpTableTest extends TableTest<DiskPhysOpTable> {
  @Override
  protected DiskPhysOpTable createTestTable(Database db) throws SQLException {
    // DiskPhysOpTable has foreign keys from MasterTraceTable
    MasterTraceTable masterTraceTable = new MasterTraceTable(db);
    masterTraceTable.dropIfExists();
    masterTraceTable.create();

    DiskPhysOpTable testTable = new DiskPhysOpTable(db);
    testTable.dropIfExists();
    testTable.create();

    return testTable;
  }

  @Override
  protected String getExpectedSchema() {
    return "0|trace_idx|INT_ID_NONE_VAL|0|null|0\n"
        + "1|begin_time|INT_TIME_US_VAL|0|null|0\n"
        + "2|end_time|INT_TIME_US_VAL|0|null|0\n"
        + "3|file_op|TEXT_ID_NONE_VAL|0|null|0\n"
        + "4|thread_name|TEXT_ID_NONE_VAL|0|null|0\n"
        + "5|cpu|INT_ID_NONE_VAL|0|null|0\n"
        + "6|file_name|TEXT_PATH_NONE_VAL|0|null|0\n"
        + "7|page_count|INT_QNT_NONE_VAL|0|null|0\n"
        + "8|pages|INT_ID_NONE_ARR|0|null|0\n"
        + "9|sectors|INT_ID_NONE_ARR|0|null|0\n";
  }
}
