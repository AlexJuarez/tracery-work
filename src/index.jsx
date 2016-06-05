import { AppContainer } from 'react-hot-loader';
var App = require('./App');
var React = require('react');
var ReactDOM = require('react-dom');

var holderDiv = document.createElement("div");
document.body.appendChild(holderDiv);

ReactDOM.render(
  <AppContainer>
    <App />
  </AppContainer>,
  holderDiv);

if (module.hot) {
  module.hot.accept('./App', () => {
    const NextApp = require('./App');
    ReactDOM.render(
      <AppContainer>
        <NextApp />
      </AppContainer>,
      holderDiv);
  });
}
