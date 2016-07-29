// IMPORTANT!
// This declaration is manually ported from the TypeScript declaration created by thrift at
// build/generated-sources/thrift/gen-js/table_types.d.ts. Whenever updating the Thrift schema
// for any types defined in this file, one should re-port the new version. (Porting is not that
// hard, so it's likely better to just re-port it than to try to determine exactly the changes
// required.)
//
// @flow

declare module table_types {
  declare var RawType:{
    NULL: number;
    BOOL: number;
    INT: number;
    FLOAT: number;
    STRING: number;
    BINARY: number;
  }

  declare var Category: {
    OTHER: number;
    QUANTITY: number;
    ID: number;
    DURATION: number;
    TIMESTAMP: number;
    PATH: number;
    URL: number;
  }

  declare var Unit: {
    NONE: number;
    PERCENT: number;
    BYTES: number;
    SECONDS: number;
    MILLISECONDS: number;
    MICROSECONDS: number;
    NANOSECONDS: number;
  }

  declare var Structure: {
    SCALAR: number;
    ARRAY: number;
  }

  declare class TableColumnType {
    rawType: number;
    category: number;
    unit: number;
    structure: number;

    constructor(args?: { rawType: number, category: number, unit: number, structure: number }): void;
  }

  declare class TableColumnInfo {
    name: string;
    type: TableColumnType;
    description: string;

    constructor(args?: { name: string, type: TableColumnType, description?: string }): void;
  }

  declare class TableInfo {
    name: string;
    columns: Array<TableColumnInfo>;
    rowCount: number;

    constructor(args?: { name: string, columns: Array<TableColumnInfo>, rowCount: number }): void;
  }
}
