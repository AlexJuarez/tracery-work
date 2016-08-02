/**
 * Copyright 2004-present Facebook. All Rights Reserved.
 *
 * @flow
 */

/* eslint no-param-reassign: "off" */

function getClasses(className: string): Array<string> {
  return className.split(/\s/g);
}

export function addClass(element: HTMLElement, className: string) {
  element.className = [...getClasses(element.className), className].join(' ');
}

export function removeClass(element: HTMLElement, className: string) {
  const classes = getClasses(element.className);
  const index = classes.indexOf(className);
  if (index !== -1) {
    classes.splice(classes.indexOf(className), 1);
    element.className = classes.join(' ');
  }
}
