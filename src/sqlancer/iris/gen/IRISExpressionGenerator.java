package sqlancer.iris.gen;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

import sqlancer.Randomly;
import sqlancer.common.ast.BinaryOperatorNode.Operator;
import sqlancer.common.gen.CERTGenerator;
import sqlancer.common.gen.NoRECGenerator;
import sqlancer.common.gen.TLPWhereGenerator;
import sqlancer.common.gen.TypedExpressionGenerator;
import sqlancer.common.schema.AbstractTables;
import sqlancer.iris.ast.IRISConstant;
import sqlancer.iris.ast.IRISTableReference;
import sqlancer.iris.IRISSchema.IRISColumn;
import sqlancer.iris.ast.IRISBetweenOperation;
import sqlancer.iris.ast.IRISBinaryComparisonOperation.IRISBinaryComparisonOperator;
import sqlancer.iris.ast.IRISBinaryOperation;
import sqlancer.iris.ast.IRISColumnReference;
import sqlancer.iris.ast.IRISColumnValue;
import sqlancer.iris.ast.IRISExpression;
import sqlancer.iris.ast.IRISInOperation;
import sqlancer.iris.ast.IRISJoin;
import sqlancer.iris.ast.IRISSelect;
import sqlancer.iris.ast.IRISUnaryPostfixOperation;
import sqlancer.iris.ast.IRISUnaryPrefixOperation;
import sqlancer.iris.ast.IRISCase.IRISCaseWithoutBaseExpression;
import sqlancer.iris.IRISSchema.IRISTable;
import sqlancer.iris.IRISGlobalState;
import sqlancer.iris.IRISSchema;
import sqlancer.iris.IRISSchema.IRISDataType;
import sqlancer.iris.IRISSchema.IRISRowValue;

