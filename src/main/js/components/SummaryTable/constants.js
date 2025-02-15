/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import { List, Map } from 'immutable';
import { Header } from './';

export type Headers = List<Header>;
export type ColumnOrder = List<number>;
export type OnResize = (rowHeight: number, maxRows: number, tableBodyHeight: number) => void;
export type OnHeaderUpdate = (headers: Headers) => void;
export type Rows = Map<string, List<*>>;

export const INITIAL_ROW_MAX = 50;
export const ROW_INITIAL_HEIGHT = 22;
export const ON_RESIZE_ROW_COUNT = 50;
export const RESIZE_REFRESH_RATE = 33;
export const SCROLLING_REFRESH_RATE = 16;
export const HEADER_CELL_MIN_WIDTH = 30;

// Scroll Directions
export type ScrollDirections = 'up' | 'down';
export const SCROLL_DIRECTIONS = {
  DOWN: 'down',
  UP: 'up',
};
