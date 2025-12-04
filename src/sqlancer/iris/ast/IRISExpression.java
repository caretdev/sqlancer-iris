package sqlancer.iris.ast;

import sqlancer.common.ast.newast.Expression;
import sqlancer.iris.IRISSchema.IRISColumn;
import sqlancer.iris.IRISSchema.IRISDataType;

public interface IRISExpression extends Expression<IRISColumn> {

  default IRISDataType getExpressionType() {
    return null;
  }

  default IRISConstant getExpectedValue() {
    return null;
  }
}
