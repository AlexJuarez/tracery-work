// @flow

/** Interface for various implementations of RectangularMap cell rendering. */
export interface CellRenderer {
  addCell(position: { row: number, col: number}, fillIndex: number): void;
  render(): void;
}
