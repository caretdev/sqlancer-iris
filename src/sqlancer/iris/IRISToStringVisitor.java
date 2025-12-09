package sqlancer.iris;

import sqlancer.common.ast.newast.NewToStringVisitor;
import sqlancer.iris.ast.IRISSelect;
import sqlancer.iris.ast.IRISSelect.IRISFromTable;
import sqlancer.iris.ast.IRISCase;
import sqlancer.iris.ast.IRISCastOperation;
import sqlancer.iris.ast.IRISColumnReference;
import sqlancer.iris.ast.IRISColumnValue;
import sqlancer.iris.ast.IRISConstant;
import sqlancer.iris.ast.IRISExpression;
import sqlancer.iris.ast.IRISPostfixText;

public final class IRISToStringVisitor extends NewToStringVisitor<IRISExpression> {

  @Override
  public void visitSpecific(IRISExpression expr) {
    if (expr instanceof IRISConstant) {
      visit((IRISConstant) expr);
    } else if (expr instanceof IRISSelect) {
      visit((IRISSelect) expr);
    } else if (expr instanceof IRISFromTable) {
      visit((IRISFromTable) expr);
    } else if (expr instanceof IRISCase) {
      visit((IRISCase) expr);
    } else if (expr instanceof IRISColumnValue) {
      visit((IRISColumnValue) expr);
    } else if (expr instanceof IRISColumnReference) {
      visit((IRISColumnReference) expr);
    } else if (expr instanceof IRISPostfixText) {
      visit((IRISPostfixText) expr);
    } else if (expr instanceof IRISCastOperation) {
      visit((IRISCastOperation) expr);
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

  public void visit(IRISColumnValue c) {
    sb.append(c.getColumn().getFullQualifiedName());
  }

  public void visit(IRISCase cas) {
    sb.append("CASE");
    if (cas instanceof IRISCase.IRISCaseWithoutBaseExpression) {
      for (IRISCase.CasePair pair : cas.getPairs()) {
        sb.append(" WHEN ");
        visit(pair.getCond());
        sb.append(" THEN ");
        visit(pair.getThen());
      }
    }
    sb.append(" ELSE ");
    visit(cas.getElseExpr());
    sb.append(" END");
  }

  public void visit(IRISColumnReference c) {
    if (c.getColumn().getTable() == null) {
      sb.append(c.getColumn().getName());
    } else {
      sb.append(c.getColumn().getFullQualifiedName());
    }
  }

  public void visit(IRISPostfixText op) {
    visit(op.getExpr());
    sb.append(op.getText());
  }

  public void visit(IRISCastOperation cast) {
    sb.append("CAST(");
    visit(cast.getExpression());
    sb.append(" AS ");
    sb.append(cast.getType().name());
    sb.append(")");

  }

}
