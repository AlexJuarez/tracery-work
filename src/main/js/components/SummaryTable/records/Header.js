/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import { Record } from 'immutable';

import { HEADER_CELL_MIN_WIDTH } from '../constants';

export default class Header extends Record({
  key: 0,
  minWidth: HEADER_CELL_MIN_WIDTH,
  order: 0,
  title: '',
  width: null,
}) {
  key: number;
  minWidth: number;
  order: number;
  title: string;
  width: ?number;
}
