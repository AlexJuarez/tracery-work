// @flow

import type { Dispatch } from 'redux';

import * as Query from 'query_types';

import createAction from './_createAction';
import startQueryAsync from './_startQueryAsync';
import * as actions from './_types';
import type { State } from '../state';

// TODO: Build a middleware to handle fetching data from the server and shoving it in the cache,
// such that we can just send an action of a particular shape and have all this stuff happen
// without having to write a bunch of new code every time
// TODO: Use normalizr for this
export function loadTraceList(): any {  // FlowFixMe: Need a good Thunk return type
  return (dispatch: Dispatch, getState: () => State) => {
    const query: Query.Query = new Query.Query({
      resultSet: [
        new Query.ResultColumn({
          expression: new Query.Expression({
            valueExpression: new Query.ValueExpression({
              value: 'trace_idx',
            }),
          }),
          aggregation: Query.Aggregation.NONE,
        }),
        new Query.ResultColumn({
          expression: new Query.Expression({
            valueExpression: new Query.ValueExpression({
              value: 'trace_url',
            }),
          }),
          aggregation: Query.Aggregation.NONE,
        }),
      ],
      sourceTables: ['com_facebook_tracery_database_trace_MasterTraceTable'],
    });

    const queryId = startQueryAsync(query, dispatch, getState);
    dispatch(createAction(actions.START_OPEN_TRACE_FLOW, {
      queryId,
    }));
  };
}
