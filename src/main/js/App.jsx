// @flow

import { connect } from 'react-redux';
import React from 'react';

import type { State } from './state';
import { getAppMode } from './state';
import * as appModes from './state/appMode';

import Hello from './Hello';
import DevTools from './ui/devtools/DevTools';

type StateProps = {
  appMode: string,
}

type Props = {
  /** Height in dips */
  height: number,
  /** Width in dips */
  width: number,
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
      return <Hello />;
    default:
      return null;
  }
}

function mapStateToProps(state: State): StateProps {
  return {
    appMode: getAppMode(state),
  };
}

export default connect(mapStateToProps)(App);
