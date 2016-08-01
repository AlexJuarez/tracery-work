// @flow

import type { ViewState } from './_view';
import view from './_view';
import type { Action } from '../actions';
import * as actions from '../actions';

type Views = {
  [id: number]: ViewState<any>;
}

export type UiState = {
  nextViewId: number,
  rootViewId: number,
  viewIds: Array<number>,
  views: Views,
}

export default function uiState(state?: UiState, action: Action<*>): UiState {
  if (!state) {
    const views = {};
    views[0] = view(undefined, actions.doNothing());
    return {
      nextViewId: 0,
      rootViewId: 0,
      viewIds: [0],
      views,
    };
  }

  switch (action.type) {
    // TODO: CHANGE_APP_MODE?
    case actions.START_OPEN_TRACE_FLOW:
    case actions.SHOW_FILE_LIST:
    case actions.START_HEATMAP_DEMO:
    case actions.START_SUMMARY_TABLE_DEMO: {
      const views = {};
      views[0] = view(undefined, action);

      return {
        nextViewId: 1,
        rootViewId: 0,
        viewIds: [0],
        views,
      };
    }
    default:
      return state;
  }
}
