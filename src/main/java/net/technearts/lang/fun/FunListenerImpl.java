package net.technearts.lang.fun;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.math.BigDecimal;
import java.math.BigInteger;

import static java.lang.String.valueOf;
import static net.technearts.lang.fun.Nil.NULL;

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
        Object body = ctx.op;
        env.put(operatorName, body);
    }

    @Override
    public void exitNonAssignExp(FunParser.NonAssignExpContext ctx) {
        // Apenas preserva o valor da expressão avaliada na pilha
    }

    @Override
    public void exitIntegerExp(FunParser.IntegerExpContext ctx) {
        env.push(new BigInteger(ctx.INTEGER().getText()));
    }

    @Override
    public void exitDecimalExp(FunParser.DecimalExpContext ctx) {
        env.push(new BigDecimal(ctx.DECIMAL().getText()));
    }

    @Override
    public void exitStringLiteral(FunParser.StringLiteralContext ctx) {
        env.push(ctx.SIMPLESTRING().getText().replaceAll("^\"|\"$", ""));
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
        env.push(NULL);
    }

    @Override
    public void exitIdAtomExp(FunParser.IdAtomExpContext ctx) {
        String variableName = ctx.ID().getText();
        if (env.isMissing(variableName)) {
            env.push(NULL);
            // TODO warn about null result
        } else {
            env.push(env.get(variableName));
        }
    }

    @Override
    public void exitAddSubExp(FunParser.AddSubExpContext ctx) {
        var right = env.pop();
        var left = env.pop();
        if (right instanceof BigDecimal || left instanceof BigDecimal) {
            if (ctx.getChild(1).getText().equals("+")) {
                env.push((new BigDecimal(valueOf(left))).add((new BigDecimal(valueOf(right)))));
            } else {
                env.push((new BigDecimal(valueOf(left))).subtract((new BigDecimal(valueOf(right)))));
            }
        } else if (right instanceof BigInteger && left instanceof BigInteger) {
            if (ctx.getChild(1).getText().equals("+")) {
                env.push(((BigInteger) left).add(((BigInteger) right)));
            } else {
                env.push(((BigInteger) left).subtract(((BigInteger) right)));
            }
        } else {
            env.push(NULL);
        }
    }

    @Override
    public void exitMulDivModExp(FunParser.MulDivModExpContext ctx) {
        var right = new NumericWrapper(env.pop());
        var left = new NumericWrapper(env.pop());
        switch (ctx.getChild(1).getText()) {
            case "*" -> env.push(left.multiply(right));
            case "/" -> env.push(left.divide(right));
            case "%" -> env.push(left.remainder(right));
        }
    }

    @Override
    public void exitComparisonExp(FunParser.ComparisonExpContext ctx) {
        var right = new NumericWrapper(env.pop());
        var left = new NumericWrapper(env.pop());
        boolean result = switch (ctx.getChild(1).getText()) {
            case "=" -> left.compareTo(right) == 0;
            case "<>", "~=" -> left.compareTo(right) != 0;
            case "<" -> left.compareTo(right) < 0;
            case "<=" -> left.compareTo(right) <= 0;
            case ">" -> left.compareTo(right) > 0;
            case ">=" -> left.compareTo(right) >= 0;
            default -> throw new RuntimeException("Operador de comparação desconhecido.");
        };
        env.push(result);
    }

    @Override
    public void exitNullTestExp(FunParser.NullTestExpContext ctx) {
        Object right = env.pop();
        Object left = env.pop();
        env.push(left != NULL ? left : right);
    }

    @Override
    public void exitTableConstruct(FunParser.TableConstructContext ctx) {
        Table table = new Table();
        while (!env.isEmpty()) {
            table.put(env.last());
        }
        env.push(table);
    }

    @Override
    public void exitAndExp(FunParser.AndExpContext ctx) {
        boolean right = (boolean) env.pop();
        boolean left = (boolean) env.pop();
        env.push(left && right);
    }

    @Override
    public void exitOrExp(FunParser.OrExpContext ctx) {
        boolean right = (boolean) env.pop();
        boolean left = (boolean) env.pop();
        env.push(left || right);
    }

    @Override
    public void exitXorExp(FunParser.XorExpContext ctx) {
        boolean right = (boolean) env.pop();
        boolean left = (boolean) env.pop();
        env.push(left ^ right);
    }

    @Override
    public void exitCallExp(FunParser.CallExpContext ctx) {
        String functionName = ctx.ID().getText();
        Object argument = env.pop();

        if (env.isMissing(functionName)) {
            env.push(NULL);
        } else {
            // Avalia a função como um operador unário
            FunParser.ExpressionContext body = (FunParser.ExpressionContext) env.get(functionName);
            env.put("it", argument); // Define o "it" para o argumento da função
            env.put("this", body); // Define o "this" para o argumento da função
            new ParseTreeWalker().walk(this, body); // Avalia o corpo da função
            env.remove("it");
            env.remove("this");
        }
    }

    @Override
    public void exitThisExp(FunParser.ThisExpContext ctx) {
        if (env.isMissing("this")) {
            env.push(NULL);
        } else {
            env.push(env.get("this"));
        }
    }

    @Override
    public void exitDerefExp(FunParser.DerefExpContext ctx) {
        Object key = env.pop();
        Object base = env.pop();

        if (base instanceof Table) {
            if (((Table) base).containsKey(key)) {
                env.push(((Table) base).get(key));
                return;
            }
        }
        env.push(NULL);
    }

    @Override
    public void exitTableConcatSep(FunParser.TableConcatSepContext ctx) {
        var left = env.pop();
        Table table;
        if (left instanceof Table) {
            table = (Table) left;
        } else {
            table = new Table();
            table.put(left);
        }
        table.push(env.pop());
        env.push(table);
    }

    @Override
    public void exitUnaryExp(FunParser.UnaryExpContext ctx) {
        Object operand = env.pop();
        switch (ctx.getChild(0).getText()) {
            case "+" -> env.push(operand); // Retorna o mesmo valor
            case "-" -> env.push(operand instanceof BigDecimal decimal ? decimal.negate()
                    : operand instanceof BigInteger integer ? integer.negate()
                    : NULL);
            case "~" -> env.push(operand instanceof Boolean bool ? !bool : NULL);
            default -> throw new RuntimeException("Operador unário desconhecido: " + ctx.getChild(0).getText());
        }
    }

    @Override
    public void exitPowerExp(FunParser.PowerExpContext ctx) {
        var right = new NumericWrapper(env.pop());
        var left = new NumericWrapper(env.pop());
        env.push(left.pow(right));
    }

    @Override
    public void exitParenthesisExp(FunParser.ParenthesisExpContext ctx) {
        // Apenas empilha o resultado da expressão dentro do parêntese
    }

    @Override
    public void exitItAtomExp(FunParser.ItAtomExpContext ctx) {
        if (env.isMissing("it")) {
            env.push(NULL);
        } else {
            env.push(env.get("it"));
        }
    }

    @Override
    public void exitTestExp(FunParser.TestExpContext ctx) {
        Object fallback = env.pop();
        Object condition = env.pop();

        Object result = null;
        if (condition == NULL) {
            result = NULL;
        } else if (condition instanceof Boolean) {
            result = (Boolean) condition ? true : fallback;
        } else if (condition instanceof BigDecimal || condition instanceof BigInteger) {
            result = BigDecimal.ZERO.compareTo(new BigDecimal(valueOf(condition))) == 0 ? fallback : condition;
        } else if (condition instanceof Table) {
            result = ((Table) condition).isEmpty() ? fallback : condition;
        }
        env.push(result);
    }


}

