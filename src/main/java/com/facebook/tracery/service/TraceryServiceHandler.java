package com.facebook.tracery.service;

import com.facebook.tracery.database.Database;
import com.facebook.tracery.database.trace.FileInfoTable;
import com.facebook.tracery.database.trace.MasterTraceTable;
import com.facebook.tracery.thrift.FileInfo;
import com.facebook.tracery.thrift.TraceInfo;
import com.facebook.tracery.thrift.TraceryService;
import org.apache.thrift.TException;

import java.util.List;

public class TraceryServiceHandler implements TraceryService.Iface {
  private final Database db;

  public TraceryServiceHandler(Database db) {
    this.db = db;
  }

  @Override
  public List<TraceInfo> getTraces() throws TException {
    MasterTraceTable masterTraceTable = new MasterTraceTable(db);
    try {
      return masterTraceTable.getTraceInfos();
    } catch (Exception ex) {
      throw new TException(ex);
    }
  }

  @Override
  public List<FileInfo> getFiles() throws TException {
    FileInfoTable fileInfoTable = new FileInfoTable(db);
    try {
      return fileInfoTable.getFileInfos();
    } catch (Exception ex) {
      throw new TException(ex);
    }
  }
}
