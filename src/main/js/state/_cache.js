// @flow

import { combineReducers } from 'redux';

import type { TraceInfosTable } from './_traceInfosTable';
import traceInfos from './_traceInfosTable';
import type { Queries } from './_queries';
import queries from './_queries';

// TODO: Eventually the shape of the cache will be determined by what tables and queries have been
// defined in the app's configuration.
export type Cache = {
  traceInfos: TraceInfosTable,
  queries: Queries,
}

export default combineReducers({ queries, traceInfos });
