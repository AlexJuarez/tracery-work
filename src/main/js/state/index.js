// @flow

import { combineReducers } from 'redux';

import type { AppMode } from './appMode';
import appMode from './_appMode';

export type State = {
  appMode: AppMode,
}

export default combineReducers({ appMode });

export function getAppMode(state: State): AppMode {
  return state.appMode;
}
