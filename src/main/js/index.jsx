// @flow

import { AppContainer } from 'react-hot-loader';
import { Provider } from 'react-redux';
import React from 'react';
import ReactDOM from 'react-dom';

import Redbox from 'redbox-react';
import BrowserHost from './BrowserHost';
import configureStore from './store/configureStore';

require('./index.css');

const holderDiv = document.createElement('div');
holderDiv.classList.add('tracery-app-container');
document.body.appendChild(holderDiv);

const store = configureStore();

function render(Host: ReactClass<*>) {
  ReactDOM.render(
    // TODO: react-hot-loader@3.0.0-beta.2 seems to include a version of Redbox
    // that doesn't like React 15. Once that's fixed, we can stop specifying it
    // explicitly here and remove our dependency on redbox-react.
    <AppContainer errorReporter={Redbox}>
      <Provider store={store} key="provider">
        <Host />
      </Provider>
    </AppContainer>,
    holderDiv);
}

render(BrowserHost);

if (module.hot) {
  module.hot.accept('./BrowserHost', () => {
    /* eslint global-require: 0 */
    const NextHost = require('./BrowserHost').default;
    render(NextHost);
  });
}
