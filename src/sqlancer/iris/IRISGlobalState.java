package sqlancer.iris;

import sqlancer.SQLGlobalState;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IRISGlobalState extends SQLGlobalState<IRISOptions, IRISSchema> {

  @Override
  protected IRISSchema readSchema() throws SQLException {
    return IRISSchema.fromConnection(getConnection(), getDatabaseName());
  }

  // public boolean usesPQS() {
  // return getDbmsSpecificOptions().oracles.stream().anyMatch(o -> o ==
  // IRISOracleFactory.PQS);
  // }

  public List<String> getCollates() {
    List<String> collates = new ArrayList<>();
    collates.add("%EXACT");
    collates.add("%MINUS");
    collates.add("%PLUS");
    collates.add("%SPACE");
    collates.add("%SQLSTRING");
    collates.add("%SQLUPPER");
    collates.add("%TRUNCATE");
    collates.add("%MVR");
    return collates;
  }

}
