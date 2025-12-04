package sqlancer.iris.ast;

import sqlancer.common.ast.newast.TableReferenceNode;
import sqlancer.iris.IRISSchema.IRISTable;

public class IRISTableReference extends TableReferenceNode<IRISExpression, IRISTable> implements IRISExpression {

  public IRISTableReference(IRISTable table) {
    super(table);
  }

}
