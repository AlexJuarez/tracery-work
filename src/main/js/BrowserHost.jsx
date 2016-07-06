// @flow

import React from 'react';

import App from './App';

type Props = {};

/**
 * Adapts the App to be hosted in the browser, including:
 * - Resizing the App element whenever the browser window size changes.
 */
class BrowserHost extends React.Component {
  static defaultProps: Props;

  constructor(props: Props) {
    super(props);

    this.state = getDocumentClientDimensions();
  }

  state: {
    height: number;
    width: number;
  };

  componentDidMount() {
    window.onresize = (): void => this.handleResize();
  }

  componentWillUnmount() {
    window.onresize = undefined;
  }

  props: Props;

  handleResize() {
    window.requestAnimationFrame((): void => this.setState(getDocumentClientDimensions()));
  }

  render(): React.Element<*> {
    return <div><App width={this.state.width} height={this.state.height} /></div>;
  }
}

function getDocumentClientDimensions(): Object {
  return {
    width: document.body.clientWidth,
    height: document.body.clientHeight,
  };
}

export default BrowserHost;
