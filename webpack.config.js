// @flow

const HtmlWebpackPlugin = require('html-webpack-plugin');
const path = require('path');

const THRIFT_LIB_SRC_DIR = path.join(__dirname, 'lib/js/thrift');
const THRIFT_GENERATED_SRC_DIR = path.join(__dirname, 'build/generated-sources/thrift/gen-js');

module.exports = {
  devtool: 'source-map',
  entry: [
    //    'webpack-dev-server/client?http://0.0.0.0:8080',
    //    'webpack/hot/only-dev-server',
    //    'react-hot-loader/patch',
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
      test: /table_types\.js$/,
      include: THRIFT_GENERATED_SRC_DIR,
      // IMPORTANT: Any time a new type is added to table_types, it must be added to the exports
      // list here too
      loader: 'exports-loader?' +
                'RawType,' +
                'Category,' +
                'Unit,' +
                'Structure,' +
                'TableColumnType,' +
                'TableColumnInfo,' +
                'TableInfo!' +
              'imports-loader?Thrift=thrift',
    },
    {
      test: /query_types\.js$/,
      include: THRIFT_GENERATED_SRC_DIR,
      // IMPORTANT: Any time a new type is added to query_types, it must be added to the exports
      // list here too
      loader: 'exports-loader?' +
                'UnaryOperation,' +
                'BinaryOperation,' +
                'TrinaryOperation,' +
                'Aggregation,' +
                'UnaryExpression,' +
                'BinaryExpression,' +
                'TrinaryExpression,' +
                'ValueExpression,' +
                'Expression,' +
                'Ordering,' +
                'Grouping,' +
                'ResultColumn,' +
                'Query,' +
                'QueryResult!' +
              'imports-loader?Thrift=thrift,{TableColumnType}=table_types',
    },
    {
      test: /tracery_types\.js$/,
      include: THRIFT_GENERATED_SRC_DIR,
      // IMPORTANT: Any time a new type is added to tracery_types, it must be added to the exports
      // list here too
      loader: 'exports-loader?TraceInfo=TraceInfo!imports-loader?Thrift=thrift',
    },
    {
      test: /TraceryService\.js$/,
      include: THRIFT_GENERATED_SRC_DIR,
      // IMPORTANT: Any time TraceryServiceClient starts using a new type, an import must be added
      // to the imports list here too
      loader: 'exports-loader?TraceryServiceClient=TraceryServiceClient!' +
              'imports-loader?' +
                'Thrift=thrift,' +
                '{TraceInfo}=tracery_types',
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
