// @flow

import invariant from 'invariant';

import type { Action, QueryStartedPayload, QueryFinishedPayload } from '../actions';
import * as actions from '../actions';

import type { Query } from './_query';
import query from './_query';

export type Queries = {
  numQueries: number,
  items: {
    [id: number]: Query
  },
};

export default function queries(
    state: Queries = { numQueries: 0, items: {} },
    action: Action<*>): Queries {
  switch (action.type) {
    case actions.QUERY_STARTED: {
      invariant(action.payload, 'Expected a payload');
      const payload: QueryStartedPayload = action.payload;
      invariant(payload.id === state.numQueries, 'Expected the next query.');

      return {
        numQueries: state.numQueries + 1,
        items: {
          ...state.items,
          [payload.id]: query(undefined, action),
        },
      };
    }
    case actions.QUERY_FINISHED: {
      invariant(action.payload, 'Expected a payload');
      const payload: QueryFinishedPayload = action.payload;

      return {
        ...state,
        items: {
          ...state.items,
          [payload.id]: query(state.items[payload.id], action),
        },
      };
    }

    default:
      return state;
  }
}
