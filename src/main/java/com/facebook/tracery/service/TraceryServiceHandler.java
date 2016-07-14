package com.facebook.tracery.service;

import com.facebook.tracery.database.Database;
import com.facebook.tracery.database.Table;
import com.facebook.tracery.database.trace.MasterTraceTable;
import com.facebook.tracery.thrift.TraceInfo;
import com.facebook.tracery.thrift.TraceryService;
import com.facebook.tracery.thrift.table.TableInfo;
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
  public TableInfo getTable(String tableName) throws TException {
    try {
      Table table = Table.createTable(db, tableName);
      return table.getTableInfo();
    } catch (Exception ex) {
      throw new TException(ex);
    }
  }
}
