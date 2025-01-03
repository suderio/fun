
package net.technearts.lang.fun;

import java.math.BigDecimal;
import java.math.BigInteger;

import static java.lang.String.valueOf;
import static java.lang.System.err;
import static net.technearts.lang.fun.ElementWrapper.wrap;
import static net.technearts.lang.fun.ElementWrapper.Nil.NULL;

public class FunVisitorImpl extends FunBaseVisitor<Object> {
    private final ExecutionEnvironment env;

    public FunVisitorImpl(ExecutionEnvironment env) {
        this.env = env;
    }

    @Override
    public Object visitShiftExp(FunParser.ShiftExpContext ctx) {
        var left = wrap(visit(ctx.expression(0)));  // Avalia o lado esquerdo
        var right = wrap(visit(ctx.expression(1))); // Avalia o lado direito

        if (ctx.RSHIFT() != null) {
            // Deslocamento à direita
            return left.shiftRight(right);
        } else if (ctx.LSHIFT() != null) {
            // Deslocamento à esquerda
            return left.shiftLeft(right);
        } else {
            throw new RuntimeException("Operador desconhecido para ShiftExp.");
        }
    }

    @Override
    public Object visitSubstExp(FunParser.SubstExpContext ctx) {
        // TODO
        return super.visitSubstExp(ctx);
    }

    @Override
    public Object visitAssignOpExp(FunParser.AssignOpExpContext ctx) {
        // TODO
        return super.visitAssignOpExp(ctx);
    }

    @Override
    public Object visitRangeExp(FunParser.RangeExpContext ctx) {
        // TODO
        return super.visitRangeExp(ctx);
    }

    @Override
    public Object visitDocStringLiteral(FunParser.DocStringLiteralContext ctx) {
        // TODO
        return super.visitDocStringLiteral(ctx);
    }

    @Override
    public Object visitItAtomLiteral(FunParser.ItAtomLiteralContext ctx) {
        if (env.isMissing("it")) {
            err.printf("Warning: 'it' is missing from %s. Null was pushed into the stack.\n", ctx.getText());
            return NULL;
        } else {
            return env.get("it");
        }
    }

    @Override
    public Object visitFileTable(FunParser.FileTableContext ctx) {
        Table table = new Table();
        for (var expression : ctx.assign()) {
            Object value = visit(expression);
            table.put(value);
        }
        return table;
    }

