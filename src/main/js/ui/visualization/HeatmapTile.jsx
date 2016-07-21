// @flow

import invariant from 'invariant';
import React from 'react';

import type { Color } from '../Color';
import Surface from '../canvas/Surface';
import Rectangle from '../canvas/Rectangle';
import RectangularMap from '../canvas/RectangularMap';

type Props = {
  /** Width in dips */
  width: number,
  /** Height in dips */
  height: number,
  /** Cell size in dips */
  cellSize: number,
  data: Array<?number>,
}

const colors: Array<Color> = new Array(16);
for (let i = 0; i < 16; i++) {
  colors[i] = { r: i * 16, g: i * 16, b: i * 16, a: 255 };
}

class HeatmapTile extends React.Component {
  constructor(props: Props) {
    super(props);

    this.props = props;
  }

  props: Props;

  render(): React.Element<*> {
    const { width, height, cellSize, data } = this.props;

    const maxWidthInCells = Math.trunc(width / cellSize);
    const maxHeightInCells = Math.trunc(height / cellSize);
    const maxCells = maxWidthInCells * maxHeightInCells;

    const numCells = data.length;
    const heightInCells = Math.trunc(numCells / maxWidthInCells + 1);

    invariant(
      numCells <= maxCells,
      `Too much data! Max cells: ${maxCells} Data len: ${numCells}`);

    const cells = new Array(heightInCells);

    let i = 0;
    for (let row = 0; row < heightInCells; row++) {
      const rowLength = Math.min(numCells - i, maxWidthInCells);
      cells[row] = new Array(rowLength);
      for (let col = 0; col < rowLength; col++, i++) {
        if (data[i]) {
          // TODO: Actually scale...
          cells[row][col] = Math.floor(data[i] / 16);
        }
      }
    }
    invariant(i === numCells, `Problem with the math. Expected ${numCells} found ${i}.`);

    const GRID_THRESHOLD = 10;
    return (
      <Surface width={width} height={height}>
        <Rectangle
          x={0}
          y={0}
          width={width}
          height={height}
          fill="lightBlue"
        />
        <RectangularMap
          x={0}
          y={0}
          width={width}
          height={height}
          cellWidth={cellSize}
          cellHeight={cellSize}
          colors={colors}
          cells={cells}
          stroke={cellSize >= GRID_THRESHOLD ? { r: 64, g: 64, b: 64, a: 255 } : undefined}
        />
      </Surface>);
  }
}

export default HeatmapTile;
