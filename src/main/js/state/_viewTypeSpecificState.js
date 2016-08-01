// @flow

import type { Action } from '../actions';

export type StatelessViewState = {
}

export type QueryBackedViewState = {
  queryId: number,
}

export type ViewTypeSpecificState =
  StatelessViewState |
  QueryBackedViewState;

export default function viewTypeSpecificState(
    state: ViewTypeSpecificState = {},
    action: Action<*>): ViewTypeSpecificState {
  switch (action.type) {
    default: {
      if (action.payload && 'queryId' in action.payload) {
        const queryId = action.payload.queryId;
        return {
          ...state,
          queryId,
        };
      }

      return state;
    }
  }
}
