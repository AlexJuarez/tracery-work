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
import { Header } from '.';

import ScrollState from './records/ScrollState';
import SummaryTableContainer from './SummaryTableContainer';
import TableSizing from './SummaryTableSizing';
import TableHeader from './SummaryTableHeader';

import {
  INITIAL_ROW_MAX,
  ROW_INITIAL_HEIGHT,
  SCROLLING_REFRESH_RATE,
} from './constants';

import type {
  Headers,
  Rows,
} from './constants';

require('./styles/summary-table.less');

type Props = {
  /**
   * Optional the current row to start at.
   */
  currentRow?: number,
  headers: Array<string>,
  /**
   * height in pixels
   */
  height: number,
  /**
   * rows represents the currently loaded rows in a sparse map
   * with Map<rowNumber, row data>
   */
  rows: Rows,
  /**
   * totalRows is the total number of rows that is expected.
   */
  totalRows: number,
  /**
   * width in pixels
   */
  width: number,

  // Functions for updating
  onScrollStateUpdate?: (scrollState: ScrollState, viewSizeInRows: number) => void,
  onInit?: () => void,
};

type State = {
  headers: Headers,
  rowHeight: number,
  scroll: ScrollState,
  tableBodyHeight: number,
  viewSizeInRows: number,
};

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

    if (props.width != null) {
      invariant(props.width >= 0, 'Width must be greater than 0');
    }

    if (props.height != null) {
      invariant(props.height >= 0, 'Height must be greater than 0');
    }

    this.state = {
      headers: new List(props.headers.map((title: string, order: number): Header => (
        new Header({
          title,
          order,
        })
      ))),
      tableBodyHeight: 0,
      rowHeight: ROW_INITIAL_HEIGHT,
      viewSizeInRows: INITIAL_ROW_MAX,
      scroll: new ScrollState(initialScrollState),
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

  _onHeaderUpdate(headers: Headers) {
    this.setState({
      headers: headers.sort((a: Header, b: Header): number => (
        a.order - b.order
      )),
    });
  }

  _onResize(
    rowHeight: number,
    viewSizeInRows: number,
    tableBodyHeight: number,
  ) {
    this.setState({
      rowHeight,
      viewSizeInRows,
      tableBodyHeight,
    });
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

  _onHeaderResize(index: number, delta: number) {
    const { headers } = this.state;
    const firstHeader = headers.get(index);
    const secondHeader = headers.get(index + 1);

    let firstHeaderWidth = firstHeader.width - delta;
    let secondHeaderWidth = secondHeader.width + delta;

    /**
     * If the headerWidth is less than the minWidth for that column
     * then the headerWidth should be equal to the minWidth
     * and the other header should be equal to its original width + the change
     * in width in the first.
     */
    if (firstHeaderWidth < firstHeader.minWidth) {
      firstHeaderWidth = firstHeader.minWidth;
      secondHeaderWidth = secondHeader.width + (firstHeader.width - firstHeaderWidth);
    } else if (secondHeaderWidth < secondHeader.minWidth) {
      secondHeaderWidth = secondHeader.minWidth;
      firstHeaderWidth = firstHeader.width + (secondHeader.width - secondHeaderWidth);
    }

    const updated = headers.set(index,
      firstHeader
        .set('width', firstHeaderWidth)
        .set('customWidth', true)
    )
      .set(index + 1,
        secondHeader
          .set('width', secondHeaderWidth)
          .set('customWidth', true)
      );

    this.setState({ headers: updated });
  }

  _renderHeaders(): ?React.Element<*> {
    if (!this._hasRows()) {
      return null;
    }

    const headers = this.state.headers
      .map((header: Header, index: number): React.Element<*> => (
        <TableHeader
          onResize={(delta: number) => { this._onHeaderResize(index, delta); }}
          header={header}
          headers={this.state.headers}
          key={header.title}
        />
      ));

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
        style={{ height: `${this.state.tableBodyHeight}px` }}
      >
        <SummaryTableContainer
          headers={this.state.headers}
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
        headers={this.state.headers}
        width={this.props.width}
        height={this.props.height}
        onResize={(
          rowHeight: number,
          viewSizeInRows: number,
          tableBodyHeight: number
        ) => {
          this._onResize(rowHeight, viewSizeInRows, tableBodyHeight);
        }}
        onHeaderUpdate={(headers: Headers) => {
          this._onHeaderUpdate(headers);
        }}
      />
    );
  }

  render(): React.Element<*> {
    return (
      <div style={{ width: `${this.props.width}px` }} className="summary-table-container">
        {this._renderHeaders()}
        {this._renderSummaryTable()}
        {this._renderSummaryTableSizing()}
      </div>
    );
  }
}
