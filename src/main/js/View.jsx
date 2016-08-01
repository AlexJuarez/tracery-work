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

import * as actions from './actions';

type StateProps = {
  viewType: string,
}

type OwnProps = {
  viewId: number,
  /** Height in dips */
  height: number,
  /** Width in dips */
  width: number,
}

type Props = OwnProps & StateProps & {
  onLoadClicked: (event: SyntheticMouseEvent) => boolean,
  onDemoClicked: (event: SyntheticMouseEvent) => boolean,
  onSummaryTableDemoClicked: (event: SyntheticMouseEvent) => boolean,
  onTraceClicked: (key: any, event: SyntheticMouseEvent) => boolean,
  onFileClicked: (key: any, event: SyntheticMouseEvent) => boolean,
};

function View(props: Props): ?React.Element<any> {
  switch (props.viewType) {
    case viewTypes.STARTUP:
      return (<StartupMenu
        onLoadClicked={props.onLoadClicked}
        onDemoClicked={props.onDemoClicked}
        onSummaryTableDemoClicked={props.onSummaryTableDemoClicked}
      />);
    // TODO: Move loading string to state
    // TODO: Introduce middleware for async actions, move click handler to state
    // TODO: Change ViewType to be QUERY_BACKED_LIST and just spread the viewSpecificState to props
    case viewTypes.SELECT_TRACE:
      return (<QueryBackedList
        viewId={props.viewId}
        loadingString="Loading trace list..."
        onItemClicked={props.onTraceClicked}
      />);
    case viewTypes.SELECT_FILE:
      return (<QueryBackedList
        viewId={props.viewId}
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

function mapStateToProps(state: State, ownProps: OwnProps): StateProps {
  return {
    viewType: getViewType(state, ownProps.viewId),
  };
}

const mapDispatchToProps = {
  onLoadClicked: actions.loadTraceList,
  onDemoClicked: actions.startHeatmapDemo,
  onSummaryTableDemoClicked: actions.startSummaryTableDemo,
  onTraceClicked: (traceId: number): boolean => actions.loadFileList(traceId, 'R'),
  onFileClicked: actions.doNothing,
};

export default connect(mapStateToProps, mapDispatchToProps)(View);
