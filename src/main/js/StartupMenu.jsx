// @flow

import React from 'react';

type Props = {
  onDemoClicked: (event: SyntheticMouseEvent) => boolean,
  onLoadClicked: (event: SyntheticMouseEvent) => boolean,
  onSummaryTableDemoClicked: (event: SyntheticMouseEvent) => boolean,
}

export default function StartupMenu(props: Props): React.Element<*> {
  return (
    <div>
      <button onClick={props.onLoadClicked}>Load trace...</button>
      <button onClick={props.onDemoClicked}>Show heatmap demo...</button>
      <button onClick={props.onSummaryTableDemoClicked}>Show Summary Table demo...</button>
    </div>
  );
}
