// @flow

// TODO: Basically this whole file should be parameterized by table

import invariant from 'invariant';
import { combineReducers } from 'redux';

import type { Action } from '../actions';
import * as actions from '../actions';
import type { TraceInfo } from 'tracery_types';
import type { FetchStatus } from '../api/FetchStatus';
import fetchStatus from './_fetchStatus';

type Ids = Array<string>;
type Rows = {
  [id: string]: TraceInfo,
}

export type TraceInfosTable = {
  ids: Ids,
  rows: Rows,
  lastFetchStatus: FetchStatus,
}

function rows(state: Rows = {}, action: Action<*>): Rows {
  switch (action.type) {
    case actions.TRACES_TABLE_FETCH_SUCCESS:
      invariant(action.payload, 'Expected an action with a payload');
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
      invariant(action.payload, 'Expected an action with a payload');
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
  return state.ids.map((id: string): TraceInfo => state.rows[id]);
}

export function getUrls(state: TraceInfosTable): Array<string> {
  return getTraceInfos(state).map((info: TraceInfo): string => info.traceUrl);
}
