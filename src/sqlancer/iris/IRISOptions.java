package sqlancer.iris;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import sqlancer.DBMSSpecificOptions;

import java.util.Collections;
import java.util.List;

@Parameters(separators = "=", commandDescription = "IRIS (default port: " + IRISOptions.DEFAULT_PORT
        + ", default host: " + IRISOptions.DEFAULT_HOST + ")")
public class IRISOptions implements DBMSSpecificOptions<IRISOracleFactory> {
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 1972;
    public static final String DEFAULT_NAMESPACE = "USER";

    @Parameter(names = "--oracle")
    public List<IRISOracleFactory> oracles = Collections.emptyList();

    @Parameter(names = "--connection-url", description = "Specifies the URL for connecting to the IRIS server", arity = 1)
    public String connectionURL = String.format("IRIS://%s:%d/%s", IRISOptions.DEFAULT_HOST,
            IRISOptions.DEFAULT_PORT, IRISOptions.DEFAULT_NAMESPACE);

    @Override
    public List<IRISOracleFactory> getTestOracleFactory() {
        return oracles;
    }
}
