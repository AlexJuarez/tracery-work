// @flow

// TODO: Move this to api
import type { Action } from '../actions';
import * as actions from '../actions';

import type { FetchStatus } from '../api/FetchStatus';
import * as statusCodes from '../api/FetchStatus';

const DEFAULT_FETCH_STATUS: FetchStatus = {
  code: statusCodes.SUCCESS,
};

// TODO: Generic fetching action shape so this doesn't have to know about all fetching action types
export default function fetchStatus(
    state: FetchStatus = DEFAULT_FETCH_STATUS,
    action: Action<*>): FetchStatus {
  switch (action.type) {
    case actions.TRACES_TABLE_FETCH_BEGIN:
    case actions.START_OPEN_TRACE_FLOW: // TODO: NO!
      return {
        code: statusCodes.IN_PROGRESS,
      };
    case actions.TRACES_TABLE_FETCH_SUCCESS:
      return {
        code: statusCodes.SUCCESS,
      };
    case actions.TRACES_TABLE_FETCH_FAILURE:
      return {
        code: statusCodes.FAILURE,
        message: action.payload,
      };
    default:
      return state;
  }
}
