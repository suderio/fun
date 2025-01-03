package net.technearts.lang.fun;

import jakarta.annotation.Nonnull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static java.lang.String.valueOf;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static net.technearts.lang.fun.ElementWrapper.Nil.NULL;

public class ElementWrapper<T> implements Comparable<T>{
    private final T value;
    private final MathContext mathContext = new MathContext(50, HALF_UP);

    public ElementWrapper(T value) {
        this.value = value;
    }

    public static <V> ElementWrapper<V>  wrap(V value) {
        return new ElementWrapper<>(value);
    }

    public BigDecimal getDecimal() {
        return switch (value) {
            case BigDecimal v -> v;
            case BigInteger v -> new BigDecimal(v);
            case Table v -> new BigDecimal(v.size());
            case String v -> new BigDecimal(v.length());
            case Boolean v -> v ? ONE : ZERO;
            case null, default -> null;
        };
    }

    public BigInteger getInteger() {
        return switch (value) {
            case BigDecimal v -> v.round(mathContext).toBigInteger();
            case BigInteger v -> v;
            case Table v -> new BigInteger(valueOf(v.size()));
            case String v -> new BigInteger(valueOf(v.length()));
            case Boolean v -> v ? BigInteger.ONE : BigInteger.ZERO;
            case null, default -> null;
        };
    }

    public Object multiply(ElementWrapper<?> ne) {
        if (this.getDecimal() == null || ne.getDecimal() == null) {
            return NULL;
        }
        return this.getDecimal().multiply(ne.getDecimal(), mathContext);
    }

    public Object divide(ElementWrapper<?> ne) {
        if (this.getDecimal() == null || ne.getDecimal() == null) {
            return NULL;
        }
        return this.getDecimal().divide(ne.getDecimal(), mathContext);
    }
    public Object remainder(ElementWrapper<?> ne) {
        if (this.getDecimal() == null || ne.getDecimal() == null) {
            return NULL;
        }
        return this.getDecimal().remainder(ne.getDecimal(), mathContext);
    }

    public Object pow(ElementWrapper<?> ne) {
        if (this.getDecimal() == null || ne.getDecimal() == null) {
            return NULL;
        }
        return Utils.pow(this.getDecimal(), ne.getDecimal());
    }
    @Override
    public int compareTo(@Nonnull T ne) {
        if (this.getDecimal() == null || ((ElementWrapper<?>)ne).getDecimal() == null) {
            return -1;
        }
        return this.getDecimal().compareTo(((ElementWrapper<?>)ne).getDecimal());
    }

    public enum Nil {
        NULL
    }
}
