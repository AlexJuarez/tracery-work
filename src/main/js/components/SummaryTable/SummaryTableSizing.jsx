/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import React, { Component } from 'react';
import throttle from 'lodash.throttle';

import { List } from 'immutable';

import forEachChild from './utils/forEachChild';

import SummaryTableRow from './SummaryTableRow';

import { ON_RESIZE_ROW_COUNT, RESIZE_REFRESH_RATE } from './constants';

import type { Column, OnResize, Rows, Headers } from './constants';

type Props = {
  columns: Headers,
  rows: Rows,
  height: number,
  width: number,

  // Event callbacks
  onResize: OnResize,
};

type State = {
  headerHeight: number,
};

function renderRow(r: List<*>, rowNumber: number): React.Element<*> {
  return (
    <SummaryTableRow data={r} rowNumber={rowNumber} key={rowNumber} />
  );
}

/**
 * This is a hidden table rendering that updates the size of the columns
 * when a resize event is triggered.
 */

export default class SummaryTableSizing extends Component {
  constructor(props: Props, context: Object) {
    super(props, context);

    this.state = {
      headerHeight: 0,
    };

    this._ticking = false;
    this._handleResizing = () => {
      if (!this._ticking) {
        window.requestAnimationFrame(
          throttle(() => { this._handleColumnWidth(); }, RESIZE_REFRESH_RATE)
        );
      }
      this._ticking = true;
    };
  }

  props: Props;
  state: State;

  componentDidMount() {
    this._handleResizing();
  }

  shouldComponentUpdate(nextProps: Props, nextState: State): boolean {
    if (this.props.rows.size === 0 && nextProps.rows.size !== 0) {
      return true;
    }

    if (this.props.height !== nextProps.height) {
      return true;
    }

    if (this.props.width !== nextProps.width) {
      return true;
    }

    if (this.state.headerHeight !== nextState.headerHeight) {
      return true;
    }

    return this.props.columns !== nextProps.columns;
  }

  componentDidUpdate() {
    this._handleResizing();
  }

  _handleResizing: () => void;
  _ticking: boolean;
  _rows: HTMLElement;
  _container: HTMLElement;

  _renderColumns(): Array<React.Element<*>> {
    return this.props.columns.toArray().map((c: Column): React.Element<*> => (
      <th className="summary-table-cell" style={{ minWidth: '3ex' }} key={c.title}>
        {c.title}
      </th>
    ));
  }

  _renderRows(): Array<React.Element<*>> {
    return this.props.rows.valueSeq().take(ON_RESIZE_ROW_COUNT).toArray()
      .map(renderRow);
  }

  _handleColumnWidth() {
    const firstRow = this._rows && this._rows.firstElementChild;
    const widths = [];
    forEachChild(firstRow, (cell: HTMLElement) => {
      widths.push(cell.clientWidth);
    });

    if (firstRow != null) {
      const rowHeight = firstRow.clientHeight;
      const maxRows = Math.ceil(this._container.clientHeight / rowHeight);
      this.props.onResize(
        widths,
        rowHeight,
        maxRows,
        (this.props.height - this.state.headerHeight)
      );
    }

    this._ticking = false;
  }

  _renderBody(): ?React.Element<*> {
    if (this.state.headerHeight === 0) {
      return null;
    }

    return (
      <tbody ref={(r: HTMLElement) => { this._rows = r; }}>
        {this._renderRows()}
      </tbody>
    );
  }

  render(): React.Element<*> {
    return (
      <div
        ref={(c: HTMLElement) => { this._container = c; }}
        className="summary-table-sizing"
      >
        <table
          className="summary-table"
          style={{ visibility: 'hidden' }}
        >
          <thead
            ref={(e: HTMLElement) => {
              if (e != null) {
                this.setState({ headerHeight: e.clientHeight });
              }
            }}
          >
            <tr className="summary-table-header">
              {this._renderColumns()}
            </tr>
          </thead>
          {this._renderBody()}
        </table>
      </div>
    );
  }
}