    @Override
    public Object visitExpressionExp(FunParser.ExpressionExpContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitTableConcatSepExp(FunParser.TableConcatSepExpContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));
        Table table = new Table();
        table.put(left);
        if (right instanceof Table t1) {
            t1.forEach((k, v) -> table.put(v));
        } else {
            table.put(right);
        }
        return table;
    }

    @Override
    public Object visitPowerExp(FunParser.PowerExpContext ctx) {
        var left = wrap(visit(ctx.expression(0)));
        var right = wrap(visit(ctx.expression(1)));
        return left.pow(right);
    }

    @Override
    public Object visitUnaryExp(FunParser.UnaryExpContext ctx) {
        Object operand = visit(ctx.expression());
        return switch (ctx.getChild(0).getText()) {
            case "+" -> operand;
            case "-" ->
                    operand instanceof BigDecimal decimal ? decimal.negate() : operand instanceof BigInteger integer ? integer.negate() : NULL;
            case "~" -> operand instanceof Boolean bool ? !bool : NULL;
            default -> throw new RuntimeException("Operador unário desconhecido: " + ctx.getChild(0).getText());
        };
    }

    @Override
    public Object visitParenthesisExp(FunParser.ParenthesisExpContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitAssignExp(FunParser.AssignExpContext ctx) {
        String variableName = ctx.ID().getText();
        Object value = visit(ctx.expression());
        env.put(variableName, value);
        return value;
    }

    @Override
    public Object visitOperatorExp(FunParser.OperatorExpContext ctx) {
        env.turnOff();
        var body = ctx.op;
        env.turnOn();
        env.put(ctx.ID().getText(), body);
        return body;
    }

    @Override
    public Object visitIntegerLiteral(FunParser.IntegerLiteralContext ctx) {
        return new BigInteger(ctx.INTEGER().getText());
    }

    @Override
    public Object visitDecimalLiteral(FunParser.DecimalLiteralContext ctx) {
        return new BigDecimal(ctx.DECIMAL().getText());
    }

    @Override
    public Object visitStringLiteral(FunParser.StringLiteralContext ctx) {
        return ctx.SIMPLESTRING().getText().replaceAll("^\"|\"$", "");
    }

    @Override
    public Object visitTrueLiteral(FunParser.TrueLiteralContext ctx) {
        return true;
    }

    @Override
    public Object visitFalseLiteral(FunParser.FalseLiteralContext ctx) {
        return false;
    }

    @Override
    public Object visitNullLiteral(FunParser.NullLiteralContext ctx) {
        return NULL;
    }

    @Override
    public Object visitIdAtomExp(FunParser.IdAtomExpContext ctx) {
        String variableName = ctx.ID().getText();
        if (env.isMissing(variableName)) {
            err.printf("Warning: %s is missing from environment. Null was returned.\n", variableName);
            return NULL;
        }
        return env.get(variableName);
    }

    @Override
    public Object visitAddSubExp(FunParser.AddSubExpContext ctx) {
        Object left = visit(ctx.expression(0));
        Object right = visit(ctx.expression(1));
        if (left instanceof BigDecimal || right instanceof BigDecimal) {
            BigDecimal l = new BigDecimal(valueOf(left));
            BigDecimal r = new BigDecimal(valueOf(right));
            return ctx.PLUS() != null ? l.add(r) : l.subtract(r);
        } else if (left instanceof BigInteger l && right instanceof BigInteger r) {
            return ctx.PLUS() != null ? l.add(r) : l.subtract(r);
        } else {
            err.printf("Warning: Unsupported types for addition/subtraction: %s, %s. Null was returned.\n", left, right);
            return NULL;
        }
    }

    @Override
    public Object visitMulDivModExp(FunParser.MulDivModExpContext ctx) {
        var left = new ElementWrapper<>(visit(ctx.expression(0)));
        var right = new ElementWrapper<>(visit(ctx.expression(1)));
        return switch (ctx.getChild(1).getText()) {
            case "*" -> left.multiply(right);
            case "/" -> left.divide(right);
            case "%" -> left.remainder(right);
            default -> NULL;
        };
    }

    @Override
    public Object visitComparisonExp(FunParser.ComparisonExpContext ctx) {
        var left = new ElementWrapper<>(visit(ctx.expression(0)));
        var right = new ElementWrapper<>(visit(ctx.expression(1)));
        return switch (ctx.getChild(1).getText()) {
            case "<" -> left.compareTo(right) < 0;
            case "<=" -> left.compareTo(right) <= 0;
            case ">" -> left.compareTo(right) > 0;
            case ">=" -> left.compareTo(right) >= 0;
            default -> throw new RuntimeException("Unknown comparison operator.");
        };
    }

    @Override
    public Object visitEqualityExp(FunParser.EqualityExpContext ctx) {
        Object left = visit(ctx.expression(0));
        Object right = visit(ctx.expression(1));
        return switch (ctx.getChild(1).getText()) {
            case "=" -> left.equals(right);
            case "<>", "~=" -> !left.equals(right);
            default -> throw new RuntimeException("Unknown equality operator.");
        };
    }

    @Override
    public Object visitNullTestExp(FunParser.NullTestExpContext ctx) {
        Object left = visit(ctx.expression(0));
        return left != NULL ? left : visit(ctx.expression(1));
    }

    @Override
    public Object visitTableConstructExp(FunParser.TableConstructExpContext ctx) {
        Table table = new Table();
        for (var expression : ctx.expression()) {
            table.put(visit(expression));
        }
        return table;
    }

    @Override
    public Object visitAndShortExp(FunParser.AndShortExpContext ctx) {
        boolean left = (boolean) visit(ctx.expression(0));
        if (!left) return false; // Short-circuit
        return visit(ctx.expression(1));
    }

    @Override
    public Object visitAndExp(FunParser.AndExpContext ctx) {
        boolean left = (boolean) visit(ctx.expression(0));
        boolean right = (boolean) visit(ctx.expression(1));
        return left && right;
    }

    @Override
    public Object visitXorExp(FunParser.XorExpContext ctx) {
        boolean left = (boolean) visit(ctx.expression(0));
        boolean right = (boolean) visit(ctx.expression(1));
        return left ^ right;
    }

    @Override
    public Object visitOrShortExp(FunParser.OrShortExpContext ctx) {
        boolean left = (boolean) visit(ctx.expression(0));
        if (left) return true; // Short-circuit
        return visit(ctx.expression(1));
    }

    @Override
    public Object visitOrExp(FunParser.OrExpContext ctx) {
        boolean left = (boolean) visit(ctx.expression(0));
        boolean right = (boolean) visit(ctx.expression(1));
        return left || right;
    }

    @Override
    public Object visitCallExp(FunParser.CallExpContext ctx) {
        String functionName = ctx.ID().getText();
        Object argument = visit(ctx.expression());
        if (env.isMissing(functionName)) {
            err.printf("Warning: %s is missing in environment. Null was returned.", functionName);
            return NULL;
        } else if (env.get(functionName) instanceof FunParser.ExpressionContext body) {
            env.put("it", argument);
            env.put("this", body);
            Object result = visit(body);
            env.remove("this");
            env.remove("it");
            return result;
        } else {
            return env.get(functionName);
        }
    }

    @Override
    public Object visitThisExp(FunParser.ThisExpContext ctx) {
        if (env.isMissing("this")) {
            err.println("Warning: 'this' is missing in environment. Null was returned.");
            return NULL;
        } else if (env.get("this") instanceof FunParser.ExpressionContext body) {
            var argument = visit(ctx.expression());
            var oldIt = env.get("it");
            env.put("it", argument);
            Object result = visit(body);
            env.put("it", oldIt);
            return result;
        } else {
            err.println("Warning: 'this' is missing in environment. Null was returned.");
            return NULL;
        }
    }

    @Override
    public Object visitDerefExp(FunParser.DerefExpContext ctx) {
        Object base = visit(ctx.expression(0));
        Object key = visit(ctx.expression(1));
        if (base instanceof Table table && table.containsKey(key)) {
            return table.get(key);
        }
        err.printf("Warning: Dereference of %s is missing in table. Null was returned.\n", key);
        return NULL;
    }

    @Override
    public Object visitTestExp(FunParser.TestExpContext ctx) {
        Object condition = visit(ctx.expression(0));
        if (condition == NULL) {
            err.println("Warning: Test with null condition.");
            return NULL;
        } else if (condition instanceof Boolean bool) {
            return bool ? true : visit(ctx.expression(1));
        } else if (condition instanceof Number number) {
            return BigDecimal.ZERO.compareTo(new BigDecimal(valueOf(number))) == 0 ? visit(ctx.expression(1)): true;
        } else if (condition instanceof Table table) {
            return table.isEmpty() ? visit(ctx.expression(1)) : true;
        }
        return NULL;
    }
}

