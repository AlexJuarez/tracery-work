include "table.thrift"
namespace java com.facebook.tracery.thrift.query

enum UnaryOperation {
  NEGATE,     // -
  NOT
}
struct UnaryExpression {
  1: required UnaryOperation operation;
  2: required Expression operand;
}

enum BinaryOperation {
  CONCATENATE,// ||
  MULTIPLY,   // *
  DIVIDE,     // /
  MODULO,     // %
  ADD,        // +
  SUBTRACT,   // -
  SHIFT_LEFT, // <<
  SHIFT_RIGHT,// >>
  BITWISE_AND,// &
  BITWISE_OR, // |
  LT,         // <
  LE,         // <=
  GE,         // >=
  GT,         // >
  EQ,         // =, ==
  NEQ,        // !=, <>
  IN,         // IN
  LIKE,       // LIKE
  GLOB,       // GLOB
  REGEXP,     // REGEXP
  AND,        // AND
  OR          // OR
}
struct BinaryExpression {
  1: required Expression leftOperand;
  2: required BinaryOperation operation;
  3: required Expression rightOperand;
}

enum TrinaryOperation {
  BETWEEN
}
struct TrinaryExpression {
  1: required Expression leftOperand;
  2: required TrinaryOperation operation;
  3: required Expression rightOperand0;
  4: required Expression rightOperand1;
}

struct ValueExpression {
  1: required string value;
}

union Expression {
  1: UnaryExpression unaryExpression;
  2: BinaryExpression binaryExpression;
  3: TrinaryExpression trinaryExpression;
  4: ValueExpression valueExpression;
}

struct Ordering {
  1: required string columnName;
  2: required bool ascending = true;
}

struct Grouping {
  1: required string columnNameOrIndex;
}

enum Aggregation {
  NONE,
  AVG,
  MIN,
  MAX,
  SUM,
  COUNT
}

struct ResultColumn {
  1: required Expression expression;
  2: required Aggregation aggregation = Aggregation.NONE;
  3: optional string resultAlias; // AS
}

struct Query {
  // SELECT - define the result set columns and (if applicable) grouping aggregates
  // -defaults to '*' if nothing specified
  1: optional list<ResultColumn> resultSet;

  // DISTINCT - eliminate duplicate rows in the result set
  2: optional bool distinct = false;

  // FROM - source tables used to create the working table
  3: required list<string> sourceTables;

  // WHERE - filter rows from the working table
  4: optional Expression where;

  // GROUP BY - group rows in the working table by similar values
  5: optional list<Grouping> groupBy;

  // HAVING - filter rows out of the grouped table - requires GROUP BY
  6: optional Expression having;

  // ORDER BY - sort the rows of the result set
  7: optional list<Ordering> orderBy;

  // LIMIT - limits the result set to the specified number of rows
  8: optional i32 limit = -1;

  // OFFSET - skip over rows at the beginning of the result set - requires LIMIT
  9: optional i32 offset = -1;
}

struct QueryResultRow {
  1: required list<string> cells;
}

struct QueryResult {
  1: required list<string> columnNames;
  2: required list<table.TableColumnType> columnTypes;
  3: required list<QueryResultRow> rows;
}
