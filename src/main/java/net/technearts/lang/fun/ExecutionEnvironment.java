package net.technearts.lang.fun;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class ExecutionEnvironment {
    private final Map<String, Object> variables = new HashMap<>();
    private final Deque<Object> stack = new ArrayDeque<>();
    public Object get(String id) {
        return variables.get(id);
    }

    public void put(String id, Object value) {
        variables.put(id, value);
    }

    public boolean contains(String id) {
        return variables.containsKey(id);
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
}
