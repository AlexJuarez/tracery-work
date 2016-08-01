// @flow

import { connect } from 'react-redux';
import React from 'react';

import type { State } from './state';
import { getAppMode } from './state';
import * as appModes from './state/appMode';

import HeatmapDemo from './HeatmapDemo';
import SummaryTableDemo from './SummaryTableDemo';
import StartupMenu from './StartupMenu';
import QueryBackedList from './QueryBackedList';
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
  onSummaryTableDemoClicked: (event: SyntheticMouseEvent) => boolean,
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
        onSummaryTableDemoClicked={props.onSummaryTableDemoClicked}
      />);
    case appModes.SELECT_TRACE:
      return <QueryBackedList loadingString="Loading trace list..." />;
    case appModes.HEATMAP_DEMO:
      return <HeatmapDemo width={props.width} height={props.height} />;
    case appModes.SUMMARY_TABLE_DEMO:
      return <SummaryTableDemo />;
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
  onLoadClicked: actions.loadTraceList,
  onDemoClicked: actions.startHeatmapDemo,
  onSummaryTableDemoClicked: actions.startSummaryTableDemo,
};

export default connect(mapStateToProps, mapDispatchToProps)(App);
