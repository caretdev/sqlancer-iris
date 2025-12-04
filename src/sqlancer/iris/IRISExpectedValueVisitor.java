package sqlancer.iris;

import sqlancer.iris.ast.IRISBetweenOperation;
import sqlancer.iris.ast.IRISBinaryComparisonOperation;
import sqlancer.iris.ast.IRISBinaryOperation;
import sqlancer.iris.ast.IRISColumnReference;
import sqlancer.iris.ast.IRISConstant;
import sqlancer.iris.ast.IRISExpression;
import sqlancer.iris.ast.IRISInOperation;
import sqlancer.iris.ast.IRISSelect;
import sqlancer.iris.ast.IRISSelect.IRISFromTable;
import sqlancer.iris.ast.IRISTableReference;

public class IRISExpectedValueVisitor implements IRISVisitor {
  private final StringBuilder sb = new StringBuilder();
  private static final int NR_TABS = 0;

  private void print(IRISExpression expr) {
    IRISToStringVisitor v = new IRISToStringVisitor();
    v.visit(expr);
    for (int i = 0; i < NR_TABS; i++) {
      sb.append("\t");
    }
    sb.append(v.get());
    sb.append(" -- ");
    sb.append(expr.getExpectedValue());
    sb.append("\n");
  }

  public String get() {
    return sb.toString();
  }

  @Override
  public void visit(IRISSelect sel) {
    visit(sel.getWhereClause());
  }

  @Override
  public void visit(IRISConstant c) {
    print(c);
  }

  @Override
  public void visit(IRISColumnReference column) {
    print(column);
  }

  @Override
  public void visit(IRISTableReference table) {
  }

  @Override
  public void visit(IRISFromTable from) {
  }

  @Override
  public void visit(IRISBinaryOperation op) {

  }

  @Override
  public void visit(IRISBinaryComparisonOperation op) {

  }

  @Override
  public void visit(IRISBetweenOperation op) {

  }

  @Override
  public void visit(IRISInOperation op) {

  }

}
