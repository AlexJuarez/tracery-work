// @flow

import { connect } from 'react-redux';
import React from 'react';

import type { State } from './state';
import { getViewType } from './state';
import * as viewTypes from './state/viewType';

import HeatmapDemo from './HeatmapDemo';
import SummaryTableDemo from './SummaryTableDemo';
import StartupMenu from './StartupMenu';
import QueryBackedList from './QueryBackedList';
import DevTools from './ui/devtools/DevTools';

import * as actions from './actions';

type StateProps = {
  viewType: string,
}

type Props = {
  /** Height in dips */
  height: number,
  /** Width in dips */
  width: number,
  onLoadClicked: (event: SyntheticMouseEvent) => boolean,
  onDemoClicked: (event: SyntheticMouseEvent) => boolean,
  onSummaryTableDemoClicked: (event: SyntheticMouseEvent) => boolean,
  onTraceClicked: (key: any, event: SyntheticMouseEvent) => boolean,
  onFileClicked: (key: any, event: SyntheticMouseEvent) => boolean,
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
  switch (props.viewType) {
    case viewTypes.STARTUP:
      return (<StartupMenu
        onLoadClicked={props.onLoadClicked}
        onDemoClicked={props.onDemoClicked}
        onSummaryTableDemoClicked={props.onSummaryTableDemoClicked}
      />);
    case viewTypes.SELECT_TRACE:
      return (<QueryBackedList
        loadingString="Loading trace list..."
        onItemClicked={props.onTraceClicked}
      />);
    case viewTypes.SELECT_FILE:
      return (<QueryBackedList
        loadingString="Loading file list..."
        onItemClicked={props.onFileClicked}
      />);
    case viewTypes.HEATMAP_DEMO:
      return <HeatmapDemo width={props.width} height={props.height} />;
    case viewTypes.SUMMARY_TABLE_DEMO:
      return <SummaryTableDemo />;
    default:
      return null;
  }
}

function mapStateToProps(state: State): StateProps {
  return {
    viewType: getViewType(state),
  };
}

const mapDispatchToProps = {
  onLoadClicked: actions.loadTraceList,
  onDemoClicked: actions.startHeatmapDemo,
  onSummaryTableDemoClicked: actions.startSummaryTableDemo,
  onTraceClicked: (traceId: number): boolean => actions.loadFileList(traceId, 'R'),
  onFileClicked: actions.doNothing,
};

export default connect(mapStateToProps, mapDispatchToProps)(App);
