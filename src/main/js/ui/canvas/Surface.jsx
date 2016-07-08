// @flow

import invariant from 'invariant';
import React from 'react';

import type { CanvasComponent } from './CanvasComponent';
import { dipsToPixels } from './pixels';

type Props = {
  /** Width in dips */
  width: number,
  /** Height in dips */
  height: number,
  children?: any,
};

/**
 * Provides an HTML5 2D Canvas rendering surface for React component children.
 *
 * Child components should return null from their render() methods, and conform to the
 * CanvasComponent interface. Children are rendered in the order in which they are specified; that
 * is, the first child is rendered on the bottom and the last on the top.
 *
 * For now, this component re-renders its children any time anything changes. Higher-level
 * components should implement shouldComponentUpdate() to help avoid excessive rendering.
 */
class Surface extends React.Component {
  constructor(props: Props) {
    super(props);

    this._clearChildComponents();

    invariant(props.width > 0 && props.height > 0, 'Size must be non-negative');
  }

  props: Props;

  componentDidMount() {
    this._renderChildren();
  }

  componentWillUpdate() {
    this._clearChildComponents();
  }

  componentDidUpdate() {
    this._renderChildren();
  }

  getCanvasContext(component: CanvasComponent): CanvasRenderingContext2D {
    invariant(component, 'Callers must pass `this` to `getCanvasContext`.');
    invariant(this._canvasContext, '_canvasContext was unexpectedly null during rendering.');

    return this._canvasContext;
  }

  _pixelWidth(): number {
    // We use ceiling here because we want to make sure that we have enough space for at least
    // as many dips as are specified.
    return Math.ceil(dipsToPixels(this.props.width));
  }

  _pixelHeight(): number {
    // We use ceiling here because we want to make sure that we have enough space for at least
    // as many dips as are specified.
    return Math.ceil(dipsToPixels(this.props.height));
  }

  _canvasContext: ?CanvasRenderingContext2D;

  _setCanvas(canvas: HTMLCanvasElement) {
    if (canvas && !this._canvasContext) {
      this._canvasContext = canvas.getContext('2d');
    }
  }

  _childComponents: Array<CanvasComponent>;

  _clearChildComponents() {
    this._childComponents = new Array(React.Children.count(this.props.children));
  }

  _addChild(index: number, child: CanvasComponent) {
    this._childComponents[index] = child;
  }

  _renderChildren() {
    if (this._canvasContext) {
      this._clearCanvas();

      // This is to help Flow understand that the value of canvasContext won't
      // change before the arrow function is run.
      const nonNullContext = this._canvasContext;
      this._childComponents.forEach(
        (child: ?CanvasComponent): void => this._renderChild(child, nonNullContext));
    }
  }

  _clearCanvas() {
    if (this._canvasContext) {
      this._canvasContext.clearRect(
        0,
        0,
        this._pixelWidth(),
        this._pixelHeight());
    }
  }

  _renderChild(child: ?CanvasComponent) {
    if (child && this._canvasContext) {
      child.renderToCanvas(this);
    }
  }

  render(): React.Element<*> {
    return (
      <canvas
        ref={(canvas: HTMLCanvasElement): void => this._setCanvas(canvas)}
        width={this._pixelWidth()}
        height={this._pixelHeight()}
        style={{
          width: this.props.width,
          height: this.props.height,
          display: 'block',
        }}
      >
        {
          React.Children.map(
            this.props.children,
            (child: React.Element<*>, index: number): React.Element<*> =>
              React.cloneElement(
                child,
                {
                  ref: (component: CanvasComponent): void =>
                    this._addChild(index, component),
                }
              )
          )
        }
      </canvas>
    );
  }
}

export default Surface;
