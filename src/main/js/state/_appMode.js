// @flow

import type { Action } from '../actions';
import * as actions from '../actions';

import type { AppMode } from './appMode';
import * as appModes from './appMode';

export default function appMode(state: AppMode = appModes.STARTUP, action: Action<*>): AppMode {
  switch (action.type) {
    case actions.START_OPEN_TRACE_FLOW:
      return appModes.SELECT_TRACE;
    case actions.START_HEATMAP_DEMO:
      return appModes.HEATMAP_DEMO;
    default:
      return state;
  }
}
