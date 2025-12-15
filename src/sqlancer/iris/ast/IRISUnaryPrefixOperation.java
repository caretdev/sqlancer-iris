package sqlancer.iris.ast;

import sqlancer.common.ast.newast.NewUnaryPrefixOperatorNode;
import sqlancer.iris.IRISSchema.IRISDataType;
import sqlancer.IgnoreMeException;
import sqlancer.common.ast.BinaryOperatorNode.Operator;

public class IRISUnaryPrefixOperation extends NewUnaryPrefixOperatorNode<IRISExpression> implements IRISExpression {
  public enum PrefixOperator implements Operator {
    NOT("NOT", IRISDataType.BOOLEAN) {

      @Override
      public sqlancer.iris.IRISSchema.IRISDataType getExpressionType() {
        return IRISDataType.BOOLEAN;
      }

      @Override
      protected IRISConstant getExpectedValue(IRISConstant expectedValue) {
        if (expectedValue.isNull()) {
          return IRISConstant.createNullConstant();
        }
        return IRISConstant.createBooleanConstant(!expectedValue.cast(IRISDataType.BIT).asBoolean());
      }

    },
    UNARY_PLUS("+", IRISDataType.INTEGER) {

      @Override
      public sqlancer.iris.IRISSchema.IRISDataType getExpressionType() {
        return IRISDataType.INTEGER;
      }

      @Override
      protected IRISConstant getExpectedValue(IRISConstant expectedValue) {
        return expectedValue;
      }

    },
    UNARY_MINUS("-", IRISDataType.INTEGER) {

      @Override
      public sqlancer.iris.IRISSchema.IRISDataType getExpressionType() {
        return IRISDataType.INTEGER;
      }

      @Override
      protected IRISConstant getExpectedValue(IRISConstant expectedValue) {
        if (expectedValue.isNull()) {
          throw new IgnoreMeException();
        }
        try {
          return IRISConstant.createIntConstant(-expectedValue.asInt());
        } catch (UnsupportedOperationException e) {
          return null;
        }
      }

    };

    private String textRepresentation;
    private IRISDataType[] dataTypes;

    private PrefixOperator(String textRepresentation, IRISDataType... dataTypes) {
      this.textRepresentation = textRepresentation;
      this.dataTypes = dataTypes;
    }

    public abstract IRISDataType getExpressionType();

    protected abstract IRISConstant getExpectedValue(IRISConstant expectedValue);

    @Override
    public String getTextRepresentation() {
      return textRepresentation;
    }
  }

  private final IRISExpression expr;
  private final PrefixOperator op;

  public IRISUnaryPrefixOperation(IRISExpression expr, PrefixOperator op) {
    super(expr, op);
    this.expr = expr;
    this.op = op;
  }

  @Override
  public IRISDataType getExpressionType() {
    return op.getExpressionType();
  }

  @Override
  public IRISConstant getExpectedValue() {
    IRISConstant expectedValue = expr.getExpectedValue();
    if (expectedValue == null) {
      return null;
    }
    return op.getExpectedValue(expectedValue);
  }

  public IRISDataType[] getInputDataTypes() {
    return op.dataTypes;
  }

  public String getTextRepresentation() {
    return op.textRepresentation;
  }

  public IRISExpression getExpression() {
    return expr;
  }
}
