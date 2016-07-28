package com.facebook.tracery.database.trace;

import com.facebook.tracery.database.Database;
import com.facebook.tracery.database.TableTest;

import java.sql.SQLException;

public class FileInfoTableTest extends TableTest<FileInfoTable> {
  @Override
  protected FileInfoTable createTestTable(Database db) throws SQLException {
    // FileInfoTable has foreign keys from MasterTraceTable
    MasterTraceTable masterTraceTable = new MasterTraceTable(db);
    masterTraceTable.dropIfExists();
    masterTraceTable.create();

    FileInfoTable testTable = new FileInfoTable(db);
    testTable.dropIfExists();
    testTable.create();

    return testTable;
  }

  @Override
  protected String getExpectedSchema() {
    return "0|trace_idx|INT_ID_NONE_VAL|0|null|0\n"
        + "1|file_name|TEXT_PATH_NONE_VAL|0|null|0\n"
        + "2|file_size|INT_QNT_BYTES_VAL|0|null|0\n"
        + "3|inode|INT_ID_NONE_VAL|0|null|0\n";
  }
}
