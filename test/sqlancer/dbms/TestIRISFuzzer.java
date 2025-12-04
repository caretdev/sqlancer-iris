package sqlancer.dbms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import sqlancer.Main;

public class TestIRISFuzzer {
  @Test
  public void test() throws Exception {
    try (TestingIRISServer irisServer = new TestingIRISServer()) {
      System.out.println("URL: " + irisServer.getConnectionString());
      assertEquals(0,
          Main.executeMain(new String[] {
              "--random-seed", "0",
              "--timeout-seconds", TestConfig.SECONDS,
              "--num-threads", "4",
              "--num-queries", TestConfig.NUM_QUERIES,
              "--username", irisServer.getUsername(),
              "--password", irisServer.getUsername(),
              // "--port", String.valueOf(irisServer.getPort()),
              "iris",
              "--connection-url", irisServer.getConnectionString(),
              "--oracle", "fuzzer",
          }));
    }
  }

}
