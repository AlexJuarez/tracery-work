include "type.thrift"

namespace java com.facebook.tracery.thrift.table

enum RawType {
  BOOL,
  INT,
  FLOAT,
  STRING,
  BINARY
}

enum Category {
  OTHER,
  QUANTITY,
  ID,
  DURATION,
  TIMESTAMP,
  PATH,
  URL
}

enum Unit {
  NONE,
  PERCENT,
  BYTES,
  SECONDS,
  MILLISECONDS,
  MICROSECONDS,
  NANOSECONDS
}

enum Structure {
  SCALAR,
  ARRAY
}

struct TableColumnType {
  1: required RawType rawType;
  2: required Category category;
  3: required Unit unit;
  4: required Structure structure;
}

struct TableColumnInfo {
  1: required string name;
  2: required TableColumnType type;
  4: optional string description;
}

struct TableInfo {
  1: required string name;
  2: required list<TableColumnInfo> columns;
  3: required i32 rowCount;
}
