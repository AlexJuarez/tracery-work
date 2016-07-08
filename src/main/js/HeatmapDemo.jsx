// @flow

import React from 'react';

import { Rectangle, Surface } from './ui/canvas';

type Props = {
  width: number,
  height: number,
}

function HeatmapDemo(props: Props): React.Element<*> {
  return (
    <Surface
      width={props.width}
      height={props.height}
    >
      <Rectangle
        x={10}
        y={10}
        width={100}
        height={100}
        fill="red"
      />
      <Rectangle
        x={20}
        y={20}
        width={100}
        height={100}
        fill="white"
        stroke="black"
      />
      <Rectangle
        x={30}
        y={30}
        width={100}
        height={100}
        stroke="blue"
        strokeWidth={5}
      />
    </Surface>
  );
}

export default HeatmapDemo;
