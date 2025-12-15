package sqlancer.dbms.gen;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import sqlancer.MainOptions;
import sqlancer.Randomly;
import sqlancer.iris.IRISGlobalState;
import sqlancer.iris.IRISSchema;
import sqlancer.iris.IRISSchema.IRISTable;
import sqlancer.iris.IRISSchema.IRISTables;
import sqlancer.iris.IRISSchema.IRISColumn;
import sqlancer.iris.IRISSchema.IRISDataType;
import sqlancer.iris.ast.IRISConstant;
import sqlancer.iris.ast.IRISExpression;
import sqlancer.iris.ast.IRISInOperation;
import sqlancer.iris.ast.IRISSelect;
import sqlancer.iris.gen.IRISExpressionGenerator;

import java.util.ArrayList;
import java.util.List;

public class TestIRISExpressionGenerator {

  private IRISExpressionGenerator gen;

  private List<IRISColumn> getRandomColumns(int nr) {
    List<IRISSchema.IRISColumn> columns = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      IRISColumn column = new IRISColumn("c" + i, IRISSchema.IRISDataType.getRandomType());
      columns.add(column);
    }
    return columns;
  }

  private List<IRISSchema.IRISIndex> getRandomIndexes(List<IRISColumn> columns) {
    List<IRISSchema.IRISIndex> indexes = new ArrayList<>();
    return indexes;
  }

  private IRISTables getRandomTables(int nr) {
    List<IRISTable> tables = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      List<IRISSchema.IRISColumn> columns = getRandomColumns(Randomly.smallNumber() + 1);
      List<IRISSchema.IRISIndex> indexes = getRandomIndexes(columns);
      IRISTable table = new IRISTable("t" + i, columns, indexes);
      columns.forEach(c -> c.setTable(table));
      tables.add(table);
    }
    return new IRISTables(tables);
  }

  public TestIRISExpressionGenerator() {
    IRISGlobalState globalState = new IRISGlobalState();
    globalState.setMainOptions(new MainOptions());
    globalState.setRandomly(new Randomly(0));
    gen = new IRISExpressionGenerator(globalState);
    IRISTables tables = getRandomTables(Randomly.smallNumber() + 1);
    gen = gen.setTablesAndColumns(tables);
  }

  @Test
  public void generateBooleanExpression() {
    IRISExpression expr;
    for (int i = 1; i <= 1000; i++) {
      expr = gen.generateBooleanExpression();
      System.out.println("SELECT 1 WHERE " + expr.asString() + ";");
      if (expr instanceof IRISConstant) {
        fail("Found Constant: " + expr.asString());
      }
      // assertTrue(!(expr instanceof IRISConstant));
    }
  }

  @Test
  public void generateOptimizedQueryString() {
    IRISSelect select;
    for (int i = 0; i < 100; i++) {
      select = gen.generateSelect();
      select.setFromList(gen.getTableRefs());
      IRISExpression randomWhereCondition = this.gen.generateBooleanExpression();
      boolean shouldUseAggregate = Randomly.getBoolean();
      String optimizedQueryString = this.gen.generateOptimizedQueryString(select, randomWhereCondition,
          shouldUseAggregate);
      System.err.println(optimizedQueryString);
    }
  }

  @Test
  public void generateUnoptimizedQueryString() {
    IRISSelect select;
    for (int i = 0; i < 10; i++) {
      select = gen.generateSelect();
      select.setFromList(gen.getTableRefs());
      IRISExpression randomWhereCondition = this.gen.generateBooleanExpression();
      String optimizedQueryString = this.gen.generateUnoptimizedQueryString(select, randomWhereCondition);
      System.err.println(optimizedQueryString);
    }
  }

  @Test
  public void generateConstant() {
    IRISExpression expr;
    IRISDataType[] types = IRISDataType.values();
    for (IRISDataType type : types) {
      // if (type == IRISDataType.NULL) {
      // continue;
      // }
      System.out.println("Type: " + type.name());
      expr = gen.generateConstant(type);
      System.out
          .println("Constant: " + expr.asString());
    }
  }

  @Test
  public void generateInExpression() {
    IRISDataType type;
    IRISExpression expr;
    for (int i = 0; i < 100; i++) {
      type = IRISDataType.getRandomType();
      expr = new IRISInOperation(gen.generateLeafNode(type),
          gen.generateExpressions(type, Randomly.smallNumber() + 1), Randomly.getBoolean());
      System.out.println("IN Expression: " + type.name() + ": " + expr.asString());
    }
  }
}
