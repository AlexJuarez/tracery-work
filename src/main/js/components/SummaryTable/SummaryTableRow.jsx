/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import React, { Component } from 'react';
import classNames from 'classnames';
import invariant from 'invariant';
import shallowCompare from 'react-addons-shallow-compare';

import { List } from 'immutable';

type Props = {
  data: List<*>,
  height?: number,
  rowNumber: number,
};

/**
 * SummaryTableRow represents one row item in the table.
 */

export default class SummaryTableRow extends Component {
  constructor(props: Props, context: Object) {
    super(props, context);

    if (props.height != null) {
      invariant(props.height >= 0, 'Height must be positive');
    }

    invariant(props.rowNumber >= 0, 'The starting row number must be positive');
  }

  props: Props;

  shouldComponentUpdate(nextProps: Props, nextState: *): boolean {
    return shallowCompare(this, nextProps, nextState);
  }

  render(): React.Element<*> {
    const { rowNumber, data, height } = this.props;

    const classes = classNames(
      'summary-table-row-loading',
      { 'summary-table-row-striped': (rowNumber) % 2 === 1 },
    );

    const style = {};

    if (data.size === 0 && height != null) {
      style.height = `${height}px`;
      return (
        <tr className={classes} style={style}>
          <td colSpan="100%" className="summary-table-row-loading-bg" />
        </tr>
      );
    }

    return (
      <tr className={classes} style={style}>
        {data.map((cell: *, j: number): React.Element<*> => (
          <td key={`${rowNumber}:${j}`} className="summary-table-cell">
            <div className="summary-table-cell-content">
              {cell}
            </div>
          </td>
        ))}
      </tr>
    );
  }
}
