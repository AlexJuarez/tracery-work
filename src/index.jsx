// @flow

import { AppContainer } from 'react-hot-loader';
import React from 'react';
import ReactDOM from 'react-dom';

import BrowserHost from './BrowserHost';

require('./index.css');

const holderDiv = document.createElement('div');
holderDiv.classList.add('tracery-app-container');
document.body.appendChild(holderDiv);

ReactDOM.render(
  <AppContainer>
    <BrowserHost />
  </AppContainer>,
  holderDiv);

if (module.hot) {
  module.hot.accept('./BrowserHost', () => {
    /* eslint global-require: 0 */
    const NextHost = require('./BrowserHost').default;
    ReactDOM.render(
      <AppContainer>
        <NextHost />
      </AppContainer>,
      holderDiv);
  });
}
