package sqlancer.iris.ast;

import sqlancer.iris.IRISSchema.IRISColumn;
import sqlancer.iris.IRISSchema.IRISDataType;

public class IRISColumnValue implements IRISExpression {

  private final IRISColumn c;
  private final IRISConstant expectedValue;

  public IRISColumnValue(IRISColumn c, IRISConstant expectedValue) {
    this.c = c;
    this.expectedValue = expectedValue;
  }

  public static IRISColumnValue create(IRISColumn c, IRISConstant expectedValue) {
    return new IRISColumnValue(c, expectedValue);
  }

  public IRISColumn getColumn() {
    return c;
  }

  @Override
  public IRISConstant getExpectedValue() {
    return expectedValue;
  }

  @Override
  public IRISDataType getExpressionType() {
    return c.getType();
  }

}
