package sqlancer.iris.ast;

import java.util.function.BinaryOperator;

import sqlancer.Randomly;
import sqlancer.common.ast.BinaryOperatorNode;
import sqlancer.common.ast.BinaryOperatorNode.Operator;
import sqlancer.iris.ast.IRISBinaryArithmeticOperation.IRISBinaryOperator;
import sqlancer.iris.IRISSchema.IRISDataType;

public class IRISBinaryArithmeticOperation extends BinaryOperatorNode<IRISExpression, IRISBinaryOperator>
    implements IRISExpression {

  public IRISBinaryArithmeticOperation(IRISExpression left, IRISExpression right, IRISBinaryOperator op) {
    super(left, right, op);
  }

  public enum IRISBinaryOperator implements Operator {
    ADD("+") {
      @Override
      public IRISConstant apply(IRISConstant left, IRISConstant right) {
        return applyBitOperation(left, right, (l, r) -> l + r);
      }
    },
    SUBTRACT("-") {
      @Override
      public IRISConstant apply(IRISConstant left, IRISConstant right) {
        return applyBitOperation(left, right, (l, r) -> l - r);
      }
    },
    MULTIPLY("*") {
      @Override
      public IRISConstant apply(IRISConstant left, IRISConstant right) {
        return applyBitOperation(left, right, (l, r) -> l * r);
      }
    },
    DIVIDE("/") {
      @Override
      public IRISConstant apply(IRISConstant left, IRISConstant right) {
        return applyBitOperation(left, right, (l, r) -> r == 0 ? -1 : l / r);
      }
    },
    MODULO("#") {
      @Override
      public IRISConstant apply(IRISConstant left, IRISConstant right) {
        return applyBitOperation(left, right, (l, r) -> r == 0 ? -1 : l % r);
      }
    },
    INTDEV("\\") {
      @Override
      public IRISConstant apply(IRISConstant left, IRISConstant right) {
        return applyBitOperation(left, right, (l, r) -> r == 0 ? -1 : (long) Math.toIntExact(l / r));
      }
    };

    private String textRepresentation;

    private IRISBinaryOperator(String textRepresentation) {
      this.textRepresentation = textRepresentation;
    }

    @Override
    public String getTextRepresentation() {
      return textRepresentation;
    }

    private static IRISConstant applyBitOperation(IRISConstant left, IRISConstant right,
        BinaryOperator<Long> op) {
      if (left.isNull() || right.isNull()) {
        return IRISConstant.createNullConstant();
      } else {
        long leftVal = left.cast(IRISDataType.INTEGER).asInt();
        long rightVal = right.cast(IRISDataType.INTEGER).asInt();
        long value = op.apply(leftVal, rightVal);
        return IRISConstant.createIntConstant(value);
      }
    }

    public abstract IRISConstant apply(IRISConstant left, IRISConstant right);

    public static IRISBinaryOperator getRandom() {
      return Randomly.fromOptions(values());
    }

  }

  @Override
  public IRISConstant getExpectedValue() {
    IRISConstant leftExpected = getLeft().getExpectedValue();
    IRISConstant rightExpected = getRight().getExpectedValue();
    if (leftExpected == null || rightExpected == null) {
      return null;
    }
    return getOp().apply(leftExpected, rightExpected);
  }

  @Override
  public IRISDataType getExpressionType() {
    return IRISDataType.INTEGER;
  }
}
