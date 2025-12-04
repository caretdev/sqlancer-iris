package sqlancer.iris;

import sqlancer.iris.ast.IRISSelect;
import sqlancer.iris.ast.IRISTableReference;
import sqlancer.iris.ast.IRISSelect.IRISFromTable;
import sqlancer.iris.ast.IRISBetweenOperation;
import sqlancer.iris.ast.IRISBinaryComparisonOperation;
import sqlancer.iris.ast.IRISBinaryOperation;
import sqlancer.iris.ast.IRISColumnReference;
import sqlancer.iris.ast.IRISConstant;
import sqlancer.iris.ast.IRISExpression;
import sqlancer.iris.ast.IRISInOperation;

public interface IRISVisitor {

  void visit(IRISConstant c);

  void visit(IRISSelect sel);

  void visit(IRISColumnReference column);

  void visit(IRISTableReference ref);

  void visit(IRISFromTable from);

  void visit(IRISBinaryOperation op);

  void visit(IRISBinaryComparisonOperation op);

  void visit(IRISBetweenOperation op);

  void visit(IRISInOperation op);

  default void visit(IRISExpression expr) {
    if (expr instanceof IRISConstant) {
      visit((IRISConstant) expr);
    } else if (expr instanceof IRISSelect) {
      visit((IRISSelect) expr);
    } else if (expr instanceof IRISColumnReference) {
      visit((IRISColumnReference) expr);
    } else if (expr instanceof IRISTableReference) {
      visit((IRISTableReference) expr);
    } else if (expr instanceof IRISFromTable) {
      visit((IRISFromTable) expr);
    } else if (expr instanceof IRISBinaryOperation) {
      visit((IRISBinaryOperation) expr);
    } else if (expr instanceof IRISBinaryComparisonOperation) {
      visit((IRISBinaryComparisonOperation) expr);
    } else if (expr instanceof IRISBetweenOperation) {
      visit((IRISBetweenOperation) expr);
    } else if (expr instanceof IRISInOperation) {
      visit((IRISInOperation) expr);
    } else {
      throw new AssertionError(expr);
    }
  }

  static String asString(IRISExpression expr) {
    IRISToStringVisitor visitor = new IRISToStringVisitor();
    visitor.visit(expr);
    return visitor.get();
  }

  static String asExpectedValues(IRISExpression expr) {
    IRISExpectedValueVisitor visitor = new IRISExpectedValueVisitor();
    visitor.visit(expr);
    return visitor.get();
  }
}
