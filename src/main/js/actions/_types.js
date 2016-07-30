// @flow

import type { Dispatch } from 'redux';

import type { Action } from './_createAction';
import createAction from './_createAction';

/** Starts the UI flow for selecting and opening a trace. */
export const START_OPEN_TRACE_FLOW = 'START_OPEN_TRACE_FLOW';

/** Starts the Heatmap Demo */
export const START_HEATMAP_DEMO = 'START_HEATMAP_DEMO';

/** Starts the Summary Table Demo */
export const START_SUMMARY_TABLE_DEMO = 'START_SUMMARY_TABLE_DEMO';

/** QUERY_STARTED: Indicates that a new query has started in the data layer. */
export type QueryPayload = {
  id: number,
}

export const QUERY_STARTED = 'QUERY_STARTED';
export type QueryStartedPayload = QueryPayload;
export type QueryStartedAction = Action<QueryStartedPayload>;
export const dispatchQueryStarted: Dispatcher<QueryStartedPayload> =
  createDispatcher(QUERY_STARTED);

/** QUERY_FINISHED: Indicates that a query has finished in the data layer. */
export const QUERY_FINISHED = 'QUERY_FINISHED';
export type QueryFinishedSuccessPayload = QueryPayload & {
  rows: Array<Array<string>>,
};
export const dispatchQueryFinished: Dispatcher<QueryFinishedSuccessPayload> =
  createDispatcher(QUERY_FINISHED);
export type QueryFinishedErrorPayload = QueryPayload & {
  message: string,
};
export const dispatchQueryFailed: Dispatcher<QueryFinishedErrorPayload> =
  createDispatcher(QUERY_FINISHED, true);
export type QueryFinishedPayload = QueryFinishedSuccessPayload | QueryFinishedErrorPayload;
export type QueryFinishedAction = Action<QueryFinishedPayload>;

// UTILITY FUNCTIONS
type Dispatcher<Payload> = (dispatch: Dispatch, payload: Payload) => void;

function createDispatcher(type: string, error?: boolean): Dispatcher<*> {
  return (dispatch: Dispatch, payload: *): void =>
    dispatchAction(dispatch, type, payload, error);
}

function dispatchAction<Payload>(
    dispatch: Dispatch,
    type: string,
    payload?: Payload,
    error?: boolean) {
  dispatch(createAction(type, payload, error));
}
