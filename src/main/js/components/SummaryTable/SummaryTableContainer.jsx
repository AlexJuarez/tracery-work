/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import React, { Component } from 'react';
import { List } from 'immutable';
import SummaryTableRow from './SummaryTableRow';
import ScrollState from './records/ScrollState';

import type { ColumnWidths, Rows } from './constants';

type Props = {
  columnWidths: ColumnWidths,
  viewSizeInRows: number,
  rowHeight: number,
  rows: Rows,
  scrollState: ScrollState,
  totalRows: number,
};

export default class SummaryTableContainer extends Component {

  props: Props;

  _getStart(): number {
    return Math.max(this.props.scrollState.get('offset') - 1, 0);
  }

  _getEnd(): number {
    const { scrollState, viewSizeInRows, totalRows } = this.props;
    return Math.min(scrollState.get('offset') + viewSizeInRows, totalRows);
  }

  _renderTopOffset(): ?React.Element<*> {
    const start = this._getStart();
    const { rowHeight } = this.props;
    if (start === 0) {
      return null;
    }

    return <tr key="start" style={{ height: `${start * rowHeight}px` }} />;
  }

  _renderBottomOffset(): ?React.Element<*> {
    const { totalRows, rowHeight } = this.props;
    const end = this._getEnd();
    if (end === totalRows) {
      return null;
    }

    return <tr key="end" style={{ height: `${(totalRows - end) * rowHeight}px` }} />;
  }

  _renderRows(): Array<React.Element<*>> {
    const rows = [];
    const { rowHeight, viewSizeInRows } = this.props;
    const start = this._getStart();
    const end = this._getEnd();

    for (let i = start; i < end; i++) {
      const data = this.props.rows.get(`${i}`);
      /**
       * This key function is important, by taking the modulo of
       * the current row number by the view port size we can be
       * assured that SummaryTableRow containers are recycled,
       * improving render perf.
       */
      const key = i % (viewSizeInRows + 1);

      rows.push(
        <SummaryTableRow
          key={key}
          data={data == null ? new List() : data}
          height={rowHeight}
          rowNumber={i}
        />
      );
    }

    return rows;
  }

  _renderHeaders(): Array<React.Element<*>> {
    return this.props.columnWidths.valueSeq().toArray()
      .map((width: number, i: number): React.Element<*> => (
        <th key={i} style={{ padding: 0, width: `${width}px` }} />
      ));
  }

  render(): ?React.Element<*> {
    return (
      <table
        className="summary-table"
      >
        <thead>
          <tr>
            {this._renderHeaders()}
          </tr>
        </thead>
        <tbody>
          {this._renderTopOffset()}
          {this._renderRows()}
          {this._renderBottomOffset()}
        </tbody>
      </table>
    );
  }
}
