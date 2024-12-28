package net.technearts.lang.fun;

import org.junit.jupiter.api.AssertionFailureBuilder;

import java.math.BigDecimal;

public class TestUtils {
    public static void assertNumbersEqual(BigDecimal expected, BigDecimal actual) {
        if (expected.compareTo(actual) != 0) {
            AssertionFailureBuilder.assertionFailure().message(null).expected(expected).actual(actual).buildAndThrow();
        }
    }

    public static void assertNumbersEqual(BigDecimal expected, Object actual) {
        if (actual instanceof BigDecimal && expected.compareTo((BigDecimal) actual) != 0) {
            AssertionFailureBuilder.assertionFailure().message(null).expected(expected).actual(actual).buildAndThrow();
        }
    }

    public static void assertNumbersEqual(Integer expected, Object actual) {
        if (actual instanceof BigDecimal && new BigDecimal(expected).compareTo((BigDecimal) actual) != 0) {
            AssertionFailureBuilder.assertionFailure().message(null).expected(expected).actual(actual).buildAndThrow();
        }
    }

    public static void assertNumbersEqual(Double expected, Object actual) {
        if (actual instanceof BigDecimal && new BigDecimal(expected).compareTo((BigDecimal) actual) != 0) {
            AssertionFailureBuilder.assertionFailure().message(null).expected(expected).actual(actual).buildAndThrow();
        }
    }
}
