// @flow

import Hello from './Hello';
import React from 'react';

type Props = {
  /** Height in dips */
  height: number,
  /** Width in dips */
  width: number,
};

/** Root Application element, regardless of host environment. */
function App(props: Props): React.Element<*> {
  return (
    <div
      style={{
        height: props.height,
        width: props.width,
        background: 'red',
        overflow: 'hidden',
      }}
    >
      <Hello />
    </div>
  );
}

export default App;
