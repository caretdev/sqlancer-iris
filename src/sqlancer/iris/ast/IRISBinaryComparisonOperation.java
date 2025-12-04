package sqlancer.iris.ast;

import sqlancer.common.ast.BinaryOperatorNode;
import sqlancer.common.ast.BinaryOperatorNode.Operator;
import sqlancer.iris.IRISSchema.IRISDataType;
import sqlancer.iris.ast.IRISBinaryComparisonOperation.IRISBinaryComparisonOperator;

public class IRISBinaryComparisonOperation extends BinaryOperatorNode<IRISExpression, IRISBinaryComparisonOperator>
    implements IRISExpression {

  public enum IRISBinaryComparisonOperator implements Operator {
    EQUALS("=") {
      @Override
      public IRISConstant getExpectedValue(IRISConstant leftVal, IRISConstant rightVal) {
        return leftVal.isEquals(rightVal);
      }
    };

    private final String textRepresentation;

    @Override
    public String getTextRepresentation() {
      return textRepresentation;
    }

    IRISBinaryComparisonOperator(String textRepresentation) {
      this.textRepresentation = textRepresentation;
    }

    public abstract IRISConstant getExpectedValue(IRISConstant leftVal, IRISConstant rightVal);
  }

  protected IRISBinaryComparisonOperation(IRISExpression left, IRISExpression right, IRISBinaryComparisonOperator op) {
    super(left, right, op);
  }

  @Override
  public IRISConstant getExpectedValue() {
    IRISConstant leftExpectedValue = getLeft().getExpectedValue();
    IRISConstant rightExpectedValue = getRight().getExpectedValue();
    if (leftExpectedValue == null || rightExpectedValue == null) {
      return null;
    }
    return getOp().getExpectedValue(leftExpectedValue, rightExpectedValue);
  }

  @Override
  public IRISDataType getExpressionType() {
    return IRISDataType.BIT;
  }

}
