// @flow

import { connect } from 'react-redux';
import React from 'react';

import type { State } from './state';
import { getAppMode } from './state';
import * as appModes from './state/appMode';

import HeatmapDemo from './HeatmapDemo';
import StartupMenu from './StartupMenu';
import TraceList from './TraceList';
import DevTools from './ui/devtools/DevTools';

import * as actions from './actions';

type StateProps = {
  appMode: string,
}

type Props = {
  /** Height in dips */
  height: number,
  /** Width in dips */
  width: number,
  onLoadClicked: (event: SyntheticMouseEvent) => boolean,
  onDemoClicked: (event: SyntheticMouseEvent) => boolean,
} & StateProps;

/** Root Application element, regardless of host environment. */
function App(props: Props): React.Element<*> {
  return (
    <div
      style={{
        height: props.height,
        width: props.width,
        overflow: 'hidden',
      }}
    >
      {renderContent(props)}
      {!window.devToolsExtension && <DevTools />}
    </div>
  );
}

function renderContent(props: Props): ?React.Element<any> {
  switch (props.appMode) {
    case appModes.STARTUP:
      return (<StartupMenu
        onLoadClicked={props.onLoadClicked}
        onDemoClicked={props.onDemoClicked}
      />);
    case appModes.SELECT_TRACE:
      return <TraceList />;
    case appModes.HEATMAP_DEMO:
      return <HeatmapDemo width={props.width} height={props.height} />;
    default:
      return null;
  }
}

function mapStateToProps(state: State): StateProps {
  return {
    appMode: getAppMode(state),
  };
}

const mapDispatchToProps = {
  onLoadClicked: actions.loadTrace,
  onDemoClicked: actions.startHeatmapDemo,
};

export default connect(mapStateToProps, mapDispatchToProps)(App);
