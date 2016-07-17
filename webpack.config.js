// @flow

const HtmlWebpackPlugin = require('html-webpack-plugin');
const path = require('path');

const THRIFT_LIB_SRC_DIR = path.join(__dirname, 'lib/thrift');
const THRIFT_GENERATED_SRC_DIR = path.join(__dirname, 'build/generated-sources/thrift/gen-js');

module.exports = {
  devtool: 'source-map',
  entry: [
    'webpack-dev-server/client?http://0.0.0.0:8080',
    'webpack/hot/only-dev-server',
    'react-hot-loader/patch',
    './src/main/js/index.jsx',
  ],
  module: {
    loaders: [{
      test: /\.jsx?$/,
      include: path.join(__dirname, 'src'),
      loader: 'babel-loader',
    },

    // Thrift itself and its generated JavaScript code are not in module format. The below
    // configurations do loader magic to make them behave like modules
    {
      test: /thrift\.js$/,
      include: THRIFT_LIB_SRC_DIR,
      loader: 'exports-loader?Thrift',
    },
    {
      test: /tracery_types\.js$/,
      include: THRIFT_GENERATED_SRC_DIR,
      loader: 'exports-loader?TraceInfo!imports-loader?Thrift=thrift',
    },
    {
      test: /TraceryService\.js$/,
      include: THRIFT_GENERATED_SRC_DIR,
      loader: 'exports-loader?TraceryServiceClient!' +
              'imports-loader?' +
                'Thrift=thrift,' +
                'tracery_types,' +
                'TraceInfo=>tracery_types.TraceInfo',
    },
    {
      test: /\.css$/,
      loader: 'style-loader!css-loader',
    }],
  },
  output: {
    devtoolModuleFilenameTemplate: '/[resource-path]',
    path: path.join(__dirname, 'build'),
    publicPath: '/',
    filename: 'index.bundle.js',
  },
  plugins: [
    new HtmlWebpackPlugin({
      title: 'Tracery 0.1',
    }),
  ],
  resolve: {
    extensions: ['', '.js', '.jsx'],
    root: [THRIFT_GENERATED_SRC_DIR, THRIFT_LIB_SRC_DIR],
  },
};
