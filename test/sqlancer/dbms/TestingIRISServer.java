package sqlancer.dbms;

import com.caretdev.testcontainers.IRISContainer;

public class TestingIRISServer implements AutoCloseable {

  private static final String USER = "sqlancer";
  private static final String PASSWORD = "sqlancer";
  private static final String DATABASE = "USER";

  private final IRISContainer dockerContainer;

  public TestingIRISServer() {
    dockerContainer = new IRISContainer("containers.intersystems.com/intersystems/iris-community:latest-em");
    dockerContainer.withDatabaseName(DATABASE);
    dockerContainer.withUsername(USER);
    dockerContainer.withPassword(PASSWORD);
    dockerContainer.withLicenseKey(System.getProperty("user.home") + "/iris-community.key");

    dockerContainer.start();
  }

  @Override
  public void close() throws Exception {
    dockerContainer.close();
  }

  public String getConnectionString() {
    return dockerContainer.getJdbcUrl();
  }

  public String getUsername() {
    return USER;
  }

  public String getPassword() {
    return PASSWORD;
  }

  public int getPort() {
    return dockerContainer.getMappedPort(1972);
  }
}
