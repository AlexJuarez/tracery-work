// @flow

import invariant from 'invariant';

import { rgba } from '../../Color';
import type { Color } from '../../Color';
import type { Props } from './Props';

/** Coordinates and sizes in Props should be in pixels. */
class SpriteCellRenderer {
  _spriteCanvas: HTMLCanvasElement;
  _canvasContext: CanvasRenderingContext2D;
  _props: Props;

  constructor(props: Props, canvasContext: CanvasRenderingContext2D) {
    this._props = props;
    this._canvasContext = canvasContext;

    this._spriteCanvas = this._initializeSpriteCanvas();
  }

  _initializeSpriteCanvas(): HTMLCanvasElement {
    const spriteCanvas: HTMLCanvasElement = document.createElement('canvas');

    spriteCanvas.width = this._props.colors.length * this._props.cellWidth + 1;
    spriteCanvas.height = this._props.cellHeight + 1;

    const canvasContext = (spriteCanvas.getContext('2d'): ?CanvasRenderingContext2D);
    invariant(canvasContext, 'Expected a context.');

    this._props.colors.forEach(
      (color: Color, index: number): void => this._drawSprite(canvasContext, index, color));

    return spriteCanvas;
  }

  _drawSprite(canvasContext: CanvasRenderingContext2D, index: number, color: Color) {
    const context = canvasContext;
    const stroke = this._props.stroke || color;
    const left = index * this._props.cellWidth;
    const top = 0;

    context.fillStyle = rgba(color);
    context.strokeStyle = rgba(stroke);

    context.fillRect(left, top, this._props.cellWidth, this._props.cellHeight);
    context.strokeRect(left + 0.5, top + 0.5, this._props.cellWidth, this._props.cellHeight);
  }

  addCell(position: { row: number, col: number }, fillIndex: number) {
    const { row, col } = position;

    // Cell width and height extended by 1 to get the right and bottom borders
    const cellWidth = this._props.cellWidth + 1;
    const cellHeight = this._props.cellHeight + 1;

    this._canvasContext.drawImage(
      this._spriteCanvas,
      fillIndex * this._props.cellWidth,  // sx
      0,  // sy
      cellWidth,  // sWidth
      cellHeight,  // sHeight
      this._props.x + col * this._props.cellWidth,  // dx
      this._props.y + row * this._props.cellHeight,  // dy
      cellWidth,  // dWidth
      cellHeight  // dHeight
    );
  }

  render() {
  }
}

export default SpriteCellRenderer;
