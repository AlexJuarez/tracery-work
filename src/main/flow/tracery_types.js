// IMPORTANT!
// This declaration is manually ported from the TypeScript declaration created by thrift at
// build/generated-sources/thrift/gen-js/tracery_types.d.ts. Whenever updating the Thrift schema
// for any types defined in this file, one should re-port the new version. (Porting is not that
// hard, so it's likely better to just re-port it than to try to determine exactly the changes
// required.)
//
// @flow

declare module tracery_types {
  declare class TraceInfo {
    traceId: string;
    traceUrl: string;
    tableNames: Array<string>;
    beginTime: number;
    endTime: number;
    description: string;

    constructor(args?: {
      traceId: string,
      traceUrl: string,
      tableNames: Array<string>,
      beginTime?: number,
      endTime?: number,
      description?: string
    }): void;
  }
}
