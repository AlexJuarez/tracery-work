// @flow

import { combineReducers } from 'redux';

import type { StatusCode } from '../api/FetchStatus';
import type { Cache } from './_cache';
import cache from './_cache';
import * as fromTraceInfos from './_traceInfosTable';

import type { AppMode } from './appMode';
import appMode from './_appMode';

export type State = {
  appMode: AppMode,
  cache: Cache,
}

export default combineReducers({ appMode, cache });

export function getAppMode(state: State): AppMode {
  return state.appMode;
}

export function getTraceUrls(state: State): Array<string> {
  return fromTraceInfos.getUrls(state.cache.traceInfos);
}

// TODO: Need a generic fetch status mechanism
export function getLastTracesTableFetchStatusCode(state: State): StatusCode {
  return state.cache.traceInfos.lastFetchStatus.code;
}
