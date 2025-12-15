package sqlancer.dbms.gen;


import sqlancer.MainOptions;
import sqlancer.Randomly;
import sqlancer.iris.IRISGlobalState;
import sqlancer.iris.gen.IRISInsertGenerator;

public class TestIRISInsertGenerator {
  IRISGlobalState globalState;
  IRISInsertGenerator gen;

  public TestIRISInsertGenerator() {
    IRISGlobalState globalState = new IRISGlobalState();
    globalState.setMainOptions(new MainOptions());
    globalState.setRandomly(new Randomly());
    this.globalState = globalState;
    this.gen = new IRISInsertGenerator(globalState);
  }

}
