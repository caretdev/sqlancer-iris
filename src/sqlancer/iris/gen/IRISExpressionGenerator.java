package sqlancer.iris.gen;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import sqlancer.Randomly;
import sqlancer.common.ast.BinaryOperatorNode.Operator;
import sqlancer.common.gen.NoRECGenerator;
import sqlancer.common.gen.TLPWhereGenerator;
import sqlancer.common.gen.TypedExpressionGenerator;
import sqlancer.common.schema.AbstractTables;
import sqlancer.iris.ast.IRISConstant;
import sqlancer.iris.ast.IRISTableReference;
import sqlancer.iris.IRISSchema.IRISColumn;
import sqlancer.iris.ast.IRISBetweenOperation;
import sqlancer.iris.ast.IRISBinaryOperation;
import sqlancer.iris.ast.IRISColumnReference;
import sqlancer.iris.ast.IRISExpression;
import sqlancer.iris.ast.IRISInOperation;
import sqlancer.iris.ast.IRISJoin;
import sqlancer.iris.ast.IRISSelect;
import sqlancer.iris.ast.IRISUnaryPostfixOperation;
import sqlancer.iris.ast.IRISUnaryPrefixOperation;
import sqlancer.iris.IRISSchema.IRISTable;
import sqlancer.iris.IRISGlobalState;
import sqlancer.iris.IRISSchema;
import sqlancer.iris.IRISSchema.IRISDataType;

