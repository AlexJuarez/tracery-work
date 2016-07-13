// @flow

export type Color = {
  r: number;
  g: number;
  b: number;
  a: number;
}

export function rgba(color: Color): string {
  return `rgba(${color.r}, ${color.g}, ${color.b}, ${color.a})`;
}
