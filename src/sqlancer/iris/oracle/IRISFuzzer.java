package sqlancer.iris.oracle;

import sqlancer.Randomly;
import sqlancer.common.oracle.TestOracle;
import sqlancer.common.query.SQLQueryAdapter;
import sqlancer.iris.IRISGlobalState;
import sqlancer.iris.IRISVisitor;
import sqlancer.iris.gen.IRISRandomQueryGenerator;

public class IRISFuzzer implements TestOracle<IRISGlobalState> {

  private final IRISGlobalState globalState;

  public IRISFuzzer(IRISGlobalState globalState) {
    this.globalState = globalState;
  }

  @Override
  public void check() throws Exception {
    String s = IRISVisitor.asString(IRISRandomQueryGenerator.createRandomQuery(Randomly.smallNumber() + 1, globalState));
    try {
      globalState.executeStatement(new SQLQueryAdapter(s));
      globalState.getManager().incrementSelectQueryCount();
    } catch (Error e) {

    }

  }

}
