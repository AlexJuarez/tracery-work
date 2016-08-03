/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

import React, { Component } from 'react';
import { Header } from './';
import pageOffsetLeft from './utils/pageOffsetLeft';
import shallowCompare from 'react-addons-shallow-compare';
import { addClass, removeClass } from './utils/classes';

import type { Headers } from './';

type Props = {
  header: Header,
  headers: Headers,

  onDragStart: (header: Header) => void,
  onDragOver: (header: Header) => void,
  onDragEnd: (header: Header) => void,
  onResize: (delta: number) => void,
};

type State = {
  resizing: boolean,
  dragging: boolean,
}

function clearDragOptions(event: SyntheticDragEvent) {
  const { dataTransfer } = event;
  dataTransfer.effectAllowed = 'none';
  dataTransfer.dropEffect = 'none';
}

export default class SummaryTableHeader extends Component {
  constructor(props: Props, context: Object) {
    super(props, context);

    this.state = {
      resizing: false,
      dragging: false,
    };

    this._ticking = false;

    this._stopResizing = this._stopResizing.bind(this);
    this._mouseMoveHandler = this._mouseMoveHandler.bind(this);
  }

  props: Props;
  state: State;

  componentDidMount() {
    window.addEventListener('mouseup', this._stopResizing, false);
    window.addEventListener('mousemove', this._mouseMoveHandler, false);
  }

  shouldComponentUpdate(nextProps: Props, nextState: State): boolean {
    return shallowCompare(this, nextProps, nextState);
  }

  componentWillUnmount() {
    window.removeEventListener('mouseup', this._stopResizing);
    window.removeEventListener('mousemove', this._mouseMoveHandler);
  }

  _stopResizing: () => void;
  _mouseMoveHandler: (event: SyntheticMouseEvent) => void;
  _ticking: boolean;
  _resizeHandle: ?HTMLElement;
  _root: ?HTMLElement;

  _mouseMoveHandler(event: SyntheticMouseEvent) {
    if (this.state.resizing) {
      this._resizingHandler(event);
    }
  }

  _resizingHandler(event: SyntheticMouseEvent) {
    if (this._resizeHandle != null && !this._ticking) {
      const mouseX = event.pageX;
      /**
       * Find the offsetLeft relative to the resizeHandle
       * then add half the width so that the delta is calculated
       * from the middle of the resize handle
       */
      const offsetCenter = (this._resizeHandle.offsetWidth / 2);
      const offsetLeft = pageOffsetLeft(this._resizeHandle);
      const resizeFn = () => {
        this.props.onResize(offsetLeft + offsetCenter - mouseX);

        this._ticking = false;
      };
      window.requestAnimationFrame(() => { resizeFn(); });
    }

    this._ticking = true;
  }

  _startResizing(event: SyntheticMouseEvent) {
    // only start resizing if left button mouse down
    if (event.button === 0) {
      event.preventDefault();
      event.stopPropagation();
      addClass(document.body, 'summary-table-cursor-resizing');
      this.setState({ resizing: true });
    }
  }

  _stopResizing() {
    removeClass(document.body, 'summary-table-cursor-resizing');
    this.setState({ resizing: false });
  }

  _startDragging(event: SyntheticDragEvent) {
    clearDragOptions(event);
    this.props.onDragStart(this.props.header);
    this.setState({ dragging: true });
  }

  _stopDragging() {
    this.props.onDragEnd(this.props.header);
    this.setState({ dragging: false });
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
          this._startResizing(event);
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
      style.width = width;
    }

    return (
      <div
        className="summary-table-header-cell"
        draggable="true"
        onDragStart={(event: SyntheticDragEvent) => { this._startDragging(event); }}
        onDragEnd={(event: SyntheticDragEvent) => { this._stopDragging(event); }}
        onDragEnter={clearDragOptions}
        onDragOver={(event: SyntheticDragEvent) => {
          // By default elements cannot be dropped in other elements.
          // To allow a drop we must prevent the default handling of the element.
          event.preventDefault();
          this.props.onDragOver(this.props.header);
        }}
        ref={(root: HTMLElement) => { this._root = root; }}
        style={style}
      >
        {title}
        {this._renderResizeHandle()}
      </div>
    );
  }
}
