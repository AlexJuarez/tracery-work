// @flow

import { createStore, applyMiddleware, compose } from 'redux';
import type { Store } from 'redux';
import thunk from 'redux-thunk';
import createLogger from 'redux-logger';
import state from '../state';
import type { State } from '../state';
import DevTools from '../ui/devtools/DevTools';

export default function configureStore(preloadedState?: State): Store {
  const store = createStore(
    state,
    preloadedState,
    compose(
      applyMiddleware(thunk, createLogger()),
      window.devToolsExtension ? window.devToolsExtension() : DevTools.instrument()
    )
  );

  if (module.hot) {
    // Enable Webpack hot module replacement for reducers
    module.hot.accept('../state', () => {
      /* eslint global-require: 0 */
      const nextRootReducer = require('../state').default;
      store.replaceReducer(nextRootReducer);
    });
  }

  return store;
}
