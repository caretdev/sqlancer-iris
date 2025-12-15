package sqlancer.iris.gen;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import sqlancer.Randomly;
import sqlancer.iris.IRISSchema.IRISDataType;

public final class IRISCommon {

  private IRISCommon() {

  }

  public static void appendDataType(IRISDataType type, StringBuilder sb, List<String> opClasses) {

    switch (type) {
      case VARCHAR:
        if (Randomly.getBoolean()) {
          sb.append("VAR");
        }
        sb.append("CHAR");
        sb.append("(");
        sb.append(ThreadLocalRandom.current().nextInt(1, 500));
        sb.append(")");
        if (Randomly.getBooleanWithSmallProbability()) {
          sb.append(" COLLATE ");
          sb.append(Randomly.fromList(opClasses));
        }
        break;
      case DECIMAL:
      case NUMERIC:
        sb.append(Randomly.fromOptions("NUMERIC", "DECIMAL"));
        sb.append("(");
        int precision = ThreadLocalRandom.current().nextInt(1, 38);
        sb.append(precision);
        sb.append(", ");
        int scale = ThreadLocalRandom.current().nextInt(0, precision);
        sb.append(scale);
        sb.append(")");
        break;
      case DOUBLE:
      case FLOAT:
      case REAL:
        sb.append(Randomly.fromOptions("FLOAT", "REAL", "DOUBLE"));
        break;
      default:
        sb.append(type.name());
        break;
    }
  }
}
