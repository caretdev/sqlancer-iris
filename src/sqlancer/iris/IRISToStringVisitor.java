package sqlancer.iris;

import sqlancer.common.ast.newast.NewToStringVisitor;
import sqlancer.iris.ast.IRISSelect;
import sqlancer.iris.ast.IRISSelect.IRISFromTable;
import sqlancer.iris.ast.IRISConstant;
import sqlancer.iris.ast.IRISExpression;

public final class IRISToStringVisitor extends NewToStringVisitor<IRISExpression> {

  @Override
  public void visitSpecific(IRISExpression expr) {
    if (expr instanceof IRISConstant) {
      visit((IRISConstant) expr);
    } else if (expr instanceof IRISSelect) {
      visit((IRISSelect) expr);
    } else if (expr instanceof IRISFromTable) {
      visit((IRISFromTable) expr);
    } else {
      throw new AssertionError(expr.getClass());
    }
  }

  public void visit(IRISConstant constant) {
    sb.append(constant.getTextRepresentation());
  }

  public void visit(IRISSelect s) {
    sb.append("SELECT ");
    if (s.getFetchColumns() == null) {
      sb.append("*");
    } else {
      visit(s.getFetchColumns());
    }
    sb.append(" FROM ");
    visit(s.getFromList());

    if (s.getWhereClause() != null) {
      sb.append(" WHERE ");
      visit(s.getWhereClause());
    }
    if (!s.getGroupByExpressions().isEmpty()) {
      sb.append(" GROUP BY ");
      visit(s.getGroupByExpressions());
    }
    if (s.getHavingClause() != null) {
      sb.append(" HAVING ");
      visit(s.getHavingClause());

    }
    if (!s.getOrderByClauses().isEmpty()) {
      sb.append(" ORDER BY ");
      visit(s.getOrderByClauses());
    }
    if (s.getLimitClause() != null) {
      sb.append(" LIMIT ");
      visit(s.getLimitClause());
    }

    if (s.getOffsetClause() != null) {
      sb.append(" OFFSET ");
      visit(s.getOffsetClause());
    }
  }

  public void visit(IRISFromTable from) {
    sb.append(from.getTable().getName());
  }

}
