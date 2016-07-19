// @flow

import { combineReducers } from 'redux';

import type { TraceInfosTable } from './_traceInfosTable';
import traceInfos from './_traceInfosTable';

// TODO: Eventually the shape of the cache will be determined by what tables and queries have been
// defined in the app's configuration.
export type Cache = {
  traceInfos: TraceInfosTable,
}

export default combineReducers({ traceInfos });

export function getTraceInfos(state: Cache): TraceInfosTable {
  return state.traceInfos;
}