public class IRISExpressionGenerator
    extends TypedExpressionGenerator<IRISExpression, IRISColumn, IRISDataType>
    implements NoRECGenerator<IRISSelect, IRISJoin, IRISExpression, IRISTable, IRISColumn>,
    TLPWhereGenerator<IRISSelect, IRISJoin, IRISExpression, IRISTable, IRISColumn> {

  List<IRISTable> tables;

  IRISGlobalState globalState;

  public IRISExpressionGenerator(IRISGlobalState globalState) {
    this.globalState = globalState;
  }

  @Override
  public IRISExpression isNull(IRISExpression expr) {
    return new IRISUnaryPostfixOperation(expr, IRISUnaryPostfixOperator.IS_NULL);
  }

  @Override
  public IRISExpression negatePredicate(IRISExpression expr) {
    return new IRISUnaryPrefixOperation(IRISUnaryPrefixOperator.NOT, expr);
  }

  @Override
  public IRISExpression generatePredicate() {
    return generateExpression(IRISSchema.IRISDataType.BIT);
  }

  @Override
  public List<IRISExpression> generateFetchColumns(boolean shouldCreateDummy) {
    if (shouldCreateDummy) {
      return List.of(new IRISColumnReference(new IRISSchema.IRISColumn("*", null, null)));
    }
    return Randomly
        .nonEmptySubset(columns.stream().map(c -> new IRISColumnReference(c)).collect(Collectors.toList()));
  }

  @Override
  public IRISExpression generateBooleanExpression() {
    return generatePredicate();
  }

  @Override
  public String generateOptimizedQueryString(IRISSelect select, IRISExpression whereCondition,
      boolean shouldUseAggregate) {
    if (shouldUseAggregate) {
      IRISColumn aggr = new IRISColumn("COUNT(*)", null, null);
      select.setFetchColumns(List.of(new IRISColumnReference(aggr)));
    } else {
      List<IRISExpression> allColumns = columns.stream().map((c) -> new IRISColumnReference(c))
          .collect(Collectors.toList());
      select.setFetchColumns(allColumns);
      if (Randomly.getBooleanWithSmallProbability()) {
        select.setOrderByClauses(generateOrderBys());
      }
    }
    select.setWhereClause(whereCondition);

    return select.asString();
  }

  @Override
  public IRISSelect generateSelect() {
    return new IRISSelect();
  }

  @Override
  public String generateUnoptimizedQueryString(IRISSelect select, IRISExpression whereCondition) {
    IRISColumn c = new IRISColumn("COUNT(*) as cnt", null, null);
    select.setFetchColumns(List.of(new IRISColumnReference(c)));
    select.setWhereClause(null);
    return "SELECT SUM(cnt) FROM (" + select.asString() + ") as res";
  }

  @Override
  public List<IRISJoin> getRandomJoinClauses() {
    List<IRISJoin> joinExpressions = new ArrayList<>();
    return joinExpressions;
  }

  @Override
  public List<IRISExpression> getTableRefs() {
    return tables.stream().map(t -> new IRISTableReference(t)).collect(Collectors.toList());
  }

  @Override
  protected boolean canGenerateColumnOfType(IRISDataType type) {
    return columns.stream().anyMatch(c -> c.getType() == type);
  }

  @Override
  protected IRISExpression generateColumn(IRISDataType type) {
    System.out.println("generateColumn: " + type);
    IRISColumn column = Randomly
        .fromList(columns.stream().filter(c -> c.getType() == type).collect(Collectors.toList()));
    return new IRISColumnReference(column);
  }

  @Override
  public IRISExpression generateConstant(IRISDataType type) {
    switch (type) {
      case CHAR:
      case VARCHAR:
        return IRISConstant.createStringConstant(globalState.getRandomly().getString());
      case BIGINT:
        return IRISConstant.createIntConstant(Randomly.getNonCachedInteger());
      case INTEGER:
        return IRISConstant.createIntConstant(Randomly.getNotCachedInteger(-2147483648, 2147483647));
      case SMALLINT:
        return IRISConstant.createIntConstant(Randomly.getNotCachedInteger(-32768, 32767));
      case TINYINT:
        return IRISConstant.createIntConstant(Randomly.getNotCachedInteger(-128, 127));
      case BIT:
        return IRISConstant.createBooleanConstant(Randomly.getBoolean());
      case NUMERIC:
      case DECIMAL:
      case DOUBLE:
      case FLOAT:
      case REAL:
        return IRISConstant.createDoubleConstant(globalState.getRandomly().getFiniteDouble());
      // case DATE:
      // case TIMESTAMP:
      // case VARBINARY:
      // case LONGVARCHAR:
      // case LONGVARBINARY:
      // case TIME:
      default:
        throw new AssertionError("Unknown type: " + type);
    }
  }

  private enum Expression {
    BINARY_COMPARISON,
    BINARY_LOGICAL,
    UNARY_POSTFIX,
    // UNARY_PREFIX,
    IN,
    BETWEEN,
    // CASE,
    // BINARY_ARITHMETIC,
    // CAST,
    // FUNCTION;
  }

  public enum IRISBinaryComparisonOperator implements Operator {
    EQUALS("="),
    GREATER(">"),
    GREATER_EQUALS(">="),
    SMALLER("<"),
    SMALLER_EQUALS("<="),
    NOT_EQUALS("!="),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    ;

    private String textRepr;

    private IRISBinaryComparisonOperator(String textRepr) {
      this.textRepr = textRepr;
    }

    public static Operator getRandom() {
      return Randomly.fromOptions(values());
    }

    @Override
    public String getTextRepresentation() {
      return textRepr;
    }
  }

  public enum IRISBinaryLogicalOperator implements Operator {
    AND,
    OR,
    ;

    @Override
    public String getTextRepresentation() {
      return toString();
    }

    public static Operator getRandom() {
      return Randomly.fromOptions(values());
    }

  }

  public enum IRISUnaryPostfixOperator implements Operator {
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    ;

    private String textRepr;

    private IRISUnaryPostfixOperator(String textRepr) {
      this.textRepr = textRepr;
    }

    @Override
    public String getTextRepresentation() {
      return textRepr;
    }

    public static IRISUnaryPostfixOperator getRandom() {
      return Randomly.fromOptions(values());
    }
  }

  public enum IRISUnaryPrefixOperator implements Operator {
    NOT("NOT"),
    ;

    private String textRepr;

    IRISUnaryPrefixOperator(String textRepr) {
      this.textRepr = textRepr;
    }

    @Override
    public String getTextRepresentation() {
      return textRepr;
    }

  }

  protected IRISExpression generateExpression() {
    return generateExpression(0);
  }

  public IRISExpression generateExpression(IRISDataType type) {
    return generateExpression(type, 0);
  }

  protected IRISExpression generateExpression(int depth) {
    if (depth >= globalState.getOptions().getMaxExpressionDepth() || Randomly.getBoolean()) {
      return generateExpression(IRISDataType.getRandomType(), depth + 1);
    }

    Expression expr = Randomly.fromOptions(Expression.values());
    Operator op;
    IRISDataType type;
    switch (expr) {
      case BINARY_COMPARISON:
        op = IRISBinaryComparisonOperator.getRandom();
        return new IRISBinaryOperation(
            generateExpression(depth + 1),
            generateExpression(depth + 1),
            op);
      case BINARY_LOGICAL:
        op = IRISBinaryLogicalOperator.getRandom();
        return new IRISBinaryOperation(
            generateExpression(depth + 1),
            generateExpression(depth + 1),
            op);
      case UNARY_POSTFIX:
        op = IRISUnaryPostfixOperator.getRandom();
        return new IRISUnaryPostfixOperation(generateExpression(depth + 1), op);
      case IN:
        type = IRISDataType.getRandomType();
        return new IRISInOperation(generateExpression(type, depth + 1),
            generateExpressions(type, Randomly.smallNumber() + 1, depth + 1), Randomly.getBoolean());
      case BETWEEN:
        type = IRISDataType.getRandomType();
        return new IRISBetweenOperation(
            generateExpression(type, depth + 1),
            generateExpression(type, depth + 1),
            generateExpression(type, depth + 1),
            Randomly.getBoolean());
      default:
        throw new AssertionError("generateExpression: " + expr);
    }
  }

  @Override
  protected IRISExpression generateExpression(IRISDataType type, int depth) {
    switch (type) {
      case BIT:
      case TINYINT:
      case BIGINT:
      case CHAR:
      case NUMERIC:
      case DECIMAL:
      case INTEGER:
      case SMALLINT:
      case FLOAT:
      case REAL:
      case DOUBLE:
      case VARCHAR:
        return generateConstant(type);
      default:
        throw new AssertionError("generateExpression: " + type.name());
    }
  }

  @Override
  protected IRISDataType getRandomType() {
    return IRISDataType.getRandomType();
  }

  @Override
  public IRISExpressionGenerator setTablesAndColumns(
      AbstractTables<IRISTable, IRISColumn> tables) {
    this.columns = tables.getColumns();
    this.tables = tables.getTables();

    return this;
  }

}
