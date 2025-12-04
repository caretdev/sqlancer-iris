package sqlancer.iris.ast;

import java.util.List;

import sqlancer.common.ast.newast.NewInOperatorNode;

public class IRISInOperation extends NewInOperatorNode<IRISExpression> implements IRISExpression {

  public IRISInOperation(IRISExpression left, List<IRISExpression> right, boolean isNegated) {
    super(left, right, isNegated);
  }


}
