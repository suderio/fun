package net.technearts.lang.fun;

public class FunVisitorImpl extends FunBaseVisitor<Object> {
    private final ExecutionEnvironment env;

    public FunVisitorImpl(ExecutionEnvironment env) {
        this.env = env;
    }


}
