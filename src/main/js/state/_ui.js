// @flow

import { combineReducers } from 'redux';

import type { AppMode } from './appMode';
import appMode from './_appMode';

export type UiState = {
  appMode: AppMode,
}

export default combineReducers({ appMode });
