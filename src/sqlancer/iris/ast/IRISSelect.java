package sqlancer.iris.ast;

import java.util.List;
import java.util.Collections;

import sqlancer.common.ast.SelectBase;
import sqlancer.common.ast.newast.Select;
import sqlancer.iris.IRISVisitor;
import sqlancer.iris.IRISSchema.IRISColumn;
import sqlancer.iris.IRISSchema.IRISDataType;
import sqlancer.iris.IRISSchema.IRISTable;

public class IRISSelect extends SelectBase<IRISExpression>
    implements IRISExpression, Select<IRISJoin, IRISExpression, IRISTable, IRISColumn> {

  private List<IRISJoin> joinClauses = Collections.emptyList();

  private boolean isDistinct;

  public void setDistinct(boolean isDistinct) {
    this.isDistinct = isDistinct;
  }

  public boolean isDistinct() {
    return isDistinct;
  }

  @Override
  public List<IRISJoin> getJoinClauses() {
    return joinClauses;
  }

  @Override
  public void setJoinClauses(List<IRISJoin> joinStatements) {
    this.joinClauses = joinStatements;
  }

  @Override
  public String asString() {
    return IRISVisitor.asString(this);
  }

  public static class IRISFromTable implements IRISExpression {

    private final IRISTable table;

    public IRISFromTable(IRISTable table) {
      this.table = table;
    }

    public IRISTable getTable() {
      return table;
    }

    @Override
    public IRISDataType getExpressionType() {
      return null;
    }
  }
}
