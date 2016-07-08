// @flow

import invariant from 'invariant';
import React from 'react';

import { dipsToPixels } from './pixels';
import type { CanvasContextProvider } from './CanvasContextProvider';

/** Coordinates and sizes  might be dips or pixels; see docs at the point of use for which. */
type Props = {
  x: number,
  y: number,
  width: number,
  height: number,
  /** Stroke as a string that parses to a css <color> */
  stroke?: string,
  /** Fill as a string that parses to a css <color> */
  fill?: string,
  /** Stroke width in dips */
  strokeWidth: number,
  /** No other properties */
  [key: string]: void,
}

const HAIRLINE_WIDTH = 0.5;

/** Renders a rectangle to the enclosing Surface, optionally filled and/or stroked. */
class Rectangle extends React.Component {
  static defaultProps: {
    strokeWidth: number,
  };

  /** Coordinates and sizes within Props should be dips. */
  constructor(props: Props) {
    super(props);

    invariant(
      props.x >= 0 && props.y >= 0 && props.width >= 0 && props.height >= 0,
      'Size and position must be non-negative.');

    invariant(props.stroke || props.fill, 'Must specify at least one of stroke or fill.');
  }

  props: Props;

  renderToCanvas(canvasContextProvider: CanvasContextProvider) {
    const pixelProps = Object.assign({}, this.props, {
      x: dipsToPixels(this.props.x),
      y: dipsToPixels(this.props.y),
      width: dipsToPixels(this.props.width),
      height: dipsToPixels(this.props.height),
      strokeWidth: dipsToPixels(this.props.strokeWidth),
    });

    drawRect(canvasContextProvider.getCanvasContext(this), pixelProps);
  }

  render(): ?React.Element<*> {
    return null;
  }
}
Rectangle.defaultProps = { strokeWidth: HAIRLINE_WIDTH };

/** Coordinates and sizes within props should be in pixels. */
function drawRect(canvasContext: CanvasRenderingContext2D, props: Props) {
  const context = canvasContext;

  const { x, y, width, height, fill, stroke, strokeWidth } = props;

  context.save();
  context.lineWidth = strokeWidth;
  context.rect(x, y, width, height);
  if (fill) {
    context.fillStyle = fill;
    context.fillRect(x, y, width, height);
  }

  if (stroke) {
    context.strokeStyle = stroke;
    context.strokeRect(x, y, width, height);
  }
  context.restore();
}

export { Rectangle as default, drawRect };
