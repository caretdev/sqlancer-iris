package sqlancer.iris;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import com.google.auto.service.AutoService;

import sqlancer.AbstractAction;
import sqlancer.DatabaseProvider;
import sqlancer.IgnoreMeException;
import sqlancer.MainOptions;
import sqlancer.Randomly;
import sqlancer.SQLConnection;
import sqlancer.SQLProviderAdapter;
import sqlancer.StatementExecutor;
import sqlancer.common.DBMSCommon;
import sqlancer.common.query.SQLQueryAdapter;
import sqlancer.common.query.SQLQueryProvider;
import sqlancer.iris.gen.IRISInsertGenerator;
import sqlancer.iris.gen.IRISUpdateGenerator;
import sqlancer.iris.gen.IRISTableGenerator;

@AutoService(DatabaseProvider.class)
public class IRISProvider extends SQLProviderAdapter<IRISGlobalState, IRISOptions> {
  public IRISProvider() {
    super(IRISGlobalState.class, IRISOptions.class);
  }

  public enum Action implements AbstractAction<IRISGlobalState> {
    INSERT(IRISInsertGenerator::getQuery),
    UPDATE(IRISUpdateGenerator::getQuery),
    ;

    private final SQLQueryProvider<IRISGlobalState> sqlQueryProvider;

    Action(SQLQueryProvider<IRISGlobalState> sqlQueryProvider) {
      this.sqlQueryProvider = sqlQueryProvider;
    }

    @Override
    public SQLQueryAdapter getQuery(IRISGlobalState state) throws Exception {
      return sqlQueryProvider.getQuery(state);
    }
  }

  private static int mapActions(IRISGlobalState globalState, Action a) {
    Randomly r = globalState.getRandomly();
    switch (a) {
      case INSERT:
        return r.getInteger(0, globalState.getOptions().getMaxNumberInserts());
      case UPDATE:
        return r.getInteger(0, 10);
      default:
        throw new AssertionError(a);
    }
  }

  @Override
  public void generateDatabase(IRISGlobalState globalState) throws Exception {
    createTables(globalState, Randomly.fromOptions(4, 5, 6));

    StatementExecutor<IRISGlobalState, Action> se = new StatementExecutor<>(globalState,
        IRISProvider.Action.values(), IRISProvider::mapActions, (q) -> {
          if (globalState.getSchema().getDatabaseTables().isEmpty()) {
            throw new IgnoreMeException();
          }
        });
    se.executeStatements();
  }

  @Override
  public SQLConnection createDatabase(IRISGlobalState globalState) throws Exception {
    String username = globalState.getOptions().getUserName();
    String password = globalState.getOptions().getPassword();
    String host = globalState.getOptions().getHost();
    int port = globalState.getOptions().getPort();
    String url = globalState.getDbmsSpecificOptions().connectionURL;
    if (url.startsWith("jdbc:")) {
      url = url.substring(5);
    }

    try {
      URI uri = new URI(url);
      String userInfoURI = uri.getUserInfo();
      String namespace = uri.getPath().split("/")[1];
      if ((namespace == null) || (namespace == "")) {
        namespace = IRISOptions.DEFAULT_NAMESPACE;
      }
      if (userInfoURI != null) {
        // username and password specified in URL take precedence
        if (userInfoURI.contains(":")) {
          String[] userInfo = userInfoURI.split(":", 2);
          username = userInfo[0];
          password = userInfo[1];
        } else {
          username = userInfoURI;
          password = null;
        }
      }
      if (host == null) {
        host = uri.getHost();
      }
      if (port == MainOptions.NO_SET_PORT) {
        port = uri.getPort();
      }
      url = String.format("%s://%s:%d/%s", uri.getScheme(), host, port, namespace);
    } catch (URISyntaxException e) {
      throw new AssertionError(e);
    }

    if (host == null) {
      host = IRISOptions.DEFAULT_HOST;
    }
    if (port == MainOptions.NO_SET_PORT) {
      port = IRISOptions.DEFAULT_PORT;
    }
    String databaseName = globalState.getDatabaseName();
    Connection con = DriverManager.getConnection("jdbc:" + url, username, password);
    try (Statement s = con.createStatement()) {
      s.execute(String.format("DROP DATABASE IF EXISTS    \"%s\"", databaseName));
    }
    try (Statement s = con.createStatement()) {
      s.execute(String.format("CREATE DATABASE \"%s\"", databaseName));
    }
    try (Statement s = con.createStatement()) {
      s.execute("USE " + databaseName);
    }
    return new SQLConnection(con);
  }

  @Override
  public String getDBMSName() {
    return "iris";
  }

  protected void createTables(IRISGlobalState globalState, int numTables) throws Exception {
    while (globalState.getSchema().getDatabaseTables().size() < numTables) {
      try {
        String tableName = DBMSCommon.createTableName(globalState.getSchema().getDatabaseTables().size());
        SQLQueryAdapter createTable = IRISTableGenerator.generate(tableName, globalState.getSchema(),
            globalState);
        globalState.executeStatement(createTable);
      } catch (IgnoreMeException e) {

      }
    }
  }
}