// @flow

// TODO: Basically this whole file should be parameterized by table

import { combineReducers } from 'redux';

import type { Action } from '../actions';
import * as actions from '../actions';
import type { TraceId, TraceInfo } from '../api/TraceInfo';
import type { FetchStatus, StatusCode } from '../api/FetchStatus';
import fetchStatus from './_fetchStatus';

type Ids = Array<TraceId>;
type Rows = {
  [id: TraceId]: TraceInfo,
}

export type TraceInfosTable = {
  ids: Ids,
  rows: Rows,
  lastFetchStatus: FetchStatus,
}

function rows(state: Rows = {}, action: Action<*>): Rows {
  switch (action.type) {
    case actions.TRACES_TABLE_FETCH_SUCCESS:
      return {
        ...state,
        ...action.payload.rows,
      };
    default:
      return state;
  }
}

function ids(state: Ids = [], action: Action<*>): Ids {
  switch (action.type) {
    case actions.TRACES_TABLE_FETCH_SUCCESS:
      return [
        ...state,
        ...action.payload.ids,
      ];
    default:
      return state;
  }
}

// TODO: Reuse all the code in this file for other types of tables
export default combineReducers({ ids, rows, lastFetchStatus: fetchStatus });

function getTraceInfos(state: TraceInfosTable): Array<TraceInfo> {
  return state.ids.map((id: TraceId): TraceInfo => state.rows[id]);
}

export function getUrls(state: TraceInfosTable): Array<string> {
  return getTraceInfos(state).map((info: TraceInfo): string => info.traceUrl);
}

export function getLastFetchStatusCode(state: TraceInfosTable): StatusCode {
  return state.lastFetchStatus.code;
}
