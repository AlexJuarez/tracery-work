// @flow

import React from 'react';

import DevTools from './ui/devtools/DevTools';
import View from './View';

type Props = {
  /** Height in dips */
  height: number,
  /** Width in dips */
  width: number,
};

/** Root Application element, regardless of host environment. */
export default function App(props: Props): React.Element<*> {
  return (
    <div
      style={{
        height: props.height,
        width: props.width,
        overflow: 'hidden',
      }}
    >
      <View width={props.width} height={props.height} />
      {!window.devToolsExtension && <DevTools />}
    </div>
  );
}
