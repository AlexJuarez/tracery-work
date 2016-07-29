package com.facebook.tracery.database.trace;

import com.facebook.tracery.database.Column;
import com.facebook.tracery.database.ColumnType;
import com.facebook.tracery.database.Database;
import com.facebook.tracery.database.Table;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Store for file information for files referenced by traces.
 */
public class FileInfoTable extends Table {
  public static final String TRACE_INDEX_COLUMN_NAME = "trace_idx";
  public static final String FILE_NAME_COLUMN_NAME = "file_name";
  public static final String FILE_SIZE_COLUMN_NAME = "file_size";
  public static final String INODE_COLUMN_NAME = "inode";

  private Column columnTraceIndex;
  private Column columnFileName;
  private Column columnFileSize;
  private Column columnInode;

  public FileInfoTable(Database db) throws SQLException {
    super(db);
  }


  public Column getTraceIndexColumn() {
    return columnTraceIndex;
  }

  public Column getFileNameColumn() {
    return columnFileName;
  }

  public Column getFileSizeColumn() {
    return columnFileSize;
  }

  public Column getInodeColumn() {
    return columnInode;
  }

  @Override
  protected void setupColumns() {
    columnTraceIndex = addColumn(TRACE_INDEX_COLUMN_NAME, ColumnType.INDEX_COLUMN_TYPE);
    columnTraceIndex.addForeignKeyConstraint("[Master trace table id column constraint.]",
        Table.getTableName(MasterTraceTable.class),
        MasterTraceTable.TRACE_INDEX_COLUMN_NAME);

    columnFileName = addColumn(FILE_NAME_COLUMN_NAME, ColumnType.PATH_COLUMN_TYPE);
    columnFileSize = addColumn(FILE_SIZE_COLUMN_NAME, ColumnType.BYTES_COLUMN_TYPE);
    columnInode = addColumn(INODE_COLUMN_NAME, ColumnType.ID_COLUMN_TYPE);
  }

  public int indexOfFile(int traceIndex, String fileName) throws SQLException {
    String sql =
        new SelectQuery()
            .addFromTable(getDbTable())
            .addColumns(columnTraceIndex.getDbColumn())
            .addCondition(BinaryCondition.equalTo(columnTraceIndex.getDbColumn(), traceIndex))
            .addCondition(BinaryCondition.equalTo(columnFileName.getDbColumn(), fileName))
            .validate().toString();
    try (Statement statement = getDatabase().createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {
      int idx = -1;
      if (resultSet.next()) {
        idx = resultSet.getInt(1);
      }
      return idx;
    }
  }

  public void insertBatch(Statement statement, int traceIndex, String fileName, long fileSize,
                          long inode) throws SQLException {
    String insertQuery =
        new InsertQuery(getDbTable())
            .addColumn(columnTraceIndex.getDbColumn(), traceIndex)
            .addColumn(columnFileName.getDbColumn(), fileName)
            .addColumn(columnFileSize.getDbColumn(), fileSize)
            .addColumn(columnInode.getDbColumn(), inode)
            .validate().toString();
    statement.addBatch(insertQuery);
  }
}
