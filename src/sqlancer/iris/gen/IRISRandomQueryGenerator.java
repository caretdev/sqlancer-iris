package sqlancer.iris.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sqlancer.Randomly;
import sqlancer.iris.ast.IRISConstant;
import sqlancer.iris.IRISGlobalState;
import sqlancer.iris.IRISSchema.IRISDataType;
import sqlancer.iris.IRISSchema.IRISTables;
import sqlancer.iris.ast.IRISSelect;
import sqlancer.iris.ast.IRISSelect.IRISFromTable;
import sqlancer.iris.ast.IRISExpression;

public final class IRISRandomQueryGenerator {

  public static IRISSelect createRandomQuery(int nrColumns, IRISGlobalState globalState) {
    List<IRISExpression> columns = new ArrayList<>();
    IRISTables tables = globalState.getSchema().getRandomTableNonEmptyTables();
    IRISExpressionGenerator gen = new IRISExpressionGenerator(globalState).setColumns(tables.getColumns());
    for (int i = 0; i < nrColumns; i++) {
      columns.add(gen.generateExpression(IRISDataType.getRandomType()));
    }

    IRISSelect select = new IRISSelect();
    select.setFromList(tables.getTables().stream().map(t -> new IRISFromTable(t)).collect(Collectors.toList()));
    select.setFetchColumns(columns);

    if (Randomly.getBoolean()) {
      select.setWhereClause(gen.generateExpression());
    }
    if (Randomly.getBoolean()) {
      select.setOrderByClauses(gen.generateOrderBys());
    }
    if (Randomly.getBoolean()) {
      select.setLimitClause(IRISConstant.createIntConstant(Randomly.getNotCachedInteger(0, Integer.MAX_VALUE)));
    }
    if (Randomly.getBoolean()) {
      select.setOffsetClause(IRISConstant.createIntConstant(Randomly.getNotCachedInteger(0, Integer.MAX_VALUE)));
    }
    // if (Randomly.getBoolean()) {
    //   select.setHavingClause(gen.generateHavingClause());
    // }

    return select;
  }
}
