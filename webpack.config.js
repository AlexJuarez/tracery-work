var HtmlWebpackPlugin = require('html-webpack-plugin');
var path = require('path');

module.exports = {
  devtool: 'source-map',
  entry: [
    'webpack-dev-server/client?http://0.0.0.0:8080',
    'webpack/hot/only-dev-server',
    'react-hot-loader/patch',
    './src/index.jsx',
  ],
  module: {
    loaders: [{
      test: /\.jsx?$/,
      include: path.join(__dirname, 'src'),
      loader: 'babel-loader',
    }],
  },
  output: {
    path: path.join(__dirname, 'build'),
    publicPath: '/',
    filename: 'index.bundle.js',
  },
  plugins: [
    new HtmlWebpackPlugin({
      title: "Tracery 0.1",
    }),
  ],
  resolve: {
    extensions: ['', '.js', '.jsx'],
  },
};
