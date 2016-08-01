/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import React, { Component } from 'react';
import throttle from 'lodash.throttle';
import invariant from 'invariant';
import shallowCompare from 'react-addons-shallow-compare';

import { List } from 'immutable';

import ScrollState from './records/ScrollState';
import SummaryTableContainer from './SummaryTableContainer';
import TableSizing from './SummaryTableSizing';

import {
  INITIAL_ROW_MAX,
  ROW_INITIAL_HEIGHT,
  SCROLLING_REFRESH_RATE,
} from './constants';

import type {
  Column,
  ColumnWidths,
  Headers,
  Rows,
} from './constants';

require('./styles/summary-table.less');

type Props = {
  /**
   * Optional the current row to start at.
   */
  currentRow?: number,
  headers: Headers,
  height?: number | string,
  /**
   * rows represents the currently loaded rows in a sparse map
   * with Map<rowNumber, row data>
   */
  rows: Rows,
  /**
   * totalRows is the total number of rows that is expected.
   */
  totalRows: number,
  width?: number | string,

  // Functions for updating
  onScrollStateUpdate?: (scrollState: ScrollState, viewSizeInRows: number) => void,
  onInit?: () => void,
};

type State = {
  rowHeight: number,
  viewSizeInRows: number,
  scroll: ScrollState,
  columnWidths: ColumnWidths,
}

export default class SummaryTable extends Component {
  constructor(props: Props, context: Object) {
    super(props, context);

    const initialScrollState = {};

    if (props.currentRow) {
      invariant(
        props.currentRow <= props.totalRows,
        'The currently selected row is greater then the total number of rows.'
      );
      invariant(
        props.currentRow >= 0,
        'The currently selected row number must be positive'
      );
      initialScrollState.offset = props.currentRow;
    }

    invariant(
      props.rows.size <= props.totalRows,
      'The number of rows exceeds the total number of rows'
    );

    invariant(
      props.totalRows >= 0,
      'The total number of rows must be positive'
    );

    this.state = {
      rowHeight: ROW_INITIAL_HEIGHT,
      viewSizeInRows: INITIAL_ROW_MAX,
      scroll: new ScrollState(initialScrollState),
      columnWidths: new List(),
    };

    this._ticking = false;
  }

  props: Props;
  state: State;

  componentDidMount() {
    if (this.props.onInit != null) {
      this.props.onInit();
    }
  }

  componentWillReceiveProps(nextProps: Props) {
    if (nextProps.currentRow != null && this.props.currentRow !== nextProps.currentRow) {
      this.setState({
        scroll: this.state.scroll.set('offset', nextProps.currentRow),
      });
    }
  }

  shouldComponentUpdate(nextProps: Props, nextState: State): boolean {
    return shallowCompare(this, nextProps, nextState);
  }

  _ticking: boolean;

  _onResize(widths: Array<number>, rowHeight: number, viewSizeInRows: number) {
    this.setState({
      columnWidths: new List(widths),
      rowHeight,
      viewSizeInRows,
    });
  }

  _getOrderedColumns(): Headers {
    return this.props.headers
      .sort((a: Column, b: Column): number => (
        a.order - b.order
      ));
  }

  _handleScroll(event: *) {
    function updateOffset(target: HTMLElement) {
      const scrollTop = target.scrollTop;
      const offset = Math.floor(scrollTop / this.state.rowHeight);
      const scroll = this.state.scroll.setOffset(offset);

      if (this.props.onScrollStateUpdate != null) {
        this.props.onScrollStateUpdate(scroll, this.state.viewSizeInRows);
      }

      this.setState({ scroll });

      this._ticking = false;
    }

    if (!this._ticking) {
      const target = event.target;
      window.requestAnimationFrame(() => {
        throttle(updateOffset.bind(this), SCROLLING_REFRESH_RATE)(target);
      });
    }
    this._ticking = true;
  }

  _hasRows(): boolean {
    return this.props.rows != null && this.props.rows.size !== 0;
  }

  _renderHeaders(): ?React.Element<*> {
    if (!this._hasRows()) {
      return null;
    }

    const { columnWidths } = this.state;

    const headers = this._getOrderedColumns()
      .map((header: Column, i: number): React.Element<*> => {
        let style;
        if (columnWidths.get(i) != null) {
          style = { width: `${columnWidths.get(i)}px` };
        }

        return (
          <div
            className="summary-table-header-cell"
            style={style}
            key={header.title}
          >
            {header.title}
          </div>
        );
      });

    return (
      <div className="summary-table-header">
        {headers}
      </div>
    );
  }

  _renderSummaryTable(): ?React.Element<*> {
    if (!this._hasRows()) {
      return null;
    }

    return (
      <div
        onScroll={(event: *): void => this._handleScroll(event)}
        className="summary-table-body"
      >
        <SummaryTableContainer
          columnWidths={this.state.columnWidths}
          viewSizeInRows={this.state.viewSizeInRows}
          rowHeight={this.state.rowHeight}
          rows={this.props.rows}
          scrollState={this.state.scroll}
          totalRows={this.props.totalRows}
        />
      </div>
    );
  }

  _renderSummaryTableSizing(): ?React.Element<*> {
    if (!this._hasRows()) {
      return null;
    }

    return (
      <TableSizing
        rows={this.props.rows}
        columns={this._getOrderedColumns()}
        width={this.props.width}
        height={this.props.height}
        onResize={(widths: Array<number>, rowHeight: number, viewSizeInRows: number) => {
          this._onResize(widths, rowHeight, viewSizeInRows);
        }}
      />
    );
  }

  render(): React.Element<*> {
    return (
      <div className="summary-table-container">
        {this._renderHeaders()}
        {this._renderSummaryTable()}
        {this._renderSummaryTableSizing()}
      </div>
    );
  }
}
