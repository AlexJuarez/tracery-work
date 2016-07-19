// @flow

import React from 'react';

type Props = {
  onLoadClicked: (event: SyntheticMouseEvent) => boolean,
}

export default function StartupMenu(props: Props): React.Element<*> {
  return <button onClick={props.onLoadClicked}>Load trace...</button>;
}
