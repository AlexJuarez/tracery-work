// @flow

import invariant from 'invariant';

import type { Color } from '../../Color';
import type { Props } from './Props';

const BYTES_PER_PIXEL = 4;

/**
 * A cell renderer based on writing one pixel at a time. This is not true to the specified dip cell
 * sizes at all zoom levels, and thus is probably not the greatest option long-term, but it's a
 * start.
 */
class PixelCellRenderer {
  _canvasContext: CanvasRenderingContext2D;
  _imageData: ImageData;
  _data: Uint8ClampedArray;
  _props: Props;

  /** Coordinates and sizes within Props should be in pixels. */
  constructor(props: Props, canvasContext: CanvasRenderingContext2D) {
    this._canvasContext = canvasContext;
    this._props = props;
    this._imageData = this._canvasContext.getImageData(0, 0, props.width, props.height);

    // Flow's model for ImageData is incorrect, at least for Chrome. This invariant keeps it from
    // complaining about the assignment that follows.
    invariant(this._imageData.data instanceof Uint8ClampedArray, 'Unexpected data array type.');
    this._data = this._imageData.data;
  }

  addCell(position: { row: number, col: number }, fill: Color) {
    const { row, col } = position;
    const top = this._props.y + (row * this._props.cellHeight);
    const bottom = this._props.y + top + this._props.cellHeight;
    const left = this._props.x + (col * this._props.cellWidth);
    const right = this._props.x + left + this._props.cellWidth;

    invariant(
      bottom <= this._props.y + this._props.height && right <= this._props.x + this._props.width,
      'Too much data for the map size.');

    const stroke = this._props.stroke || fill;

    for (let y = top; y <= bottom; y++) {
      for (let x = left; x <= right; x++) {
        const color =
          (x === left || y === top || x === right || y === bottom) ?
          stroke : fill;
        this._renderPixel(x, y, color);
      }
    }
  }

  _renderPixel(x: number, y: number, color: Color) {
    const redIndex = (y * this._props.width + x) * BYTES_PER_PIXEL;
    const greenIndex = redIndex + 1;
    const blueIndex = redIndex + 2;
    const alphaIndex = redIndex + 3;

    this._data[redIndex] = color.r;
    this._data[blueIndex] = color.g;
    this._data[greenIndex] = color.b;
    this._data[alphaIndex] = color.a;
  }

  render() {
    const context = this._canvasContext;
    if (!context) {
      return;
    }

    context.putImageData(this._imageData, 0, 0);
  }
}

export default PixelCellRenderer;
