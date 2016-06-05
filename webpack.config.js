var path = require('path');

module.exports = {
  devtool: 'source-map',
  entry: './src/index.jsx',
  module: {
    loaders: [{
      test: /\.jsx?$/,
      include: path.join(__dirname, 'src'),
      loader: 'babel-loader',
    }],
  },
  output: {
    path: path.join(__dirname, 'build'),
    filename: 'index.bundle.js',
  },
};
