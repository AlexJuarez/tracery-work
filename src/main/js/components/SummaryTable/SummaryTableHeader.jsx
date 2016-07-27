/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import React, { Component } from 'react';
import { Header } from './';

import pageOffsetLeft from './utils/pageOffsetLeft';

import type { Headers } from './';

type Props = {
  header: Header,
  headers: Headers,
  onResize: (delta: number) => void,
};

type State = {
  resizing: boolean,
}

export default class SummaryTableHeader extends Component {
  constructor(props: Props, context: Object) {
    super(props, context);

    this.state = {
      resizing: false,
    };

    this._ticking = false;

    this._stopResizing = this._stopResizing.bind(this);
    this._resizingHandler = this._resizingHandler.bind(this);
  }

  props: Props;
  state: State;

  componentDidMount() {
    window.addEventListener('mouseup', this._stopResizing, false);
    window.addEventListener('mousemove', this._resizingHandler, false);
  }

  componentWillUnmount() {
    window.removeEventListener('mouseup', this._stopResizing);
    window.removeEventListener('mousemove', this._resizingHandler);
  }

  _stopResizing: () => void;
  _resizingHandler: (event: SyntheticMouseEvent) => void;
  _ticking: boolean;
  _resizeHandle: ?HTMLElement;

  _resizingHandler(event: SyntheticMouseEvent) {
    if (!this.state.resizing) {
      return;
    }

    if (this._resizeHandle != null && !this._ticking) {
      const mouseX = event.pageX;
      /**
       * Find the offsetLeft relative to the resizeHandle
       * then add half the width so that the delta is calculated
       * from the middle of the resize handle
       */
      const offsetCenter = (this._resizeHandle.clientWidth / 2);
      const offsetLeft = pageOffsetLeft(this._resizeHandle);
      const dragFn = () => {
        this.props.onResize(offsetLeft + offsetCenter - mouseX);

        this._ticking = false;
      };
      window.requestAnimationFrame(() => { dragFn(); });
    }

    this._ticking = true;
  }

  _stopResizing() {
    document.body.className = document.body.className
      .replace('summary-table-cursor-resizing', '')
      .trim();
    this.setState({ resizing: false });
  }

  _startResizing() {
    // This class ensures that the cursor is the resizing pointer while
    // dragging.
    document.body.className += ' summary-table-cursor-resizing';
    this.setState({ resizing: true });
  }

  _isLastHeader(): boolean {
    return (this.props.headers.size - 1) === this.props.header.order;
  }

  _renderResizeHandle(): ?React.Element<*> {
    if (this._isLastHeader()) {
      return null;
    }

    return (
      <div
        ref={(resizeHandle: HTMLElement) => {
          this._resizeHandle = resizeHandle;
        }}
        onMouseDown={(event: SyntheticMouseEvent) => {
          event.preventDefault();
          this._startResizing();
        }}
        className="summary-table-header-resizing-handle"
      />
    );
  }

  render(): React.Element<*> {
    const title = this.props.header.title;
    const width = this.props.header.width;

    const style = {};

    if (width != null) {
      style.width = `${width}px`;
    }

    return (
      <div
        className="summary-table-cell summary-table-header-cell"
        style={style}
      >
        {title}
        {this._renderResizeHandle()}
      </div>
    );
  }
}
