// @flow

import invariant from 'invariant';
import React from 'react';
import { connect } from 'react-redux';

import List from './ui/controls/List';
import type { State } from './state';
import * as fromState from './state';

import * as statusCodes from './api/FetchStatus';

// TODO: Probably loading/error stuff goes elsewhere too
type Props = {
  items: Array<string>,
  loading: boolean,
  error?: string,
}

function TraceList(props: Props): React.Element<any> {
  if (props.items.length) {
    return <List items={props.items} />;
  }

  if (props.loading) {
    return <span>Loading trace list...</span>;
  }

  if (props.error) {
    return <span>{props.error}</span>;
  }

  invariant(false, 'Unexpected result');
}

function mapStateToProps(state: State): Props {
  const queryId = fromState.getQueryId(state);

  const statusCode = fromState.getQueryFetchStatusCode(state, queryId);
  if (statusCode === statusCodes.FAILURE) {
    return {
      items: [],
      loading: false,
      error: fromState.getQueryFetchErrorMessage(state, queryId) || 'Unknown error',
    };
  }

  return {
    items: fromState.getQueryRows(state, queryId)
      .map((row: Array<string>): string => row[1]),
    loading: statusCode === statusCodes.IN_PROGRESS,
  };
}

const ConnectedTraceList = connect(mapStateToProps)(TraceList);

// TODO: Flow doesn't typecheck this.
export default ConnectedTraceList;
