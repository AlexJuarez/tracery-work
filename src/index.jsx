// @flow

import { AppContainer } from 'react-hot-loader';
import App from './App';
import React from 'react';
import ReactDOM from 'react-dom';

const holderDiv = document.createElement('div');
document.body.appendChild(holderDiv);

ReactDOM.render(
  <AppContainer>
    <App />
  </AppContainer>,
  holderDiv);

if (module.hot) {
  module.hot.accept('./App', () => {
    /* eslint global-require: 0 */
    const NextApp = require('./App').default;
    ReactDOM.render(
      <AppContainer>
        <NextApp />
      </AppContainer>,
      holderDiv);
  });
}
