package sqlancer.iris.gen;

import java.util.List;
import java.util.stream.Collectors;

import sqlancer.Randomly;
import sqlancer.common.gen.AbstractInsertGenerator;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.common.query.SQLQueryAdapter;
import sqlancer.iris.IRISGlobalState;
import sqlancer.iris.IRISSchema;
import sqlancer.iris.IRISVisitor;
import sqlancer.iris.ast.IRISExpression;
import sqlancer.iris.IRISSchema.IRISColumn;
import sqlancer.iris.IRISSchema.IRISTable;

public class IRISInsertGenerator extends AbstractInsertGenerator<IRISSchema.IRISColumn> {

  private final IRISGlobalState globalState;

  public IRISInsertGenerator(IRISGlobalState globalState) {
    this.globalState = globalState;
  }

  public static SQLQueryAdapter getQuery(IRISGlobalState globalState) {
    return new IRISInsertGenerator(globalState).generate();
  }

  private ExpectedErrors getErrors() {
    ExpectedErrors errors = new ExpectedErrors();
    errors.add("SQLCODE: <-104>:<Field validation failed in INSERT");
    return errors;
  }

  private SQLQueryAdapter generate() {
    IRISTable table = globalState.getSchema().getRandomTable(t -> t.isInsertable());
    List<IRISColumn> columns = table.getRandomNonEmptyColumnSubset();
    if (Randomly.getBooleanWithRatherLowProbability()) {
      sb.append("INSERT OR UPDATE ");
    } else {
      sb.append("INSERT INTO ");
    }
    sb.append(table.getName());
    if (Randomly.getBooleanWithSmallProbability()) {
      sb.append(" DEFAULT VALUES");
    } else {
      sb.append("(");
      sb.append(columns.stream().map(c -> c.getName()).collect(Collectors.joining(", ")));
      sb.append(")");
      sb.append(" VALUES ");
      insertColumns(columns);
    }
    return new SQLQueryAdapter(sb.toString(), getErrors());
  }

  @Override
  protected void insertColumns(List<IRISColumn> columns) {
    sb.append("(");
    for (int nrColumn = 0; nrColumn < columns.size(); nrColumn++) {
      if (nrColumn != 0) {
        sb.append(", ");
      }
      insertValue(columns.get(nrColumn));
    }
    sb.append(")");
  }

  @Override
  protected void insertValue(IRISColumn column) {
    IRISExpression expr = new IRISExpressionGenerator(globalState).generateConstant(column.getType());
    // System.err.println("insertValue: " + column.getTable().getName() + "." + column.getName() + "; type: "
        // + column.getType().name() + "; value: " + IRISVisitor.asString(expr));
    // sb.append(String.format("/* %s */ ", column.getType().name()));
    sb.append(IRISVisitor.asString(expr));
  }

}
