package net.technearts.lang.fun;

import java.math.BigInteger;
import java.util.HashMap;

import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;
import static java.util.Objects.requireNonNull;

public class Table extends HashMap<Object, Object> {
    private int lastIndex;

    @Override
    public Object put(Object key, Object value) {
        if (requireNonNull(key) instanceof BigInteger) {
            if (((BigInteger) key).compareTo(valueOf(lastIndex + 1)) == 0) {
                lastIndex++;
            }
        }
        return super.put(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (requireNonNull(key) instanceof BigInteger) {
            if (((BigInteger) key).compareTo(valueOf(lastIndex)) == 0) {
                lastIndex--;
            }
        }
        return super.remove(key, value);
    }

    public Object put(Object value) {
        return super.put(valueOf(lastIndex++), value);
    }

    public Object push(Object value) {
        if (lastIndex > 0)
            for (int i = lastIndex; i >= 0; i--) {
                put(valueOf(i + 1), super.remove(valueOf(i)));
            }
        return put(ZERO, value);
    }
}
