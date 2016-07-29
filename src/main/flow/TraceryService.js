// IMPORTANT!
// This declaration is manually ported from the TypeScript declaration created by thrift at
// build/generated-sources/thrift/gen-js/TraceryService.d.ts. Whenever updating the Thrift schema
// for any types defined in this file, one should re-port the new version. (Porting is not that
// hard, so it's likely better to just re-port it than to try to determine exactly the changes
// required.)
//
// @flow

import Thrift from 'thrift';
import { TraceInfo } from 'tracery_types';
import { TableInfo } from 'table_types';
import { Query, QueryResult } from 'query_types';

declare module TraceryService {
  declare class TraceryServiceClient {
    input: Thrift.TJSONProtocol;
    output: Thrift.TJSONProtocol;
    seqid: number;

    constructor(input: Thrift.TJSONProtocol, output?: Thrift.TJSONProtocol): void;

    getTraces(): Array<TraceInfo>;

    getTraces(callback: Function): void;

    getTable(tableName: string): TableInfo;

    getTable(tableName: string, callback: Function): void;

    query(query: Query): QueryResult;

    query(query: Query, callback: Function): void;
  }
}
