include "type.thrift"
include "table.thrift"

namespace java com.facebook.tracery.thrift

struct TraceInfo {
  1: required string traceId;
  2: required string traceUrl;
  3: required list<string> tableNames;
  4: optional type.timestamp beginTime;
  5: optional type.timestamp endTime;
  6: optional string description; // human-readable description of trace content
  // TODO: add source machine info (hardware, software details)
}

service TraceryService {
  list<TraceInfo> getTraces();

  table.TableInfo getTable(1:string tableName);
}
