/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import { Record } from 'immutable';

import { SCROLL_DIRECTIONS } from './../constants';
import type { ScrollDirections } from './../constants';

/**
 * ScrollState represents the current scrolling state
 * with offset being the last offset reported
 * and direction based on the change from that offset.
 */

export default class ScrollState extends Record({
  offset: 0,
  direction: SCROLL_DIRECTIONS.DOWN,
}) {
  /**
   * Offset in rows from the top. [0, maxrows)
   */
  offset: number;
  /**
   * Scrolling momentum, the direction we think the user is scrolling
   */
  direction: ScrollDirections;

  setOffset(offset: number): ScrollState {
    if (this.offset < offset) {
      return this.merge({
        offset,
        direction: SCROLL_DIRECTIONS.DOWN,
      });
    }

    if (this.offset > offset) {
      return this.merge({
        offset,
        direction: SCROLL_DIRECTIONS.UP,
      });
    }

    return this;
  }
}
