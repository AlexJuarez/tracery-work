// @flow

import React from 'react';
import { connect } from 'react-redux';

import type { State } from './state';
import * as fromState from './state';
import DevTools from './ui/devtools/DevTools';
import View from './View';

type StateProps = {
  viewId: number,
}

type OwnProps = {
  /** Height in dips */
  height: number,
  /** Width in dips */
  width: number
}

type Props = StateProps & OwnProps;

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
      <View viewId={props.viewId} width={props.width} height={props.height} />
      {!window.devToolsExtension && <DevTools />}
    </div>
  );
}

function mapStateToProps(state: State): StateProps {
  return {
    viewId: fromState.getRootViewId(state),
  };
}

const ConnectedApp: (props: OwnProps) => React.Element<*> =
  connect(mapStateToProps)(App);

export default ConnectedApp;
