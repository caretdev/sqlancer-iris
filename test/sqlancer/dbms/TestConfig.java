package sqlancer.dbms;

public class TestConfig {
    public static final String NUM_QUERIES = "1000";
    public static final String SECONDS = "300";

    public static final String IRIS_ENV = "IRIS_AVAILABLE";

    public static boolean isEnvironmentTrue(String key) {
        return key == IRIS_ENV;
    }
}
