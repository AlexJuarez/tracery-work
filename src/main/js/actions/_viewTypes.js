// @flow

import createAction from './_createAction';
import type { Action } from './_createAction';
import * as actions from './_types';

export function startHeatmapDemo(): Action<*> {
  return createAction(actions.START_HEATMAP_DEMO);
}

export function startSummaryTableDemo(): Action<*> {
  return createAction(actions.START_SUMMARY_TABLE_DEMO);
}
