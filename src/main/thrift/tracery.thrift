include "type.thrift"

namespace java com.facebook.tracery.thrift

struct TraceInfo {
  1: required string traceId;
  2: required string traceUrl;
  3: optional type.timestamp beginTime;
  4: optional type.timestamp endTime;
  5: optional string description; // human-readable description of trace content
  // TODO: add source machine info (hardware, software details)
}

struct FileInfo {
  1: required string traceId;
  2: required string fileName;
  3: required i64 fileSize;
  4: required i64 inode;
}

service TraceryService {
  list<TraceInfo> getTraces();
  list<FileInfo> getFiles();
}
