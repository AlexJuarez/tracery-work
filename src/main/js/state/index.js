// @flow

import { combineReducers } from 'redux';

import type { StatusCode } from '../api/FetchStatus';
import type { Cache } from './_cache';
import cache from './_cache';
import * as fromTraceInfos from './_traceInfosTable';

import type { AppMode } from './appMode';
import type { UiState } from './_ui';
import uiState from './_ui';

export type State = {
  ui: UiState,
  cache: Cache,
}

export default combineReducers({ ui: uiState, cache });

export function getAppMode(state: State): AppMode {
  return state.ui.appMode;
}

export function getTraceUrls(state: State): Array<string> {
  return fromTraceInfos.getUrls(state.cache.traceInfos);
}

// TODO: Need a generic fetch status mechanism
export function getLastTracesTableFetchStatusCode(state: State): StatusCode {
  return state.cache.traceInfos.lastFetchStatus.code;
}

export function getQueryRows(state: State, queryId: number): Array<Array<string>> {
  return state.cache.queries.items[queryId].rows;
}

export function getQueryFetchStatusCode(state: State, queryId: number): StatusCode {
  return state.cache.queries.items[queryId].fetchStatus.code;
}

export function getQueryFetchErrorMessage(state: State, queryId: number): ?string {
  return state.cache.queries.items[queryId].fetchStatus.message;
}

export function getNumQueries(state: State): number {
  return state.cache.queries.numQueries;
}
