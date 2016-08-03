/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import React, { Component } from 'react';
import throttle from 'lodash.throttle';

import { List } from 'immutable';
import { Header } from '.';

import forEachChild from './utils/forEachChild';

import SummaryTableRow from './SummaryTableRow';

import { ON_RESIZE_ROW_COUNT, RESIZE_REFRESH_RATE } from './constants';

import type { ColumnOrder, OnResize, OnHeaderUpdate, Rows, Headers } from './constants';

type Props = {
  columnOrder: ColumnOrder,
  headers: Headers,
  rows: Rows,
  height: number,
  width: number,

  // Event callbacks
  onResize: OnResize,
  onHeaderUpdate: OnHeaderUpdate,
};

type State = {
  headerHeight: number,
};

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

    return this.props.headers !== nextProps.headers;
  }

  componentDidUpdate() {
    this._handleResizing();
  }

  _handleResizing: () => void;
  _ticking: boolean;
  _rows: HTMLElement;
  _container: HTMLElement;

  _renderColumns(): Array<React.Element<*>> {
    return this.props.headers.toArray().map((header: Header): React.Element<*> => {
      const style = {};

      style.minWidth = `${header.minWidth}px`;
      if (header.width != null) {
        style.width = `${header.width}px`;
      }

      return (
        <th className="summary-table-header-cell" style={style} key={header.title}>
          {header.title}
        </th>
      );
    });
  }

  _renderRows(): Array<React.Element<*>> {
    return this.props.rows.valueSeq().take(ON_RESIZE_ROW_COUNT).toArray()
      .map((data: List<*>, rowNumber: number): React.Element<*> => {
        const { columnOrder } = this.props;
        return (
          <SummaryTableRow
            columnOrder={columnOrder}
            data={data}
            rowNumber={rowNumber}
            key={rowNumber}
          />
        );
      });
  }

  _headersReady(): boolean {
    return this.state.headerHeight !== 0;
  }

  _handleHeaderWidths(headers: HTMLElement) {
    if (headers == null || this._headersReady()) {
      return;
    }

    const widths = [];

    forEachChild(headers.firstElementChild, (cell: HTMLElement) => {
      widths.push(cell.offsetWidth);
    });

    this.props.onHeaderUpdate(
      this.props.headers.map((header: Header, i: number): Header => (
        header.set('minWidth', widths[i])
      ))
    );

    this.setState({ headerHeight: headers.offsetHeight });
  }

  _handleColumnWidth() {
    const firstRow = this._rows && this._rows.firstElementChild;
    const widths = [];
    forEachChild(firstRow, (cell: HTMLElement) => {
      widths.push(cell.offsetWidth);
    });

    if (firstRow != null && firstRow instanceof HTMLElement) {
      const rowHeight = firstRow.offsetHeight;
      const maxRows = Math.ceil(this._container.offsetHeight / rowHeight);
      this.props.onHeaderUpdate(
        this.props.headers.map((header: Header, i: number): Header => (
          header.set('width', widths[i])
        ))
      );
      this.props.onResize(
        rowHeight,
        maxRows,
        (this.props.height - this.state.headerHeight)
      );
    }

    this._ticking = false;
  }

  _renderBody(): ?React.Element<*> {
    if (!this._headersReady()) {
      return null;
    }

    return (
      <tbody ref={(r: HTMLElement) => { this._rows = r; }}>
        {this._renderRows()}
      </tbody>
    );
  }

  render(): React.Element<*> {
    const style = {};

    if (!this._headersReady()) {
      style.width = 'initial';
    }

    return (
      <div
        ref={(c: HTMLElement) => { this._container = c; }}
        className="summary-table-sizing"
      >
        <table className="summary-table" style={style}>
          <thead
            ref={(e: HTMLElement) => {
              this._handleHeaderWidths(e);
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
