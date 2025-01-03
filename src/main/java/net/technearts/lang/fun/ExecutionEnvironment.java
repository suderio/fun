package net.technearts.lang.fun;

import java.math.MathContext;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;
import static java.math.RoundingMode.HALF_UP;
import static net.technearts.lang.fun.ElementWrapper.Nil.NULL;

public class ExecutionEnvironment {
    private final Map<String, Object> variables = new HashMap<>();
    private final Deque<Object> stack = new ArrayDeque<>();
    private final MathContext mathContext;
    private boolean stackOn = true;

    private boolean debug = true;

    public ExecutionEnvironment() {
        mathContext = new MathContext(50, HALF_UP);
    }

    public ExecutionEnvironment(Config config) {
        mathContext = new MathContext(config.precision(), HALF_UP);
        debug = config.debug();
    }

    public boolean isDebug() {
        return debug;
    }

    public MathContext getMathContext() {
        return mathContext;
    }

    public Object get(String id) {
        return variables.get(id);
    }

    public void put(String id, Object value) {
        if (stackOn) variables.put(id, value);
    }

    public void remove(String id) {
        if (stackOn) variables.remove(id);
    }

    public boolean isMissing(String id) {
        return !variables.containsKey(id);
    }

    public Object pop() {
        if (debug) out.printf("-- pop %s\n", stack.peek());
        if (stackOn) return stack.pop();
        return NULL;
    }

    public Object peek() {
        return stack.peek();
    }

    public void push(Object o) {
        if (debug) out.printf("-- push %s\n", o);
        if (stackOn) stack.push(o);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public Object last() {
        if (debug) out.printf("-- poolLast %s\n", stack.peekLast());
        if (stackOn) return stack.pollLast();
        return NULL;
    }

    public void turnOn() {
        stackOn = true;
    }

    public void turnOff() {
        stackOn = false;
    }
}
