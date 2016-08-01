// @flow

import { combineReducers } from 'redux';

import type { Queries } from './_queries';
import queries from './_queries';

// TODO: Eventually the shape of the cache will be determined by what tables and queries have been
// defined in the app's configuration.
export type Cache = {
  queries: Queries,
}

export default combineReducers({ queries });
