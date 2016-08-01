// @flow

import invariant from 'invariant';

import { combineReducers } from 'redux';

import type { StatusCode } from '../api/FetchStatus';
import type { Cache } from './_cache';
import cache from './_cache';

import type { ViewType } from './viewType';
import type { UiState } from './_ui';
import uiState from './_ui';

export type State = {
  ui: UiState,
  cache: Cache,
}

export default combineReducers({ ui: uiState, cache });

export function getRootViewId(state: State): number {
  return state.ui.rootViewId;
}

export function getViewType(state: State): ViewType {
  return state.ui.views[getRootViewId(state)].viewType;
}

export function getQueryId(state: State): number {
  const queryId = state.ui.views[getRootViewId(state)].state.queryId;
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
