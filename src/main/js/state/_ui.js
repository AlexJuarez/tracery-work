// @flow

import type { AppMode } from './appMode';
import appMode from './_appMode';
import type { Action } from '../actions';

export type UiState = {
  appMode: AppMode,
  queryId?: number,
}

// Manually wrote this reducer rather than using combineReducers because
// combineReducers doesn't deal well with optional fields like queryId
export default function ui(state?: UiState, action: Action<*>): UiState {
  const stateAppMode = state ? state.appMode : undefined;

  switch (action.type) {
    default: {
      let queryId = state ? state.queryId : undefined;
      if (action.payload && 'queryId' in action.payload) {
        queryId = action.payload.queryId;
      }

      return {
        appMode: appMode(stateAppMode, action),
        queryId,
      };
    }
  }
}
