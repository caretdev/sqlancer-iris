package sqlancer.iris.ast;

import sqlancer.Randomly;
import sqlancer.common.ast.newast.Join;
import sqlancer.iris.IRISSchema.IRISColumn;
import sqlancer.iris.IRISSchema.IRISDataType;
import sqlancer.iris.IRISSchema.IRISTable;

public class IRISJoin implements IRISExpression, Join<IRISExpression, IRISTable, IRISColumn> {

  public enum IRISJoinType {
    INNER, LEFT, RIGHT, FULL, CROSS;

    public static IRISJoinType getRandom() {
      return Randomly.fromOptions(values());
    }
  }

  // private IRISTable tableReference;
  // private IRISExpression onClause;
  // private IRISJoinType type;

  public IRISJoin(IRISExpression tablExpression, IRISExpression onClause, IRISJoinType type) {
    // this.tableReference = tableReference;
    // this.onClause = onClause;
    // this.type = type;
  }

  @Override
  public void setOnClause(IRISExpression clause) {
    // this.onClause = clause;
  }

  @Override
  public IRISDataType getExpressionType() {
    throw new AssertionError();
  }

  @Override
  public IRISConstant getExpectedValue() {
    throw new AssertionError();
  }

}
