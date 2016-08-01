// @flow

import type { Dispatch } from 'redux';

import * as Query from 'query_types';

import createAction from '../_createAction';
import startQueryAsync from '../_startQueryAsync';
import * as actions from '../_types';
import type { State } from '../../state';

export type OperationType = 'W' | 'R';

export function loadFileList(traceId: number, operationType: OperationType): any {
  const constOperationType = operationType;
  return (dispatch: Dispatch, getState: () => State) => {
    const query: Query.Query = new Query.Query({
      resultSet: [
        new Query.ResultColumn({
          expression: new Query.Expression({
            valueExpression: new Query.ValueExpression({
              value: 'file_name',
            }),
          }),
          aggregation: Query.Aggregation.NONE,
          resultAlias: 'key',
        }),
        new Query.ResultColumn({
          expression: new Query.Expression({
            valueExpression: new Query.ValueExpression({
              value: 'file_name',
            }),
          }),
          aggregation: Query.Aggregation.NONE,
        }),
      ],
      distinct: true,
      sourceTables: ['com_facebook_tracery_database_trace_DiskPhysOpTable'],
      where: new Query.Expression({
        binaryExpression: new Query.BinaryExpression({
          leftOperand: new Query.Expression({
            binaryExpression: new Query.BinaryExpression({
              leftOperand: new Query.Expression({
                valueExpression: new Query.ValueExpression({
                  value: 'trace_idx',
                }),
              }),
              operation: Query.BinaryOperation.EQ,
              rightOperand: new Query.Expression({
                valueExpression: new Query.ValueExpression({
                  value: `${traceId}`,
                }),
              }),
            }),
          }),
          operation: Query.BinaryOperation.AND,
          rightOperand: new Query.Expression({
            binaryExpression: new Query.BinaryExpression({
              leftOperand: new Query.Expression({
                valueExpression: new Query.ValueExpression({
                  value: 'file_op',
                }),
              }),
              operation: Query.BinaryOperation.EQ,
              rightOperand: new Query.Expression({
                valueExpression: new Query.ValueExpression({
                  value: `'${constOperationType}'`,
                }),
              }),
            }),
          }),
        }),
      }),
      orderBy: [
        new Query.Ordering({
          columnName: 'file_name',
          ascending: true,
        }),
      ],
    });

    const queryId = startQueryAsync(query, dispatch, getState);
    dispatch(createAction(actions.SHOW_FILE_LIST, {
      queryId,
    }));
  };
}
