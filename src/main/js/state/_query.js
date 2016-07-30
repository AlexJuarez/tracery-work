// @flow

import invariant from 'invariant';
import { combineReducers } from 'redux';

import type { Action, QueryPayload, QueryFinishedSuccessPayload } from '../actions';
import * as actions from '../actions';
import type { FetchStatus } from '../api/FetchStatus';

import fetchStatus from './_fetchStatus';

export type Query = {
  id: number,
  fetchStatus: FetchStatus,
  rows: Array<Array<string>>,
};

function id(state: number = -1, action: Action<*>): number {
  switch (action.type) {
    case actions.QUERY_STARTED:
    case actions.QUERY_FINISHED: {
      invariant(action.payload, 'Expected a payload');
      const payload: QueryPayload = action.payload;
      return payload.id;
    }
    default:
      invariant(state, 'Expected an existing query');
      return state;
  }
}

function rows(state: Array<Array<string>> = [], action: Action<*>): Array<Array<string>> {
  switch (action.type) {
    case actions.QUERY_STARTED:
      return [];
    case actions.QUERY_FINISHED: {
      if (action.error) {
        invariant(state, 'Expected an existing query');
        return state;
      }
      invariant(action.payload, 'Expected a payload');
      const payload: QueryFinishedSuccessPayload = action.payload;
      return payload.rows;
    }
    default:
      invariant(state, 'Expected an existing query');
      return state;
  }
}

export default combineReducers({ id, fetchStatus, rows });
