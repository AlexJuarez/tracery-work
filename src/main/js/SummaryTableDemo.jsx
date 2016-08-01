/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import React from 'react';
import { SummaryTable } from './components/SummaryTable';
import { Map, List } from 'immutable';

import type { Column } from './components/SummaryTable';

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

export default function SummaryTableDemo(): React.Element<*> {
  return (
    <SummaryTable
      rows={rows}
      headers={headers}
      totalRows={TOTAL_ROWS}
    />
  );
}
