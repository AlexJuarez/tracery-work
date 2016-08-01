// @flow

import type { ViewType } from './viewType';
import viewType from './_viewType';
import type { Action } from '../actions';

export type UiState = {
  viewType: ViewType,
  queryId?: number,
}

// Manually wrote this reducer rather than using combineReducers because
// combineReducers doesn't deal well with optional fields like queryId
export default function ui(state?: UiState, action: Action<*>): UiState {
  const stateViewType = state ? state.viewType : undefined;

  switch (action.type) {
    default: {
      let queryId = state ? state.queryId : undefined;
      if (action.payload && 'queryId' in action.payload) {
        queryId = action.payload.queryId;
      }

      return {
        viewType: viewType(stateViewType, action),
        queryId,
      };
    }
  }
}
