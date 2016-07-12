// @flow

import type { Color } from '../../Color';

/** Interface for various implementations of RectangularMap cell rendering. */
export interface CellRenderer {
  addCell(position: { row: number, col: number}, fill: Color): void;
  render(): void;
}
