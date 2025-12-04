package sqlancer.iris.ast;

import sqlancer.common.ast.BinaryOperatorNode.Operator;
import sqlancer.common.ast.newast.NewUnaryPostfixOperatorNode;

public class IRISUnaryPostfixOperation extends NewUnaryPostfixOperatorNode<IRISExpression> implements IRISExpression {

  public IRISUnaryPostfixOperation(IRISExpression expr, Operator op) {
    super(expr, op);
  }
}
