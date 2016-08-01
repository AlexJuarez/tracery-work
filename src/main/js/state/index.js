// @flow

import invariant from 'invariant';

import { combineReducers } from 'redux';

import type { StatusCode } from '../api/FetchStatus';
import type { Cache } from './_cache';
import cache from './_cache';

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

export function getQueryId(state: State): number {
  const queryId = state.ui.queryId;
  invariant(queryId !== undefined && queryId != null, 'Expected a queryId.');

  return queryId;
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
