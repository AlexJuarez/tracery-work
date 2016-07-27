/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

export default function pageOffsetLeft(elem: HTMLElement): number {
  let offsetLeft = 0;
  let target = elem;
  while (target != null && target instanceof HTMLElement) {
    offsetLeft += target.offsetLeft;
    target = target.offsetParent;
  }

  return offsetLeft;
}
