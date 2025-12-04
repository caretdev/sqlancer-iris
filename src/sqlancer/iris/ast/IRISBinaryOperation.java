package sqlancer.iris.ast;

import sqlancer.common.ast.BinaryOperatorNode.Operator;
import sqlancer.common.ast.newast.NewBinaryOperatorNode;

public class IRISBinaryOperation extends NewBinaryOperatorNode<IRISExpression> implements IRISExpression {

  public IRISBinaryOperation(IRISExpression left, IRISExpression right, Operator op) {
    super(left, right, op);
  }

}
