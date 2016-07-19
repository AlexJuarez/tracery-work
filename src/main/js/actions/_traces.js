// @flow

import type { Action } from './_createAction';
import createAction from './_createAction';
import * as actions from './_types';

export function loadTrace(): Action<*> {
  return createAction(actions.START_OPEN_TRACE_FLOW);
}
