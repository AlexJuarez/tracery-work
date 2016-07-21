package com.facebook.tracery.service;

import com.facebook.tracery.database.Database;
import com.facebook.tracery.database.Table;
import com.facebook.tracery.database.trace.MasterTraceTable;
import com.facebook.tracery.thrift.TraceInfo;
import com.facebook.tracery.thrift.TraceryService;
import com.facebook.tracery.thrift.query.Query;
import com.facebook.tracery.thrift.query.QueryResult;
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
    try {
      MasterTraceTable masterTraceTable = db.getTableByClass(MasterTraceTable.class);
      return masterTraceTable.getTraceInfos();
    } catch (Exception ex) {
      throw new TException(ex);
    }
  }

  @Override
  public TableInfo getTable(String tableName) throws TException {
    try {
      Table table = db.getTableByName(tableName);
      return table.getTableInfo();
    } catch (Exception ex) {
      throw new TException(ex);
    }
  }

  @Override
  public QueryResult query(Query select) throws TException {
    try {
      return db.doQuery(select);
    } catch (Exception ex) {
      throw new TException(ex);
    }
  }
}
