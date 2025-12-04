package sqlancer.iris.ast;

import sqlancer.common.ast.newast.NewUnaryPrefixOperatorNode;
import sqlancer.common.ast.BinaryOperatorNode.Operator;

public class IRISUnaryPrefixOperation extends NewUnaryPrefixOperatorNode<IRISExpression> implements IRISExpression {

  public IRISUnaryPrefixOperation(IRISExpression expr, Operator op) {
    super(expr, op);
  }

  public IRISUnaryPrefixOperation(Operator op, IRISExpression expr) {
    super(expr, op);
  }
}
