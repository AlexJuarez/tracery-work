// @flow

import { combineReducers } from 'redux';

import type { ViewType } from './viewType';
import viewType from './_viewType';
import viewTypeSpecificState from './_viewTypeSpecificState';

export type ViewState<ViewTypeSpecificState> = {
  viewType: ViewType,
  state: ViewTypeSpecificState,
}

export default combineReducers({ viewType, state: viewTypeSpecificState });
