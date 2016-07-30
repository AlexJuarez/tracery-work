// @flow

import invariant from 'invariant';

// TODO: Move this to api
import type { Action } from '../actions';
import * as actions from '../actions';

import type { FetchStatus } from '../api/FetchStatus';
import * as statusCodes from '../api/FetchStatus';

const DEFAULT_FETCH_STATUS: FetchStatus = {
  code: statusCodes.SUCCESS,
};

export default function fetchStatus(
    state: FetchStatus = DEFAULT_FETCH_STATUS,
    action: Action<*>): FetchStatus {
  switch (action.type) {
    case actions.QUERY_STARTED:
      return {
        code: statusCodes.IN_PROGRESS,
      };
    case actions.QUERY_FINISHED:
      invariant(action.payload, 'Expected a payload');
      return {
        code: action.error ? statusCodes.FAILURE : statusCodes.SUCCESS,
        message: action.error ? action.payload.message : undefined,
      };
    default:
      return state;
  }
}
