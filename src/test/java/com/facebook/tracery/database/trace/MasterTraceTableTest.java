package com.facebook.tracery.database.trace;

import com.facebook.tracery.database.Database;
import com.facebook.tracery.database.TableTest;

import java.sql.SQLException;

public class MasterTraceTableTest extends TableTest<MasterTraceTable> {
  @Override
  protected MasterTraceTable createTestTable(Database db) throws SQLException {
    MasterTraceTable testTable = new MasterTraceTable(db);
    testTable.dropIfExists();
    testTable.create();

    return testTable;
  }

  @Override
  protected String getExpectedSchema() {
    return "0|trace_idx|INTEGER|0|null|1\n"
        + "1|trace_url|TEXT_URL_NONE_VAL|0|null|0\n"
        + "2|begin_time|INT_TIME_US_VAL|0|null|0\n"
        + "3|end_time|INT_TIME_US_VAL|0|null|0\n"
        + "4|description|TEXT_MISC_NONE_VAL|0|null|0\n"
        + "5|tables|TEXT_ID_NONE_ARR|0|null|0\n";
  }
}
