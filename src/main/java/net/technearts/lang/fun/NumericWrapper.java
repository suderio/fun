package net.technearts.lang.fun;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static java.lang.String.valueOf;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static net.technearts.lang.fun.Nil.NULL;

public class NumericWrapper implements Comparable<NumericWrapper>{
    private final Object value;
    private final MathContext mathContext = new MathContext(50, HALF_UP);

    public NumericWrapper(Object value) {
        this.value = value;
    }

    public BigDecimal getDecimal() {
        return switch (value) {
            case BigDecimal v -> v;
            case BigInteger v -> new BigDecimal(v);
            case Table v -> new BigDecimal(v.size());
            case String v -> new BigDecimal(v.length());
            case Boolean v -> v ? ONE : ZERO;
            default -> null;
        };
    }

    public BigInteger getInteger() {
        return switch (value) {
            case BigDecimal v -> v.round(mathContext).toBigInteger();
            case BigInteger v -> v;
            case Table v -> new BigInteger(valueOf(v.size()));
            case String v -> new BigInteger(valueOf(v.length()));
            case Boolean v -> v ? BigInteger.ONE : BigInteger.ZERO;
            default -> null;
        };
    }

    public Object multiply(NumericWrapper ne) {
        if (this.getDecimal() == null || ne.getDecimal() == null) {
            return NULL;
        }
        return this.getDecimal().multiply(ne.getDecimal(), mathContext);
    }

    public Object divide(NumericWrapper ne) {
        if (this.getDecimal() == null || ne.getDecimal() == null) {
            return NULL;
        }
        return this.getDecimal().divide(ne.getDecimal(), mathContext);
    }
    public Object remainder(NumericWrapper ne) {
        if (this.getDecimal() == null || ne.getDecimal() == null) {
            return NULL;
        }
        return this.getDecimal().remainder(ne.getDecimal(), mathContext);
    }

    public Object pow(NumericWrapper ne) {
        if (this.getDecimal() == null || ne.getDecimal() == null) {
            return NULL;
        }
        return Utils.pow(this.getDecimal(), ne.getDecimal());
    }
    @Override
    public int compareTo(NumericWrapper ne) {
        if (this.getDecimal() == null || ne.getDecimal() == null) {
            return -1;
        }
        return this.getDecimal().compareTo(ne.getDecimal());
    }
}
