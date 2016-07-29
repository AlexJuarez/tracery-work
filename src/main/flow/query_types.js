// IMPORTANT!
// This declaration is manually ported from the TypeScript declaration created by thrift at
// build/generated-sources/thrift/gen-js/query_types.d.ts. Whenever updating the Thrift schema
// for any types defined in this file, one should re-port the new version. (Porting is not that
// hard, so it's likely better to just re-port it than to try to determine exactly the changes
// required.)
//
// @flow

declare module query_types {
  declare var UnaryOperation: {
    NEGATE: number;
    NOT: number;
  }

  declare var BinaryOperation: {
    LT: number;
    LE: number;
    GE: number;
    GT: number;
    EQ: number;
    NEQ: number;
  }

  declare var TrinaryOperation: {
    BETWEEN: number;
  }

  declare var Aggregation: {
    NONE: number;
    AVG: number;
    MIN: number;
    MAX: number;
    SUM: number;
    COUNT: number;
  }

  declare class UnaryExpression {
    operation: number;
    operand: Expression;

    constructor(args?: { operation: number, operand: Expression }): void;
  }

  declare class BinaryExpression {
    leftOperand: Expression;
    operation: number;
    rightOperand: Expression;

    constructor(args?: { leftOperand: Expression, operation: number, rightOperand: Expression }): void;
  }

  declare class TrinaryExpression {
    leftOperand: Expression;
    operation: number;
    rightOperand0: Expression;
    rightOperand1: Expression;

    constructor(args?: { leftOperand: Expression, operation: number, rightOperand0: Expression, rightOperand1: Expression }): void;
  }

  declare class ValueExpression {
    value: string;

    constructor(args?: { value: string; }): void;
  }

  declare class Expression {
    unaryExpression: UnaryExpression;
    binaryExpression: BinaryExpression;
    trinaryExpression: TrinaryExpression;
    valueExpression: ValueExpression;

    constructor(args?: { unaryExpression?: UnaryExpression, binaryExpression?: BinaryExpression, trinaryExpression?: TrinaryExpression, valueExpression?: ValueExpression }): void;
  }

  declare class Ordering {
    columnName: string;
    ascending: boolean;

    constructor(args?: { columnName: string, ascending: boolean }): void;
  }

  declare class Grouping {
    columnNameOrIndex: string;

    constructor(args?: { columnNameOrIndex: string }): void;
  }

  declare class ResultColumn {
    expression: Expression;
    aggregation: number;
    resultAlias: string;

    constructor(args?: { expression: Expression, aggregation: number, resultAlias?: string }): void;
  }

  declare class Query {
    resultSet: Array<ResultColumn>;
    distinct: boolean;
    sourceTables: Array<string>;
    where: Expression;
    groupBy: Array<Grouping>;
    having: Expression;
    orderBy: Array<Ordering>;
    limit: number;
    offset: number;

    constructor(args?: { resultSet?: Array<ResultColumn>, distinct?: boolean, sourceTables: Array<string>, where?: Expression, groupBy?: Array<Grouping>, having?: Expression, orderBy?: Array<Ordering>, limit?: number, offset?: number }): void;
  }

  declare class QueryResult {
    columnNames: Array<string>;
    rows: Array<Array<string>>;

    constructor(args?: { columnNames: Array<string>, rows: Array<Array<string>> }): void;
  }
}
