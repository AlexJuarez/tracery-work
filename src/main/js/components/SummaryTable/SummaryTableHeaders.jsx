/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import React, { Component } from 'react';

import shallowCompare from 'react-addons-shallow-compare';

import TableHeader from './SummaryTableHeader';

import { Header } from './';

import type { Headers } from './';

type Props = {
  headers: Headers,

  onHeaderUpdate: (headers: Headers) => void,
};

type State = {
  draggingHeaderTitle: ?string,
  dropTarget: ?string,
};

export default class SummaryTableHeaders extends Component {

  constructor(props: Props, context: Object) {
    super(props, context);

    this.state = {
      draggingHeaderTitle: null,
      dropTarget: null,
    };
  }

  props: Props;
  state: State;

  shouldComponentUpdate(nextProps: Props, nextState: *): boolean {
    return shallowCompare(this, nextProps, nextState);
  }

  componentDidUpdate(prevProps: Props) {
    const { headers } = this.props;
    if (headers === prevProps.headers) {
      const { draggingHeaderTitle, dropTarget } = this.state;
      if (draggingHeaderTitle != null && dropTarget != null
          && draggingHeaderTitle !== dropTarget) {
        const sourceHeader = headers.find(
          (val: Header): boolean => val.title === draggingHeaderTitle
        );
        const dropHeader = headers.find(
          (val: Header): boolean => val.title === dropTarget
        );
        this.props.onHeaderUpdate(
          headers
            .set(headers.indexOf(sourceHeader), sourceHeader.set('order', dropHeader.order))
            .set(headers.indexOf(dropHeader), dropHeader.set('order', sourceHeader.order))
        );
      }
    }
  }

  _onHeaderResize(index: number, delta: number) {
    const { headers } = this.props;
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

    this.props.onHeaderUpdate(
      headers
        .set(index, firstHeader.set('width', firstHeaderWidth))
        .set(index + 1, secondHeader.set('width', secondHeaderWidth))
    );
  }

  _onDragStart(header: Header) {
    this.setState({ draggingHeaderTitle: header.title });
  }

  _onDragEnd() {
    this.setState({ draggingHeaderTitle: null, dropTarget: null });
  }

  _onDragOver(header: Header) {
    this.setState({ dropTarget: header.title });
  }

  _renderHeaders(): Array<React.Element<*>> {
    return this.props.headers.toArray()
      .map((header: Header, index: number): React.Element<*> => (
        <TableHeader
          onResize={(delta: number) => { this._onHeaderResize(index, delta); }}
          header={header}
          headers={this.props.headers}
          onDragStart={(h: Header) => { this._onDragStart(h); }}
          onDragEnd={() => { this._onDragEnd(); }}
          onDragOver={(h: Header) => { this._onDragOver(h); }}
          key={index}
        />
      ));
  }

  render(): React.Element<*> {
    return (
      <div className="summary-table-header">
        {this._renderHeaders()}
      </div>
    );
  }
}
