// @flow

import type { Dispatch } from 'redux';

import Thrift from 'thrift';
import { TraceryServiceClient } from 'TraceryService';
import { TraceInfo } from 'tracery_types';

import createAction from './_createAction';
import * as actions from './_types';

// TODO: Build a middleware to handle fetching data from the server and shoving it in the cache,
// such that we can just send an action of a particular shape and have all this stuff happen
// without having to write a bunch of new code every time
// TODO: Use normalizr for this
export function loadTraceList(): any {  // FlowFixMe: Need a good Thunk return type
  return (dispatch: Dispatch) => {
    dispatch(createAction(actions.START_OPEN_TRACE_FLOW));
    dispatch(createAction(actions.TRACES_TABLE_FETCH_BEGIN));
    fetchAsync().then((result: Array<TraceInfo>) => {
      const ids = result.map((traceInfo: TraceInfo): string => traceInfo.traceId);
      const rows = {};

      result.forEach((traceInfo: TraceInfo) => {
        rows[traceInfo.traceId] = traceInfo;
      });

      dispatch(createAction(actions.TRACES_TABLE_FETCH_SUCCESS, {
        rows,
        ids,
        lastFetchStatus: {
          status: 'Success',
        },
      }));
    });
  };
}

function fetchAsync(): Promise<Array<TraceInfo>> {
  return new Promise(fetch);
}

function fetch(resolve: (result: Promise<Array<TraceInfo>> | Array<TraceInfo>) => void) {
  const transport = new Thrift.Transport('http://localhost:9090/api');
  const protocol = new Thrift.TJSONProtocol(transport);
  const client: TraceryServiceClient = new TraceryServiceClient(protocol);

  client.getTraces((result: any): void => resolve(result));
}
