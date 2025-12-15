package sqlancer.iris.ast;

import sqlancer.iris.IRISSchema.IRISDataType;
import sqlancer.iris.ast.IRISConstant;

public abstract class IRISConstant implements IRISExpression {

  public abstract String getTextRepresentation();

  public abstract String getUnquotedTextRepresentation();

  public abstract IRISConstant isEquals(IRISConstant rightVal);

  protected abstract IRISConstant isLessThan(IRISConstant rightVal);

  public abstract IRISConstant cast(IRISDataType type);

  public abstract IRISDataType getExpressionType();

  public boolean isNull() {
    return false;
  }

  // public String asString() {
  //   throw new UnsupportedOperationException(this.toString());
  // }

  public boolean isString() {
    return false;
  }

  public boolean asBoolean() {
    throw new UnsupportedOperationException(this.toString());
  }

  public long asInt() {
    throw new UnsupportedOperationException(this.toString());
  }

  public boolean isBoolean() {
    return false;
  }

  public abstract static class IRISConstantBase extends IRISConstant {
    @Override
    public String getUnquotedTextRepresentation() {
      return null;
    }

    @Override
    public IRISConstant isEquals(IRISConstant rightVal) {
      return null;
    }

    @Override
    protected IRISConstant isLessThan(IRISConstant rightVal) {
      return null;
    }

    @Override
    public IRISConstant cast(IRISDataType type) {
      return null;
    }
  }

  public static class NullConstant extends IRISConstantBase {

    @Override
    public String getTextRepresentation() {
      return "NULL";
    }

    @Override
    public IRISDataType getExpressionType() {
      return IRISDataType.NULL;
    }

    @Override
    public boolean isNull() {
      return true;
    }

  }

  public static class BooleanConstant extends IRISConstantBase {
    private final boolean value;

    public BooleanConstant(boolean value) {
      this.value = value;
    }

    @Override
    public String getTextRepresentation() {
      return value ? "1" : "0";
    }

    @Override
    public boolean asBoolean() {
      return value;
    }

    @Override
    public IRISDataType getExpressionType() {
      return IRISDataType.BIT;
    }

    @Override
    public String asString() {
      return value ? "1" : "0";
    }

    @Override
    public boolean isBoolean() {
      return value;
    }

    @Override
    public long asInt() {
      return value ? 1 : 0;
    }

  }

  public static class IntConstant extends IRISConstantBase {
    private final int value;

    public IntConstant(int value) {
      this.value = value;
    }

    public IntConstant(long value) {
      this.value = (int) value;
    }

    @Override
    public String getTextRepresentation() {
      return String.valueOf(value);
    }

    @Override
    public IRISDataType getExpressionType() {
      return IRISDataType.INTEGER;
    }

    @Override
    public long asInt() {
      return value;
    }
  }

  public static class DoubleConstant extends IRISConstantBase {
    private final double value;

    public DoubleConstant(double value) {
      this.value = value;
    }

    @Override
    public String getTextRepresentation() {
      return String.valueOf(value);
    }

    @Override
    public IRISDataType getExpressionType() {
      return IRISDataType.DOUBLE;
    }

  }

  public static class StringConstant extends IRISConstantBase {
    private final String value;

    public StringConstant(String value) {
      this.value = value;
    }

    public StringConstant(String value, int size) {
      this.value = value.substring(0, Math.min(value.length(), size));
    }

    @Override
    public String getTextRepresentation() {
      return String.format("'%s'", value.replace("'", "''"));
    }

    @Override
    public IRISDataType getExpressionType() {
      return IRISDataType.VARCHAR;
    }

    @Override
    public boolean isString() {
      return true;
    }

    @Override
    public String asString() {
      return value;
    }

    @Override
    public String getUnquotedTextRepresentation() {
      return value;
    }
  }

  public static IRISConstant createBooleanConstant(boolean value) {
    return new BooleanConstant(value);
  }

  public static IRISConstant createStringConstant(String value, int size) {
    return new StringConstant(value, size);
  }

  public static IRISConstant createStringConstant(String value) {
    return new StringConstant(value);
  }

  public static IRISConstant createIntConstant(int value) {
    return new IntConstant(value);
  }

  public static IRISConstant createIntConstant(long value) {
    return new IntConstant(value);
  }

  public static IRISConstant createDoubleConstant(double value) {
    return new DoubleConstant(value);
  }

  public static IRISConstant createNullConstant() {
    return new NullConstant();
  }
}
