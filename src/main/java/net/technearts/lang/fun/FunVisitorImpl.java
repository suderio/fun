
package net.technearts.lang.fun;

import java.math.BigDecimal;
import java.math.BigInteger;

import static java.lang.String.valueOf;
import static java.lang.System.err;
import static net.technearts.lang.fun.Nil.NULL;

public class FunVisitorImpl extends FunBaseVisitor<Object> {
    private final ExecutionEnvironment env;

    public FunVisitorImpl(ExecutionEnvironment env) {
        this.env = env;
    }

    @Override
    public Object visitFileTable(FunParser.FileTableContext ctx) {
        // Inicializa uma nova tabela
        Table table = new Table();

        // Percorre todas as expressões na regra e avalia cada uma
        for (var expression : ctx.assign()) {
            Object value = visit(expression); // Avalia a expressão atual
            table.put(value); // Adiciona o resultado na tabela
        }

        return table; // Retorna a tabela resultante
    }

    @Override
    public Object visitExpressionExp(FunParser.ExpressionExpContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitShiftExp(FunParser.ShiftExpContext ctx) {
        return super.visitShiftExp(ctx);
    }

    @Override
    public Object visitSubstExp(FunParser.SubstExpContext ctx) {
        return super.visitSubstExp(ctx);
    }

    @Override
    public Object visitAssignOpExp(FunParser.AssignOpExpContext ctx) {
        return super.visitAssignOpExp(ctx);
    }

    @Override
    public Object visitRangeExp(FunParser.RangeExpContext ctx) {
        return super.visitRangeExp(ctx);
    }

    @Override
    public Object visitDocStringLiteral(FunParser.DocStringLiteralContext ctx) {
        return super.visitDocStringLiteral(ctx);
    }

    @Override
    public Object visitTableConcatSepExp(FunParser.TableConcatSepExpContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));
        Table table = new Table();
        table.put(left);
        if (right instanceof Table t1) {
            t1.forEach((k,v) -> table.put(v));
        } else {
            table.put(right);
        }
        return table; // Retorna a tabela resultante
    }

    @Override
    public Object visitItAtomLiteral(FunParser.ItAtomLiteralContext ctx) {
        return super.visitItAtomLiteral(ctx);
    }

    @Override
    public Object visitPowerExp(FunParser.PowerExpContext ctx) {
        return super.visitPowerExp(ctx);
    }

    @Override
    public Object visitUnaryExp(FunParser.UnaryExpContext ctx) {
        Object operand = visit(ctx.expression());
        return switch (ctx.getChild(0).getText()) {
            case "+" -> operand; // Retorna o mesmo valor
            case "-" ->
                    operand instanceof BigDecimal decimal ? decimal.negate() : operand instanceof BigInteger integer ? integer.negate() : NULL;
            case "~" -> operand instanceof Boolean bool ? !bool : NULL;
            default -> throw new RuntimeException("Operador unário desconhecido: " + ctx.getChild(0).getText());
        };
    }

    @Override
    public Object visitParenthesisExp(FunParser.ParenthesisExpContext ctx) {
        return super.visitParenthesisExp(ctx);
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
        Object body = visit(ctx.expression());
        env.turnOn();
        env.put(ctx.ID().getText(), body);
        return null;
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
        NumericWrapper left = new NumericWrapper(visit(ctx.expression(0)));
        NumericWrapper right = new NumericWrapper(visit(ctx.expression(1)));
        return switch (ctx.getChild(1).getText()) {
            case "*" -> left.multiply(right);
            case "/" -> left.divide(right);
            case "%" -> left.remainder(right);
            default -> NULL;
        };
    }

    @Override
    public Object visitComparisonExp(FunParser.ComparisonExpContext ctx) {
        NumericWrapper left = new NumericWrapper(visit(ctx.expression(0)));
        NumericWrapper right = new NumericWrapper(visit(ctx.expression(1)));
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
        Object right = visit(ctx.expression(1));
        return left != NULL ? left : right;
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
    public Object visitAndExp(FunParser.AndExpContext ctx) {
        boolean left = (boolean) visit(ctx.expression(0));
        if (!left) return false; // Short-circuit
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
    public Object visitOrExp(FunParser.OrExpContext ctx) {
        boolean left = (boolean) visit(ctx.expression(0));
        if (left) return true; // Short-circuit
        boolean right = (boolean) visit(ctx.expression(1));
        return left || right;
    }

    @Override
    public Object visitCallExp(FunParser.CallExpContext ctx) {
        String functionName = ctx.ID().getText();
        Object argument = visit(ctx.expression());
        if (env.isMissing(functionName)) {
            return NULL;
        } else if (env.get(functionName) instanceof FunParser.ExpressionContext body) {
            env.put("it", argument);
            env.put("this", body);
            Object result = visit(body);
            env.remove("it");
            env.remove("this");
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
        }
        return env.get("this");
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
        Object fallback = visit(ctx.expression(1));
        if (condition == NULL) {
            err.println("Warning: Test with null condition.");
            return NULL;
        } else if (condition instanceof Boolean bool) {
            return bool ? true : fallback;
        } else if (condition instanceof Number number) {
            return BigDecimal.ZERO.compareTo(new BigDecimal(valueOf(number))) == 0 ? fallback : condition;
        } else if (condition instanceof Table table) {
            return table.isEmpty() ? fallback : condition;
        }
        return NULL;
    }
}

