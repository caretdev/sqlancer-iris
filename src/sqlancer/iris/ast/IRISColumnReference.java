package sqlancer.iris.ast;

import sqlancer.common.ast.newast.ColumnReferenceNode;
import sqlancer.iris.IRISSchema.IRISColumn;

public class IRISColumnReference extends ColumnReferenceNode<IRISExpression, IRISColumn> implements IRISExpression {

  public IRISColumnReference(IRISColumn column) {
    super(column);
  }

}
