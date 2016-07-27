/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import { Record } from 'immutable';

import { HEADER_CELL_MIN_WIDTH } from '../constants';

export default class Header extends Record({
  title: '',
  order: 0,
  width: 0,
  customWidth: false,
  minWidth: HEADER_CELL_MIN_WIDTH,
}) {
  title: string;
  order: number;
  width: number;
  minWidth: number;
  customWidth: boolean;
}
