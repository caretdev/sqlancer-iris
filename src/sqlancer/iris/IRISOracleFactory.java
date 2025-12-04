package sqlancer.iris;

import sqlancer.OracleFactory;
import sqlancer.common.oracle.NoRECOracle;
import sqlancer.common.oracle.TLPWhereOracle;
import sqlancer.common.oracle.TestOracle;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.iris.gen.IRISExpressionGenerator;
import sqlancer.iris.oracle.IRISFuzzer;
// import sqlancer.iris.oracle.IRISPivotedQuerySynthesisOracle;

// import java.sql.SQLException;

public enum IRISOracleFactory implements OracleFactory<IRISGlobalState> {
    // PQS {
    // @Override
    // public TestOracle<IRISGlobalState> create(IRISGlobalState globalState) throws
    // SQLException {
    // return new IRISPivotedQuerySynthesisOracle(globalState);
    // }

    // @Override
    // public boolean requiresAllTablesToContainRows() {
    // return true;
    // }
    // },
    WHERE {
        @Override
        public TestOracle<IRISGlobalState> create(IRISGlobalState globalState) throws Exception {
            IRISExpressionGenerator gen = new IRISExpressionGenerator(globalState);
            ExpectedErrors expectedErrors = ExpectedErrors.newErrors().with(IRISErrors.getExpressionErrors()).build();
            return new TLPWhereOracle<>(globalState, gen, expectedErrors);
        }
    },
    NOREC {
        @Override
        public TestOracle<IRISGlobalState> create(IRISGlobalState globalState)
                throws Exception {
            IRISExpressionGenerator gen = new IRISExpressionGenerator(globalState);
            ExpectedErrors errors = ExpectedErrors.newErrors().with(IRISErrors.getExpressionErrors()).build();
            return new NoRECOracle<>(globalState, gen, errors);
        }
    },
    FUZZER {
        @Override
        public TestOracle<IRISGlobalState> create(IRISGlobalState globalState) throws Exception {
            return new IRISFuzzer(globalState);
        }
    }
}
