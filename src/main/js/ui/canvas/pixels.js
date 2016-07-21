// @flow

export function dipsToPixels(dips: number): number {
  return dips * window.devicePixelRatio;
}

export function pixelsToDips(pixels: number): number {
  return pixels / window.devicePixelRatio;
}
