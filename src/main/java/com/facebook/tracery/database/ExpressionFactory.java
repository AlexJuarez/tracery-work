package com.facebook.tracery.database;

import com.facebook.tracery.thrift.query.BinaryExpression;
import com.facebook.tracery.thrift.query.BinaryOperation;
import com.facebook.tracery.thrift.query.Expression;
import com.facebook.tracery.thrift.query.TrinaryExpression;
import com.facebook.tracery.thrift.query.TrinaryOperation;
import com.facebook.tracery.thrift.query.UnaryExpression;
import com.facebook.tracery.thrift.query.UnaryOperation;
import com.facebook.tracery.thrift.query.ValueExpression;

/**
 * Helper methods for constructing Expression trees.
 */
public class ExpressionFactory {
  public static Expression createValueExpression(String value) {
    return Expression.valueExpression(new ValueExpression(value));
  }

  public static Expression createUnaryValueExpression(UnaryOperation operation, String operand) {
    Expression operandExpression = createValueExpression(operand);
    return Expression.unaryExpression(new UnaryExpression(operation, operandExpression));
  }

  public static Expression createBinaryValueExpression(String leftOperand,
                                                      BinaryOperation operation,
                                                      String rightOperand) {
    Expression leftExpression = createValueExpression(leftOperand);
    Expression rightExpression = createValueExpression(rightOperand);
    return Expression.binaryExpression(new BinaryExpression(leftExpression, operation,
        rightExpression));
  }

  public static Expression createLeafTrinaryValueExpression(String leftOperand,
                                                       TrinaryOperation operation,
                                                       String rightOperand0,
                                                       String rightOperand1) {
    Expression leftExpression = createValueExpression(leftOperand);
    Expression middleExpression = createValueExpression(rightOperand0);
    Expression rightExpression = createValueExpression(rightOperand1);
    return Expression.trinaryExpression(new TrinaryExpression(leftExpression, operation,
        middleExpression, rightExpression));
  }
}
