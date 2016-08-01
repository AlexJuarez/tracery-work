// @flow

import invariant from 'invariant';
import React from 'react';
import { connect } from 'react-redux';

import List from './ui/controls/List';
import type { State } from './state';
import * as fromState from './state';

import * as statusCodes from './api/FetchStatus';

type StateProps = {
  items: Array<string>,
  loading: boolean,
  error?: string,
}

type OwnProps = {
  loadingString: string,
}

type Props = StateProps & OwnProps;

function QueryBackedList(props: Props): React.Element<any> {
  if (props.items.length) {
    return <List items={props.items} />;
  }

  if (props.loading) {
    return <span>{props.loadingString}</span>;
  }

  if (props.error) {
    return <span>{props.error}</span>;
  }

  invariant(false, 'Unexpected result');
}

function mapStateToProps(state: State): StateProps {
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

const ConnectedTraceList: (props: OwnProps) => React.Element<*> =
  connect(mapStateToProps)(QueryBackedList);

export default ConnectedTraceList;
