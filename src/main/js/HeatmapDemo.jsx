// @flow

import React from 'react';

import { pixelsToDips } from './ui/canvas/pixels';
import HeatmapTile from './ui/visualization/HeatmapTile';

type State = {
  cellSize: number
};

const numCells = Math.floor(Math.random() * 10000);

const cells = new Array(numCells);
let i = 0;
while (i < numCells) {
  const dataValue = Math.floor(Math.random() * 16) * 16;
  const dataLen = Math.floor(Math.random() * 16);

  const limit = Math.min(numCells, i + dataLen);
  for (; i < limit; i++) {
    if (dataValue < 16) {
      cells[i] = null;
    } else {
      cells[i] = dataValue;
    }
  }
}

type Props = {
  width: number,
  height: number,
}

const ZOOM_INCREMENT = 1;
class HeatmapDemo extends React.Component {
  constructor(props: Props) {
    super(props);

    this.state = {
      cellSize: 10,
    };

    // $FlowIssue: https://github.com/facebook/flow/issues/1517
    this._handleKeyPress = this._handleKeyPress.bind(this);
  }

  props: Props;

  state: State;

  _handleKeyPress(event: SyntheticKeyboardEvent) {
    if (event.altKey || event.ctrlKey || event.metaKey || event.shiftKey) {
      return;
    }

    if (event.key === '=' || event.key === '+') {
      this._zoomIn(ZOOM_INCREMENT);
      event.stopPropagation();
    } else if (event.key === '-') {
      this._zoomOut(ZOOM_INCREMENT);
      event.stopPropagation();
    }
  }

  _zoomIn(amount: number) {
    this.setState({
      cellSize: Math.min(
        Math.min(this.props.width, this.props.height),
        this.state.cellSize + amount),
    });
  }

  _zoomOut(amount: number) {
    this.setState({
      cellSize: Math.max(pixelsToDips(1), this.state.cellSize - amount),
    });
  }

  render(): React.Element<*> {
    return (
      <div>
        <span
          ref={(obj: any): void => focus(obj)}
          tabIndex={-1}
          onKeyDown={(event: SyntheticKeyboardEvent): void => this._handleKeyPress(event)}
        />
        <HeatmapTile
          width={this.props.width}
          height={this.props.height}
          cellSize={this.state.cellSize}
          data={cells}
        />
      </div>
    );
  }
}

function focus(obj: any) {
  if (obj) {
    obj.focus();
  }
}

export default HeatmapDemo;
