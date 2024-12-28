package net.technearts.lang.fun;

import java.math.MathContext;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static java.math.RoundingMode.HALF_UP;

public class ExecutionEnvironment {
    private final Map<String, Object> variables = new HashMap<>();
    private final Deque<Object> stack = new ArrayDeque<>();
    private final MathContext mathContext;

    public ExecutionEnvironment() {
        mathContext = new MathContext(50, HALF_UP);
    }

    public ExecutionEnvironment(Config config) {
        mathContext = new MathContext(config.precision(), HALF_UP);
    }


    public MathContext getMathContext() {
        return mathContext;
    }

    public Object get(String id) {
        return variables.get(id);
    }

    public void put(String id, Object value) {
        variables.put(id, value);
    }

    public void remove(String id) {
        variables.remove(id);
    }

    public boolean isMissing(String id) {
        return !variables.containsKey(id);
    }

    public Object pop() {
        return stack.pop();
    }

    public Object peek() {
        return stack.peek();
    }

    public void push(Object o) {
        stack.push(o);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public Object last() {
        return stack.pollLast();
    }
}
