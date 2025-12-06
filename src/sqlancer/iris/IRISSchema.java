package sqlancer.iris;

import sqlancer.Randomly;
import sqlancer.SQLConnection;
import sqlancer.common.schema.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import sqlancer.iris.IRISSchema.IRISTable;
import sqlancer.iris.ast.IRISConstant;

public class IRISSchema extends AbstractSchema<IRISGlobalState, IRISTable> {

  public IRISSchema(List<IRISTable> databaseTables) {
    super(databaseTables);
  }

  public enum IRISDataType {

    NULL,

    BIT,
    TINYINT,
    BIGINT,
    LONGVARBINARY,
    VARBINARY,
    BINARY,
    LONGVARCHAR,
    CHAR,
    NUMERIC,
    DECIMAL,
    INTEGER,
    SMALLINT,
    FLOAT,
    REAL,
    DOUBLE,
    DATE,
    TIME,
    TIMESTAMP,
    VARCHAR,

    // aliases
    // TEXT,
    // BOOLEAN,
    // INT,

    // INT, TINYINT, SMALLINT, BIGINT, SERIAL,
    // BOOLEAN, BIT,
    // VARCHAR, VARBINARY,
    // NUMERIC, DECIMAL, FLOAT, REAL, MONEY,
    // DATE, TIME, TIMESTAMP,
    // TEXT, LONGVARBINARY, LONGVARCHAR,

    // BIGINT,
    // VARCHAR,
    // INTEGER,
    // BIT,
    // DATE,
    // TIMESTAMP,
    // NUMERIC,
    // DOUBLE,
    // VARBINARY,
    // LONGVARCHAR,
    // LONGVARBINARY,
    // TIME,
    // SMALLINT,
    // TINYINT,
    ;

    public static IRISDataType getRandomType(IRISDataType... exceptTypes) {
      List<IRISDataType> dataTypes = new ArrayList<>();

      dataTypes.add(BIT);
      dataTypes.add(TINYINT);
      dataTypes.add(BIGINT);
      dataTypes.add(CHAR);
      dataTypes.add(NUMERIC);
      dataTypes.add(DECIMAL);
      dataTypes.add(INTEGER);
      dataTypes.add(SMALLINT);
      dataTypes.add(FLOAT);
      dataTypes.add(REAL);
      dataTypes.add(DOUBLE);
      dataTypes.add(VARCHAR);
      // dataTypes.add(LONGVARBINARY);
      // dataTypes.add(VARBINARY);
      // dataTypes.add(BINARY);
      // dataTypes.add(LONGVARCHAR);
      // dataTypes.add(DATE);
      // dataTypes.add(TIME);
      // dataTypes.add(TIMESTAMP);
      if (exceptTypes != null) {
        for (IRISDataType type: exceptTypes) {
          dataTypes.remove(type);
        }
      }
      return Randomly.fromList(dataTypes);
    }
  }

  public static class IRISColumn extends AbstractTableColumn<IRISTable, IRISDataType> {

    public IRISColumn(String name, IRISDataType columnType) {
      super(name, null, columnType);
    }

    public IRISColumn(String name, IRISTable table, IRISDataType columnType) {
      super(name, table, columnType);
    }

    public static IRISColumn createDummy(String name) {
      return new IRISColumn(name, IRISDataType.INTEGER);
    }

  }

  public static class IRISRowValue extends AbstractRowValue<IRISTables, IRISColumn, IRISConstant> {

    IRISRowValue(IRISTables tables, Map<IRISColumn, IRISConstant> values) {
      super(tables, values);
    }
  }

  public static class IRISTable
      extends AbstractRelationalTable<IRISColumn, IRISIndex, IRISGlobalState> {

    public enum TableType {
      STANDARD, TEMPORARY
    }

    private final IRISTable.TableType tableType;
    private final boolean isInsertable;

    public IRISTable(String tableName, List<IRISColumn> columns, List<IRISIndex> indexes,
        IRISTable.TableType tableType, boolean isView, boolean isInsertable) {
      super(tableName, columns, indexes, isView);
      this.isInsertable = isInsertable;
      this.tableType = tableType;
    }

    public IRISTable(String tableName, List<IRISColumn> columns, List<IRISIndex> indexes) {
      super(tableName, columns, indexes, false);
      this.isInsertable = true;
      this.tableType = TableType.STANDARD;
    }

    public IRISTable.TableType getTableType() {
      return tableType;
    }

    public boolean isInsertable() {
      return isInsertable;
    }

  }

  // BIT - bit
  // TINYINT - tinyint
  // BIGINT - bigint
  // LONGVARBINARY - longvarbinary
  // VARBINARY - varbinary
  // BINARY - varbinary
  // LONGVARCHAR - longvarchar
  // CHAR - varchar
  // NUMERIC - numeric
  // DECIMAL - numeric
  // INTEGER - integer
  // SMALLINT - smallint
  // FLOAT - double
  // REAL - double
  // DOUBLE - double
  // DATE - date
  // TIME - time
  // TIMESTAMP - timestamp
  // VARCHAR - varchar

