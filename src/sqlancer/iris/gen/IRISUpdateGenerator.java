package sqlancer.iris.gen;

import java.util.List;

import sqlancer.Randomly;
import sqlancer.common.gen.AbstractUpdateGenerator;
import sqlancer.common.query.SQLQueryAdapter;
import sqlancer.iris.IRISGlobalState;
import sqlancer.iris.IRISSchema;
import sqlancer.iris.IRISVisitor;
import sqlancer.iris.IRISSchema.IRISColumn;
import sqlancer.iris.IRISSchema.IRISTable;
import sqlancer.iris.ast.IRISExpression;

public class IRISUpdateGenerator extends AbstractUpdateGenerator<IRISSchema.IRISColumn> {

  private final IRISGlobalState globalState;
  private IRISExpressionGenerator gen;

  public IRISUpdateGenerator(IRISGlobalState globalState) {
    this.globalState = globalState;
  }

  public static SQLQueryAdapter getQuery(IRISGlobalState globalState) {
    return new IRISUpdateGenerator(globalState).generate();
  }

  private SQLQueryAdapter generate() {
    IRISTable table = globalState.getSchema().getRandomTable(t -> t.isInsertable());
    List<IRISColumn> columns = table.getRandomNonEmptyColumnSubset();
    gen = new IRISExpressionGenerator(globalState).setColumns(table.getColumns());
    sb.append("UPDATE ");
    sb.append(table.getName());
    sb.append(" SET ");
    updateColumns(columns);
    if (Randomly.getBooleanWithSmallProbability()) {
      sb.append(" WHERE ");
      sb.append(IRISVisitor.asString(gen.generateExpression()));
    }

    return new SQLQueryAdapter(sb.toString(), errors);
  }

  @Override
  protected void updateValue(IRISColumn column) {
    IRISExpression expr = gen.generateConstant(column.getType());
    // System.err.println("updateValue: " + column.getTable().getName() + "." + column.getName() + "; type: " + column.getType().name() + "; value: " + IRISVisitor.asString(expr));
    sb.append(IRISVisitor.asString(expr));
  }


}
