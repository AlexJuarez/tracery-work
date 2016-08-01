// @flow

import type { Dispatch } from 'redux';

import * as Query from 'query_types';
import Thrift from 'thrift';
import { TraceryServiceClient } from 'TraceryService';

import type { PromiseRejectFunction, PromiseResolveFunction } from './_promiseTypes';
import * as actions from './_types';
import type { State } from '../state';
import * as fromState from '../state';

// For now we hardcode this. When the frontend starts launching the data layer itself, it'll be
// determining the port more dynamically, and when we start to support remote backends the host
// will become dynamic as well.
const DATA_LAYER_URL = 'http://localhost:9090/api';

export type ServerResponse = Query.QueryResult | Thrift.TApplicationException;

/**
 * Starts the given query as an async operation, returning the index of the query result in the
 * 'queries' table in the Redux store. QUERY_STARTED and QUERY_FINISHED actions will report query
 * progress.
 */
export default function startQueryAsync(
  query: Query.Query,
  dispatch: Dispatch,
  getState: () => State): number {
  const state = getState();
  const nextQueryId = fromState.getNumQueries(state);

  actions.dispatchQueryStarted(dispatch, { id: nextQueryId });

  runQuery(query).then((result: Query.QueryResult): void => actions.dispatchQueryFinished(
    dispatch,
    {
      id: nextQueryId,
      rows: result.rows.map((row: Query.QueryResultRow): Array<string> => row.cells),
    })
  ).catch((error: Thrift.TApplicationException): void => actions.dispatchQueryFailed(
    dispatch,
    {
      id: nextQueryId,
      message: error.message,
    })
  );

  return nextQueryId;
}

function runQuery(query: Query.Query): Promise<Query.QueryResult> {
  return new Promise(
    (resolve: PromiseResolveFunction<Query.QueryResult>,
      reject: PromiseRejectFunction<Thrift.TApplicationException>) => {
      const transport = new Thrift.Transport(DATA_LAYER_URL);
      const protocol = new Thrift.TJSONProtocol(transport);
      const client: TraceryServiceClient = new TraceryServiceClient(protocol);

      // TODO (#66): If I give a callback typed to only handle one of the two possible
      // response types below (Query.QueryResponse OR Thrift.TApplicationException)
      // Flow does not get mad. Figure out why, file a bug.
      client.query(query, (result: ServerResponse) => {
        if (result instanceof Thrift.TApplicationException) {
          reject(result);
        } else {
          resolve(result);
        }
      });
    });
}