  public static IRISDataType getColumnType(String typeString) {
    switch (typeString) {
      case "bigint":
        return IRISDataType.BIGINT;
      case "tinyint":
        return IRISDataType.TINYINT;
      case "smallint":
        return IRISDataType.SMALLINT;
      case "integer":
        return IRISDataType.INTEGER;
      case "bit":
        return IRISDataType.BIT;
      case "varchar":
        return IRISDataType.VARCHAR;
      case "varbinary":
        return IRISDataType.VARBINARY;
      case "longvarchar":
        return IRISDataType.LONGVARCHAR;
      case "longvarbinary":
        return IRISDataType.LONGVARBINARY;
      case "numeric":
        return IRISDataType.NUMERIC;
      case "double":
        return IRISDataType.DOUBLE;
      case "date":
        return IRISDataType.DATE;
      case "time":
        return IRISDataType.TIME;
      case "timestamp":
        return IRISDataType.TIMESTAMP;
      default:
        throw new AssertionError(typeString);
    }
  }

  public static final class IRISIndex extends TableIndex {

    private IRISIndex(String indexName) {
      super(indexName);
    }

    public static IRISIndex create(String indexName) {
      return new IRISIndex(indexName);
    }

    @Override
    public String getIndexName() {
      if (super.getIndexName().contentEquals("PRIMARY")) {
        return "`PRIMARY`";
      } else {
        return super.getIndexName();
      }
    }

  }

  private static List<IRISIndex> getIndexes(SQLConnection con, String schemaName, String tableName)
      throws SQLException {
    List<IRISIndex> indexes = new ArrayList<>();
    try (Statement s = con.createStatement()) {
      try (ResultSet rs = s.executeQuery(String.format(
          "SELECT INDEX_NAME FROM INFORMATION_SCHEMA.INDEXES WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME='%s'",
          schemaName, tableName))) {
        while (rs.next()) {
          String indexName = rs.getString("INDEX_NAME");
          indexes.add(IRISIndex.create(indexName));
        }
      }
    }
    return indexes;
  }

  protected static List<IRISColumn> getTableColumns(SQLConnection con, String schemaName, String tableName)
      throws SQLException {
    List<IRISColumn> columns = new ArrayList<>();
    try (Statement s = con.createStatement()) {
      try (ResultSet rs = s
          .executeQuery(String.format("SELECT column_name, data_type FROM INFORMATION_SCHEMA.COLUMNS "
              + "WHERE TABLE_SCHEMA = '%s' and TABLE_NAME = '%s' ORDER BY column_name", schemaName,
              tableName))) {
        while (rs.next()) {
          String columnName = rs.getString("column_name");
          String dataType = rs.getString("data_type");
          IRISColumn c = new IRISColumn(columnName, getColumnType(dataType));
          columns.add(c);
        }
      }
    }
    return columns;
  }

  public static IRISSchema fromConnection(SQLConnection con, String databaseName) throws SQLException {
    try {
      List<IRISTable> databaseTables = new ArrayList<>();
      try (Statement s = con.createStatement()) {
        try (ResultSet rs = s.executeQuery(
            "SELECT table_name, table_schema FROM information_schema.tables WHERE " +
                "(not table_schema %startswith '%' and not table_schema %startswith 'Ens' and not table_schema %startswith 'INFORMATION_SCHEMA' )")) {
          while (rs.next()) {
            String tableName = rs.getString("table_name");
            String tableSchema = rs.getString("table_schema");
            boolean isInsertable = true;
            boolean isView = false;
            IRISTable.TableType tableType = IRISTable.TableType.STANDARD;
            List<IRISColumn> databaseColumns = getTableColumns(con, tableSchema, tableName);
            List<IRISIndex> indexes = getIndexes(con, tableSchema, tableName);
            IRISTable t = new IRISTable(tableName, databaseColumns, indexes, tableType,
                isView, isInsertable);
            for (IRISColumn c : databaseColumns) {
              c.setTable(t);
            }
            databaseTables.add(t);
          }
        }
      }
      return new IRISSchema(databaseTables);
    } catch (SQLIntegrityConstraintViolationException e) {
      throw new AssertionError(e);
    }
  }

  public static class IRISTables extends AbstractTables<IRISTable, IRISColumn> {

    public IRISTables(List<IRISTable> tables) {
      super(tables);
    }

    public IRISRowValue getRandomRowValue(SQLConnection con) throws SQLException {
      Map<IRISColumn, IRISConstant> values = new HashMap<>();
      return new IRISRowValue(this, values);
    }
  }

  public IRISTables getRandomTableNonEmptyTables() {
    return new IRISTables(Randomly.nonEmptySubset(getDatabaseTables()));
  }
}
