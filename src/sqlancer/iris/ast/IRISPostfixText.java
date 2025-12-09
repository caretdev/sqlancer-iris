package sqlancer.iris.ast;

import sqlancer.iris.IRISSchema.IRISDataType;

public class IRISPostfixText implements IRISExpression {

  private final IRISExpression expr;
  private final String text;
  private final IRISConstant expectedValue;
  private final IRISDataType type;

  public IRISPostfixText(IRISExpression expr, String text, IRISConstant expectedValue,
      IRISDataType type) {
    this.expr = expr;
    this.text = text;
    this.expectedValue = expectedValue;
    this.type = type;
  }

  public IRISExpression getExpr() {
    return expr;
  }

  public String getText() {
    return text;
  }

  @Override
  public IRISConstant getExpectedValue() {
    return expectedValue;
  }

  @Override
  public IRISDataType getExpressionType() {
    return type;
  }

}
