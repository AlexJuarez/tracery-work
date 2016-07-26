/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import React, { Component } from 'react';
import { SummaryTable } from './components/SummaryTable';
import { Map, List } from 'immutable';

import type { Column } from './components/SummaryTable';

type State = {
  height: number,
  width: number,
};

const TOTAL_ROWS = 1000;

const headers = new List([
  '#',
  'timestamp',
  'type',
  'count',
  'thread',
  'cpu',
  'file',
  'page-count',
  'duration',
].map((title: string, order: number): Column => ({
  title,
  order,
})));

function createRows(): {[key: string]: *} {
  const rows = {};
  for (let i = 0; i < TOTAL_ROWS; i++) {
    // Create some random data for the table
    rows[i] = new List([
      i,
      (new Date()).getTime() + Math.round(Math.random() * 100000),
      ['W', 'B', 'S', 'R'][Math.round(Math.random() * 3)],
      Math.round(Math.random() * 100),
      `thread ${i}`,
      0,
      `file ${i}`,
      Math.round(Math.random()),
      Math.round(Math.random() * 10000),
    ]);
  }
  return rows;
}

const rows = new Map(createRows());

export default class SummaryTableDemo extends Component {
  constructor(props: Object, context: Object) {
    super(props, context);

    this.state = {
      height: 400,
      width: 800,
    };
  }

  state: State;

  render(): React.Element<*> {
    return (
      <div>
        <button onClick={() => { this.setState({ width: this.state.width + 20 }); }}>
          Increase Width
        </button>
        <button onClick={() => { this.setState({ width: this.state.width - 20 }); }}>
          Decrease Width
        </button>
        <button onClick={() => { this.setState({ height: this.state.height + 20 }); }}>
          Increase Height
        </button>
        <button onClick={() => { this.setState({ height: this.state.height - 20 }); }}>
          Decrease Height
        </button>
        <SummaryTable
          rows={rows}
          headers={headers}
          totalRows={TOTAL_ROWS}
          height={this.state.height}
          width={this.state.width}
        />
      </div>
    );
  }
}
