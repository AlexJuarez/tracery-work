// @flow

import { connect } from 'react-redux';
import React from 'react';

import type { State } from './state';
import { getAppMode } from './state';
import * as appModes from './state/appMode';

import StartupMenu from './StartupMenu';
import List from './ui/controls/List';
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
      <DevTools />
    </div>
  );
}

function renderContent(props: Props): ?React.Element<any> {
  switch (props.appMode) {
    case appModes.STARTUP:
      return <StartupMenu onLoadClicked={props.onLoadClicked} />;
    case appModes.SELECT_TRACE:
      return <List items={['Hello World!']} />;
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
};

export default connect(mapStateToProps, mapDispatchToProps)(App);
