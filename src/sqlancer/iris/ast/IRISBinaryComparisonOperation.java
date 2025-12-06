package sqlancer.iris.ast;

import sqlancer.Randomly;
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
    },
    GREATER(">") {
      @Override
      public IRISConstant getExpectedValue(IRISConstant leftVal, IRISConstant rightVal) {
        return rightVal.isLessThan(leftVal);
      }
    },
    GREATER_EQUALS(">=") {
      @Override
      public IRISConstant getExpectedValue(IRISConstant leftVal, IRISConstant rightVal) {
        return null;
      }
    },
    SMALLER("<") {
      @Override
      public IRISConstant getExpectedValue(IRISConstant leftVal, IRISConstant rightVal) {
        return leftVal.isLessThan(rightVal);
      }
    },
    SMALLER_EQUALS("<=") {
      @Override
      public IRISConstant getExpectedValue(IRISConstant leftVal, IRISConstant rightVal) {
        return null;
      }
    },
    NOT_EQUALS("!=") {
      @Override
      public IRISConstant getExpectedValue(IRISConstant leftVal, IRISConstant rightVal) {
        return null;
      }
    },
    LIKE("LIKE") {
      @Override
      public IRISConstant getExpectedValue(IRISConstant leftVal, IRISConstant rightVal) {
        return null;
      }
    },
    NOT_LIKE("NOT LIKE") {
      @Override
      public IRISConstant getExpectedValue(IRISConstant leftVal, IRISConstant rightVal) {
        return null;
      }
    },
    ;

    private final String textRepresentation;

    @Override
    public String getTextRepresentation() {
      return textRepresentation;
    }

    IRISBinaryComparisonOperator(String textRepresentation) {
      this.textRepresentation = textRepresentation;
    }

    public abstract IRISConstant getExpectedValue(IRISConstant leftVal, IRISConstant rightVal);

    public static IRISBinaryComparisonOperator getRandomOperator() {
      return Randomly.fromOptions(values());
    }
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
