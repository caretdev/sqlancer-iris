package sqlancer.iris.ast;

import sqlancer.iris.IRISSchema.IRISDataType;

public class IRISCastOperation implements IRISExpression {

  private final IRISExpression expr;
  private final IRISDataType type;

  public IRISCastOperation(IRISExpression expr, IRISDataType type) {
    this.expr = expr;
    this.type = type;
  }

  @Override
  public IRISConstant getExpectedValue() {
    IRISConstant expectedValue = expr.getExpectedValue();
    if (expectedValue == null) {
      return null;
    }
    return expectedValue.cast(type);
  }

  @Override
  public IRISDataType getExpressionType() {
    return type;
  }

  public IRISExpression getExpression() {
    return expr;
  }

  public IRISDataType getType() {
    return type;
  }
}
