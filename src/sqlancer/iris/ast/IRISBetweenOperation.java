package sqlancer.iris.ast;

import sqlancer.common.ast.newast.NewBetweenOperatorNode;

public class IRISBetweenOperation extends NewBetweenOperatorNode<IRISExpression> implements IRISExpression {

  public IRISBetweenOperation(IRISExpression left, IRISExpression middle, IRISExpression right, boolean isTrue) {
    super(left, middle, right, isTrue);
  }

}
