package sqlancer.iris.ast;

public class IRISCase implements IRISExpression {

  protected final CasePair[] pairs;
  protected final IRISExpression elseExpr;

  public IRISCase(CasePair[] pairs, IRISExpression elseExpr) {
    this.pairs = pairs;
    this.elseExpr = elseExpr;
  }

  public static class CasePair {

    private final IRISExpression cond;
    private final IRISExpression then;

    public CasePair(IRISExpression cond, IRISExpression then) {
      this.cond = cond;
      this.then = then;
    }

    public IRISExpression getCond() {
      return cond;
    }

    public IRISExpression getThen() {
      return then;
    }
  }

  public CasePair[] getPairs() {
    return pairs.clone();
  }

  public IRISExpression getElseExpr() {
    return elseExpr;
  }

  public static class IRISCaseWithoutBaseExpression extends IRISCase {

    public IRISCaseWithoutBaseExpression(CasePair[] pairs, IRISExpression elseExpr) {
      super(pairs, elseExpr);
    }

    public IRISCaseWithoutBaseExpression(CasePair[] pairs) {
      super(pairs, null);
    }

    public static IRISExpression createBoolean(IRISExpression expr) {
      CasePair[] pairs = new CasePair[1];
      pairs[0] = new CasePair(expr, IRISConstant.createBooleanConstant(true));
      return new IRISCaseWithoutBaseExpression(pairs, IRISConstant.createBooleanConstant(false));
    }

    @Override
    public IRISConstant getExpectedValue() {
      for (CasePair c : pairs) {
        IRISConstant expectedValue = c.getCond().getExpectedValue();
        if (expectedValue == null) {
          return null;
        }
      }
      if (elseExpr == null) {
        return IRISConstant.createNullConstant();
      } else {
        return elseExpr.getExpectedValue();
      }
    }

  }

}
