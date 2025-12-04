package sqlancer.iris.oracle;

import java.util.List;
// import java.util.stream.Collectors;

import sqlancer.SQLConnection;
import sqlancer.common.oracle.PivotedQuerySynthesisBase;
import sqlancer.common.query.Query;
import sqlancer.common.query.SQLQueryAdapter;
import sqlancer.iris.IRISGlobalState;
import sqlancer.iris.IRISSchema.IRISRowValue;
import sqlancer.iris.ast.IRISSelect;
import sqlancer.iris.IRISSchema.IRISColumn;
// import sqlancer.iris.IRISSchema.IRISTables;
// import sqlancer.iris.IRISSchema.IRISTable;

import sqlancer.iris.ast.IRISExpression;
import sqlancer.iris.IRISVisitor;

public class IRISPivotedQuerySynthesisOracle
    extends PivotedQuerySynthesisBase<IRISGlobalState, IRISRowValue, IRISExpression, SQLConnection> {

  // private List<IRISColumn> fetchColumns;
  private List<IRISColumn> columns;

  public IRISPivotedQuerySynthesisOracle(IRISGlobalState globalState) {
    super(globalState);
  }

  @Override
  protected Query<SQLConnection> getRectifiedQuery() throws Exception {
    // IRISTables randomFromTables = globalState.getSchema().getRandomTableNonEmptyTables();
    // List<IRISTable> tables = randomFromTables.getTables();

    IRISSelect selectStatement = new IRISSelect();
    // List<IRISColumn> columns = randomFromTables.getColumns();
    // pivotRow = randomFromTables.getRandomRowValue(globalState.getConnection());
    // fetchColumns = columns;
    // selectStatement.setFetchColumns(fetchColumns.stream()
    //     .map(c -> new IRISColumnValue(getFetchValueAliasedColumn(c), pivotRow.getValues().get(c)))
    //     .collect(Collectors.toList()));
    return new SQLQueryAdapter(IRISVisitor.asString(selectStatement));
  }

  @Override
  protected Query<SQLConnection> getContainmentCheckQuery(Query<?> query) throws Exception {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT * FROM (");
    sb.append(query.getUnterminatedQueryString());
    sb.append(") as result WHERE ");
    int i = 0;
    for (IRISColumn c : columns) {
      if (i++ != 0) {
        sb.append(" AND ");
      }
      sb.append("result.");
      sb.append(c.getTable().getName());
      sb.append(c.getName());
      if (pivotRow.getValues().get(c).isNull()) {
        sb.append(" IS NULL");
      } else {
        sb.append(" = ");
        sb.append(pivotRow.getValues().get(c).getTextRepresentation());
      }
    }
    String resultingQueryString = sb.toString();
    return new SQLQueryAdapter(resultingQueryString, errors);
  }

  @Override
  protected String getExpectedValues(IRISExpression expr) {
    return IRISVisitor.asExpectedValues(expr);
  }
}
