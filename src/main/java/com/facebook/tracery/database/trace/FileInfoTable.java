package com.facebook.tracery.database.trace;

import com.facebook.tracery.database.Database;
import com.facebook.tracery.thrift.FileInfo;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Store for file information for files referenced by traces.
 */
public class FileInfoTable extends AbstractTraceTable {
  public static final String TRACE_INDEX_COLUMN_NAME = "trace_idx";
  public static final String FILE_NAME_COLUMN_NAME = "file_name";
  public static final String FILE_SIZE_COLUMN_NAME = "file_size";
  public static final String INODE_COLUMN_NAME = "inode";

  private final DbColumn dbColumnTraceIndex;
  private final DbColumn dbColumnFileName;
  private final DbColumn dbColumnFileSize;
  private final DbColumn dbColumnInode;

  public FileInfoTable(Database db) {
    super(db);

    dbColumnTraceIndex = dbTable.addColumn(TRACE_INDEX_COLUMN_NAME, ColumnAffinity.INT.name(),
        null);
    dbTable.foreignKey("[Master trace table trace ID column constraint.]",
        new String[] {dbColumnTraceIndex.getName()},
        MasterTraceTable.TABLE_NAME,
        new String[] {MasterTraceTable.TRACE_INDEX_COLUMN_NAME});

    dbColumnFileName = dbTable.addColumn(FILE_NAME_COLUMN_NAME, ColumnAffinity.TEXT.name(), null);
    dbColumnFileSize = dbTable.addColumn(FILE_SIZE_COLUMN_NAME, ColumnAffinity.INT.name(), null);
    dbColumnInode = dbTable.addColumn(INODE_COLUMN_NAME, ColumnAffinity.INT.name(), null);
  }

  public DbColumn getTraceIndexColumn() {
    return dbColumnTraceIndex;
  }

  public DbColumn getFileNameColumn() {
    return dbColumnFileName;
  }

  public DbColumn getFileSizeColumn() {
    return dbColumnFileSize;
  }

  public DbColumn getInodeColumn() {
    return dbColumnInode;
  }

  public int indexOfFile(int traceIndex, String fileName) throws SQLException {
    String sql =
        new SelectQuery()
            .addColumns(dbColumnTraceIndex)
            .addCondition(BinaryCondition.equalTo(dbColumnTraceIndex, traceIndex))
            .addCondition(BinaryCondition.equalTo(dbColumnFileName, fileName))
            .validate().toString();
    try (Statement statement = db.createStatement();
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
        new InsertQuery(dbTable)
            .addColumn(dbColumnTraceIndex, traceIndex)
            .addColumn(dbColumnFileName, fileName)
            .addColumn(dbColumnFileSize, fileSize)
            .addColumn(dbColumnInode, inode)
            .validate().toString();
    statement.addBatch(insertQuery);
  }

  /**
   * Return the list of known files.
   *
   * @return file list
   */
  public List<FileInfo> getFileInfos() throws SQLException {
    List<FileInfo> result = new ArrayList<>();

    String sql =
        new SelectQuery()
            .addFromTable(dbTable)
            .addColumns(dbColumnTraceIndex, dbColumnFileName, dbColumnFileSize, dbColumnInode)
            .validate().toString();

    try (Statement statement = db.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {
      while (resultSet.next()) {
        FileInfo fileInfo = new FileInfo();
        int traceIndex = resultSet.getInt(1);
        String fileName = resultSet.getString(2);
        long fileSize = resultSet.getLong(3);
        long inode = resultSet.getLong(4);

        fileInfo.setTraceId(Integer.toString(traceIndex));
        fileInfo.setFileName(fileName);
        fileInfo.setFileSize(fileSize);
        fileInfo.setInode(inode);

        result.add(fileInfo);
      }
    }

    return result;
  }
}
