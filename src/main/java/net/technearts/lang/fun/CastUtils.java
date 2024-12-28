package net.technearts.lang.fun;

import java.math.BigDecimal;
import java.math.BigInteger;

public class CastUtils {

    private CastUtils() {}

    public static Number toNumber(Object obj) {
        return switch (obj) {
            case Integer i -> new BigInteger(i.toString());
            case Long i -> new BigInteger(i.toString());
            case BigInteger i -> i;
            case Float d -> new BigDecimal(d);
            case Double d -> new BigDecimal(d);
            case BigDecimal d -> d;
            default -> BigInteger.ZERO;
        };
    }
}
