// @flow

import type { Color } from '../../Color';

/** Coordinates and sizes  might be dips or pixels; see docs at the point of use for which. */
export type Props = {
  x: number,
  y: number,
  width: number,
  height: number,
  cellWidth: number,
  cellHeight: number,
  colors: Array<Color>,
  /** This array is allowed be jagged. */
  cells: Array<Array<?number>>,
  stroke?: Color,
  /** No other properties */
  [key: string]: void,
}
