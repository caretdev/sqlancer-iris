package sqlancer.dbms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import sqlancer.Main;

public class TestIRISPQS {

    private static final String USER = "_SYSTEM";
    private static final String PASSWORD = "SYS";

    @Test
    public void test() {
        String port = "1972";

        assertEquals(0,
                Main.executeMain(new String[] {
                        "--random-seed", "0",
                        "--timeout-seconds", TestConfig.SECONDS,
                        "--num-threads", "4",
                        "--num-queries", TestConfig.NUM_QUERIES,
                        "--username", USER,
                        "--password", PASSWORD,
                        "--host", "localhost",
                        "--port", port,
                        "iris",
                        "--oracle", "pqs",
                }));

    }
}
