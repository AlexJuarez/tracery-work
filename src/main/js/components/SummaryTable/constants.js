/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import { List, Map } from 'immutable';

export type Column = { title: string, order: number };
export type ColumnWidths = List<number>;
export type Headers = List<Column>;
export type OnResize = (
  widths: Array<number>, rowHeight: number, maxRows: number, tableBodyHeight: number,
) => void;
export type Rows = Map<string, List<*>>;

export const INITIAL_ROW_MAX = 50;
export const ROW_INITIAL_HEIGHT = 22;
export const SCROLL_ROW_LEAD = 20;
export const ON_RESIZE_ROW_COUNT = 50;
export const RESIZE_REFRESH_RATE = 33;
export const SCROLLING_REFRESH_RATE = 16;

// Scroll Directions
export type ScrollDirections = 'up' | 'down';
export const SCROLL_DIRECTIONS = {
  DOWN: 'down',
  UP: 'up',
};