public class IRISExpressionGenerator
    extends TypedExpressionGenerator<IRISExpression, IRISColumn, IRISDataType>
    implements NoRECGenerator<IRISSelect, IRISJoin, IRISExpression, IRISTable, IRISColumn>,
    TLPWhereGenerator<IRISSelect, IRISJoin, IRISExpression, IRISTable, IRISColumn>,
    CERTGenerator<IRISSelect, IRISJoin, IRISExpression, IRISTable, IRISColumn> {

  private final int maxDepth;

  private IRISRowValue rw;

  List<IRISTable> tables;

  IRISGlobalState globalState;

  public IRISExpressionGenerator(IRISGlobalState globalState) {
    this.globalState = globalState;
    this.maxDepth = globalState.getOptions().getMaxExpressionDepth();
  }

  @Override
  public IRISExpression isNull(IRISExpression expr) {
    IRISExpression boolExpression = IRISCaseWithoutBaseExpression.createBoolean(expr);
    return new IRISUnaryPostfixOperation(boolExpression, IRISUnaryPostfixOperator.IS_NULL);
  }

  @Override
  public IRISExpression negatePredicate(IRISExpression expr) {
    // System.out.println("negatePredicate: " + (expr instanceof IRISConstant) + ":
    // " + expr.asString());
    // return null;
    return new IRISUnaryPrefixOperation(IRISUnaryPrefixOperator.NOT, expr);
  }

  @Override
  public IRISExpression generatePredicate() {
    return generateExpression(IRISDataType.BIT);
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
    return generateExpression(IRISDataType.BIT, 0);
  }

  public IRISExpression generateBooleanExpression(int depth) {
    return generateBooleanExpression(depth, depth == 0);
  }

  public IRISExpression generateBooleanExpression(int depth, boolean allowBinaryComparison) {
    IRISExpression expr = generateBooleanExpressionInternal(depth, allowBinaryComparison);

    if (expr instanceof IRISConstant) {
      throw new AssertionError("generateBooleanExpression: " + expr);
    }
    return expr;
  }

  private static class IRISBinaryComparisonOperationGenerator {

    public static IRISExpression generate(IRISExpressionGenerator gen, int depth) {
      IRISBinaryComparisonOperator op = IRISBinaryComparisonOperator.getRandomOperator();
      if (op == IRISBinaryComparisonOperator.LIKE || op == IRISBinaryComparisonOperator.NOT_LIKE) {
        return new IRISBinaryOperation(
            gen.generateExpression(IRISDataType.VARCHAR, depth + 1),
            gen.generateExpression(IRISDataType.VARCHAR, depth + 1),
            op);
      }
      IRISDataType type = gen.getMeaningfulType(IRISDataType.BIT);

      return new IRISBinaryOperation(
          gen.generateExpression(type, depth + 1),
          gen.generateExpression(type, depth + 1),
          op);
    }
  }

  public IRISExpression generateBooleanExpressionInternal(int depth, boolean allowBinaryComparison) {
    // if (depth > 0 ) {
    // return generateConstant(IRISDataType.BIT);
    // }
    Expression expr = Expression.getRandom(allowBinaryComparison);
    Operator op;
    IRISDataType type;
    switch (expr) {
      case BINARY_COMPARISON:
        return IRISBinaryComparisonOperationGenerator.generate(this, depth);
      case BINARY_LOGICAL:
        op = IRISBinaryLogicalOperator.getRandom();
        return new IRISBinaryOperation(
            generateBooleanExpression(depth + 1, true),
            generateBooleanExpression(depth + 1, true),
            op);
      case UNARY_POSTFIX:
        op = IRISUnaryPostfixOperator.getRandom();
        return new IRISUnaryPostfixOperation(generateLeafNode(), op);
      case IN:
        type = IRISDataType.getRandomType();
        return new IRISInOperation(generateLeafNode(type),
            generateLeafNodes(type, Randomly.smallNumber() + 1), Randomly.getBoolean());
      case BETWEEN:
        type = IRISDataType.getRandomType();
        return new IRISBetweenOperation(
            generateLeafNode(type),
            generateLeafNode(type),
            generateLeafNode(type),
            Randomly.getBoolean());
      default:
        throw new AssertionError("generateExpression: " + expr);
    }
  }

  @Override
  public String generateOptimizedQueryString(IRISSelect select, IRISExpression whereCondition,
      boolean shouldUseAggregate) {
    if (shouldUseAggregate) {
      IRISColumn aggr = new IRISColumn("COUNT(*)", null, null);
      select.setFetchColumns(List.of(new IRISColumnReference(aggr)));
    } else {
      List<IRISExpression> allColumns = columns.stream().map(IRISColumnReference::new)
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
    // System.out.println("generateColumn: " + type);
    IRISColumn column = Randomly
        .fromList(columns.stream().filter(c -> c.getType() == type).collect(Collectors.toList()));
    return new IRISColumnReference(column);
  }

  public IRISExpression generateConstant() {
    return generateConstant(getRandomType());
  }

  public List<IRISExpression> generateConstants(IRISDataType type, int nr) {
    List<IRISExpression> expressions = new ArrayList<>();

    for (int i = 0; i < nr; ++i) {
      expressions.add(this.generateConstant(type));
    }

    return expressions;
  }

  public IRISExpression generateLeafNode() {
    return generateLeafNode(getRandomType());
  }

  public List<IRISExpression> generateLeafNodes(IRISDataType type, int nr) {
    List<IRISExpression> expressions = new ArrayList<>();

    for (int i = 0; i < nr; ++i) {
      expressions.add(generateLeafNode(type));
    }

    return expressions;
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
    // FUNCTION,
    ;

    public static Expression getRandom(boolean allowBinaryComparison) {
      List<Expression> options = new ArrayList<>(Arrays.asList(values()));
      if (!allowBinaryComparison) {
        options.remove(BINARY_COMPARISON);
      }
      return Randomly.fromList(options);
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

  public static IRISExpression generateExpression(IRISGlobalState globalState, List<IRISColumn> columns,
      IRISDataType type) {
    IRISExpressionGenerator gen = new IRISExpressionGenerator(globalState).setColumns(columns);
    return gen.generateExpression(type, 0);
  }

  public static IRISExpression generateExpression(IRISGlobalState globalState, List<IRISColumn> columns) {
    IRISExpressionGenerator gen = new IRISExpressionGenerator(globalState).setColumns(columns);
    return gen.generateExpression(0);
  }

  protected IRISExpression generateExpression(int depth) {
    return generateExpression(IRISDataType.getRandomType(), depth + 1);
  }

  final IRISExpression createColumnOfType(IRISDataType type) {
    List<IRISColumn> columns = filterColumns(type);
    IRISColumn fromList = Randomly.fromList(columns);
    IRISConstant value = rw == null ? null : rw.getValues().get(fromList);
    return IRISColumnValue.create(fromList, value);
  }

  final List<IRISColumn> filterColumns(IRISDataType type) {
    if (columns == null) {
      return Collections.emptyList();
    } else {
      return columns.stream().filter(c -> c.getType() == type).collect(Collectors.toList());
    }
  }

  private IRISDataType getMeaningfulType(IRISDataType... exceptTypes) {
    if (Randomly.getBooleanWithSmallProbability() || columns == null || columns.isEmpty()) {
      return IRISDataType.getRandomType(exceptTypes);
    } else {
      return Randomly.fromList(columns).getType();
    }
  }

  @Override
  protected IRISExpression generateExpression(IRISDataType type, int depth) {
    if (depth > 0 && Randomly.getBooleanWithRatherLowProbability() || depth > maxDepth) {
      if (Randomly.getBooleanWithRatherLowProbability()) {
        return generateConstant(type);
      } else {
        if (filterColumns(type).isEmpty()) {
          return generateConstant(type);
        } else {
          return createColumnOfType(type);
        }
      }
      // throw new AssertionError("generateExpression");
    }

    switch (type) {
      case BIT:
        return generateBooleanExpression(depth);
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

  @Override
  public String generateExplainQuery(IRISSelect select) {
    return "EXPLAIN " + select.asString();
  }

  @Override
  public boolean mutate(IRISSelect select) {
    List<Function<IRISSelect, Boolean>> mutators = new ArrayList<>();
    return Randomly.fromList(mutators).apply(select);
  }

  @Override
  public List<IRISExpression> generateOrderBys() {
    List<IRISExpression> expressions = new ArrayList<>();
    int nr = Randomly.smallNumber() + 1;
    ArrayList<IRISColumn> irisColumns = new ArrayList<>(columns);
    for (int i = 0; i < nr && !columns.isEmpty(); i++) {
      IRISColumn randomColumn = Randomly.fromList(irisColumns);
      IRISColumnReference columnReference = new IRISColumnReference(randomColumn);
      irisColumns.remove(randomColumn);
      expressions.add(columnReference);
    }
    return expressions;
  }

}
