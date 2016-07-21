// @flow

import invariant from 'invariant';
import React from 'react';

import type { CanvasContextProvider } from '../CanvasContextProvider';
import type { CellRenderer } from './CellRenderer';
import { dipsToPixels } from '../pixels';
import SpriteCellRenderer from './SpriteCellRenderer';
import type { Props } from './Props';


/** Renders a map of same-sized filled rectangular cells, optionally stroking them as well. */
class RectangularMap extends React.Component {
  constructor(props: Props) {
    super(props);

    invariant(
      props.x >= 0 && props.y >= 0 && props.width >= 0 && props.height >= 0,
      'Size and position must be non-negative.');
    invariant(props.cellWidth > 0 && props.cellHeight > 0, 'Cell width must be positive.');
  }

  props: Props;

  _newCellRenderer(props: Props, context: CanvasRenderingContext2D): CellRenderer {
    return new SpriteCellRenderer(props, context);
  }

  renderToCanvas(provider: CanvasContextProvider) {
    const pixelProps = Object.assign({}, this.props, {
      x: Math.trunc(dipsToPixels(this.props.x)),
      y: Math.trunc(dipsToPixels(this.props.y)),
      width: Math.trunc(dipsToPixels(this.props.width)),
      height: Math.trunc(dipsToPixels(this.props.height)),
      cellWidth: Math.trunc(dipsToPixels(this.props.cellWidth)),
      cellHeight: Math.trunc(dipsToPixels(this.props.cellHeight)),
    });

    const cellRenderer = this._newCellRenderer(pixelProps, provider.getCanvasContext(this));

    this.props.cells.forEach((rowCells: Array<?number>, rowIndex: number): void =>
      this.renderRow(cellRenderer, rowIndex, rowCells));
    cellRenderer.render();
  }

  renderRow(cellRenderer: CellRenderer, rowIndex: number, rowCells: Array<?number>) {
    rowCells.forEach(
      (colorIndex: ?number, colIndex: number): void =>
        this.renderCell(
          cellRenderer,
          { rowIndex, colIndex, colorIndex }));
  }

  renderCell(
      cellRenderer: CellRenderer,
      params: { rowIndex: number, colIndex: number, colorIndex: ?number }) {
    const { rowIndex, colIndex, colorIndex } = params;
    if (colorIndex) {
      cellRenderer.addCell({ row: rowIndex, col: colIndex }, colorIndex);
    }
  }

  render(): ?React.Element<*> {
    return null;
  }
}

export default RectangularMap;
