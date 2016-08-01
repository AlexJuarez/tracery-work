// @flow

import type { Action } from '../actions';
import * as actions from '../actions';

import type { ViewType } from './viewType';
import * as viewTypes from './viewType';

export default function viewType(state: ViewType = viewTypes.STARTUP, action: Action<*>): ViewType {
  switch (action.type) {
    case actions.START_OPEN_TRACE_FLOW:
      return viewTypes.SELECT_TRACE;
    case actions.SHOW_FILE_LIST:
      return viewTypes.SELECT_FILE;
    case actions.START_HEATMAP_DEMO:
      return viewTypes.HEATMAP_DEMO;
    case actions.START_SUMMARY_TABLE_DEMO:
      return viewTypes.SUMMARY_TABLE_DEMO;
    default:
      return state;
  }
}
