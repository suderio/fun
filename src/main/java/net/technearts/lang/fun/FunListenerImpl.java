package net.technearts.lang.fun;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.lang.String.valueOf;
import static net.technearts.lang.fun.CastUtils.toNumber;

public class FunListenerImpl extends FunBaseListener {
    private final ExecutionEnvironment env;

    public FunListenerImpl(ExecutionEnvironment env) {
        this.env = env;
    }

    @Override
    public void exitAssignExp(FunParser.AssignExpContext ctx) {
        String variableName = ctx.ID().getText();
        Object value = env.pop();
        env.put(variableName, value);
    }

    @Override
    public void exitOperatorExp(FunParser.OperatorExpContext ctx) {
        String operatorName = ctx.ID().getText();
        Object body = env.pop(); // Corpo do operador
        env.put(operatorName, body);
    }

    @Override
    public void exitNonAssignExp(FunParser.NonAssignExpContext ctx) {
        // Apenas preserva o valor da expressão avaliada na pilha
    }

    @Override
    public void exitIntegerExp(FunParser.IntegerExpContext ctx) {
        env.push(Integer.parseInt(ctx.INTEGER().getText()));
    }

    @Override
    public void exitDecimalExp(FunParser.DecimalExpContext ctx) {
        env.push(Double.parseDouble(ctx.DECIMAL().getText()));
    }

    @Override
    public void exitStringLiteral(FunParser.StringLiteralContext ctx) {
        env.push(ctx.STRING().getText().replaceAll("^\"|\"$", ""));
    }

    @Override
    public void exitTrueLiteral(FunParser.TrueLiteralContext ctx) {
        env.push(true);
    }

    @Override
    public void exitFalseLiteral(FunParser.FalseLiteralContext ctx) {
        env.push(false);
    }

    @Override
    public void exitNullLiteral(FunParser.NullLiteralContext ctx) {
        env.push(null);
    }

    @Override
    public void exitIdAtomExp(FunParser.IdAtomExpContext ctx) {
        String variableName = ctx.ID().getText();
        if (!env.contains(variableName)) {
            throw new RuntimeException("Variável não definida: " + variableName);
        }
        env.push(env.get(variableName));
    }

    @Override
    public void exitAddSubExp(FunParser.AddSubExpContext ctx) {
        Number right = toNumber(env.pop());
        Number left = toNumber(env.pop());
        if (right instanceof BigDecimal || left instanceof BigDecimal) {
            if (ctx.getChild(1).getText().equals("+")) {
                env.push((new BigDecimal(valueOf(left))).add((new BigDecimal(valueOf(right)))));
            } else {
                env.push((new BigDecimal(valueOf(left))).subtract((new BigDecimal(valueOf(right)))));
            }
        } else {
            if (ctx.getChild(1).getText().equals("+")) {
                env.push(((BigInteger)left).add(((BigInteger) right)));
            } else {
                env.push(((BigInteger)left).subtract(((BigInteger) right)));
            }
        }
    }

    @Override
    public void exitMulDivModExp(FunParser.MulDivModExpContext ctx) {
        Object right = env.pop();
        Object left = env.pop();
        switch (ctx.getChild(1).getText()) {
            case "*":
                env.push(((Number) left).doubleValue() * ((Number) right).doubleValue());
                break;
            case "/":
                env.push(((Number) left).doubleValue() / ((Number) right).doubleValue());
                break;
            case "%":
                env.push(((Number) left).doubleValue() % ((Number) right).doubleValue());
                break;
        }
    }

    @Override
    public void exitComparisonExp(FunParser.ComparisonExpContext ctx) {
        Object right = env.pop();
        Object left = env.pop();
        boolean result = switch (ctx.getChild(1).getText()) {
            case "=" -> Objects.equals(left, right);
            case "<>", "~=" -> !Objects.equals(left, right);
            case "<" -> ((Number) left).doubleValue() < ((Number) right).doubleValue();
            case "<=" -> ((Number) left).doubleValue() <= ((Number) right).doubleValue();
            case ">" -> ((Number) left).doubleValue() > ((Number) right).doubleValue();
            case ">=" -> ((Number) left).doubleValue() >= ((Number) right).doubleValue();
            default -> throw new RuntimeException("Operador de comparação desconhecido.");
        };
        env.push(result);
    }

    @Override
    public void exitNullTestExp(FunParser.NullTestExpContext ctx) {
        Object right = env.pop();
        Object left = env.pop();
        env.push(left != null ? left : right);
    }

    @Override
    public void exitTableConstruct(FunParser.TableConstructContext ctx) {
        List<Object> table = new ArrayList<>();
        for (int i = 0; i < ctx.expression().size(); i++) {
            table.add(env.pop());
        }
        Collections.reverse(table); // Os elementos são empilhados em ordem reversa
        env.push(table);
    }
}

