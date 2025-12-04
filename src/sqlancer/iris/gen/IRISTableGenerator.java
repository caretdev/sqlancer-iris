package sqlancer.iris.gen;

import java.util.ArrayList;
import java.util.List;

import sqlancer.Randomly;
import sqlancer.common.DBMSCommon;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.common.query.SQLQueryAdapter;
import sqlancer.iris.IRISGlobalState;
import sqlancer.iris.IRISSchema;
import sqlancer.iris.IRISSchema.IRISTable;
import sqlancer.iris.IRISSchema.IRISColumn;
import sqlancer.iris.IRISSchema.IRISDataType;

public class IRISTableGenerator {

  private final String tableName;
  private final IRISSchema newSchema;
  private final IRISGlobalState globalState;
  private final StringBuilder sb = new StringBuilder();
  private final List<IRISColumn> columnsToBeAdded = new ArrayList<>();
  protected final ExpectedErrors errors = new ExpectedErrors();
  private boolean isTemporaryTable = false;
  private final IRISTable table;

  public IRISTableGenerator(String tableName, IRISSchema newSchema, IRISGlobalState globalState) {
    this.tableName = tableName;
    this.newSchema = newSchema;
    this.globalState = globalState;
    table = new IRISTable(tableName, columnsToBeAdded, null, null, false, true);
  }

  public static SQLQueryAdapter generate(String tableName, IRISSchema newSchema, IRISGlobalState globalState) {
    return new IRISTableGenerator(tableName, newSchema, globalState).generate();
  }

  public SQLQueryAdapter generate() {
    sb.append("CREATE");
    // if (Randomly.getBoolean()) {
    // sb.append(" ");
    // isTemporaryTable = true;
    // sb.append("GLOBAL TEMPORARY");
    // }
    sb.append(" TABLE");
    if (!isTemporaryTable && Randomly.getBoolean()) {
      sb.append(" IF NOT EXISTS");
    }
    sb.append(" ");
    sb.append(tableName);
    if (Randomly.getBoolean() && !newSchema.getDatabaseTables().isEmpty()) {
      createAs();
    } else {
      createStandard();
    }
    return new SQLQueryAdapter(sb.toString(), errors, true);
  }

  private void createStandard() throws AssertionError {
    sb.append("(");
    for (int i = 0; i < Randomly.smallNumber() + 1; i++) {
      if (i != 0) {
        sb.append(", ");
      }
      String name = DBMSCommon.createColumnName(i);
      createColumn(name);
    }
    sb.append(")");
  }

  private void createAs() throws AssertionError {
    sb.append(" AS SELECT TOP 0 * FROM ");
    sb.append(newSchema.getRandomTable().getName());
  }

  private void createColumn(String name) throws AssertionError {
    sb.append(name);
    sb.append(" ");
    IRISDataType type = IRISDataType.getRandomType();
    IRISCommon.appendDataType(type, sb, globalState.getCollates());
    IRISColumn c = new IRISColumn(name, type);
    c.setTable(table);
    columnsToBeAdded.add(c);
    sb.append(" ");
  }
}
