var React = require('react');
var ReactDOM = require('react-dom');

var holderDiv = document.createElement("div");
document.body.appendChild(holderDiv);

ReactDOM.render(
		<h1>Hello, world!</h1>,
		holderDiv);
