/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

function forEachChild(elem: *, cb: (child: HTMLElement) => void) {
  if (elem != null && elem instanceof HTMLElement) {
    [].forEach.call(elem.children, cb);
  }
}

export default forEachChild;
