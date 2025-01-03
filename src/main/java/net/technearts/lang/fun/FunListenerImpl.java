package net.technearts.lang.fun;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.math.BigDecimal;
import java.math.BigInteger;

import static java.lang.String.valueOf;
import static java.lang.System.err;
import static java.lang.System.out;
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
    public void enterOperatorExp(FunParser.OperatorExpContext ctx) {
        env.turnOff();
    }

    @Override
    public void exitOperatorExp(FunParser.OperatorExpContext ctx) {
        env.turnOn();
        String operatorName = ctx.ID().getText();
        Object body = ctx.op;
        env.put(operatorName, body);
    }

//    @Override
//    public void exitNonAssignExp(FunParser.NonAssignExpContext ctx) {
//        // Apenas preserva o valor da expressão avaliada na pilha
//    }

    @Override
    public void exitIntegerLiteral(FunParser.IntegerLiteralContext ctx) {
        env.push(new BigInteger(ctx.INTEGER().getText()));
    }

    @Override
    public void exitDecimalLiteral(FunParser.DecimalLiteralContext ctx) {
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
            err.printf("Warning: %s is missing from environment. Null was pushed into the stack.\n", ctx.ID().getText());
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
            err.printf("Warning: %s or %s is not supported. Null was pushed into the stack.\n", left, right);
            env.push(NULL);
        }
    }

    @Override
    public void exitMulDivModExp(FunParser.MulDivModExpContext ctx) {
        var right = new ElementWrapper(env.pop());
        var left = new ElementWrapper(env.pop());
        switch (ctx.getChild(1).getText()) {
            case "*" -> env.push(left.multiply(right));
            case "/" -> env.push(left.divide(right));
            case "%" -> env.push(left.remainder(right));
        }
    }

    @Override
    public void exitComparisonExp(FunParser.ComparisonExpContext ctx) {
        var right = new ElementWrapper(env.pop());
        var left = new ElementWrapper(env.pop());
        boolean result = switch (ctx.getChild(1).getText()) {
            case "<" -> left.compareTo(right) < 0;
            case "<=" -> left.compareTo(right) <= 0;
            case ">" -> left.compareTo(right) > 0;
            case ">=" -> left.compareTo(right) >= 0;
            default -> throw new RuntimeException("Operador de comparação desconhecido.");
        };
        env.push(result);
    }

    @Override
    public void exitEqualityExp(FunParser.EqualityExpContext ctx) {
        var right = new ElementWrapper(env.pop());
        var left = new ElementWrapper(env.pop());
        boolean result = switch (ctx.getChild(1).getText()) {
            case "=" -> left.compareTo(right) == 0;
            case "<>", "~=" -> left.compareTo(right) != 0;
            default -> throw new RuntimeException("Operador de igualdade desconhecido.");
        };
        env.push(result);
    }

    @Override
    public void enterNullTestExp(FunParser.NullTestExpContext ctx) {
        out.println("Entering Null Test");
    }

    @Override
    public void exitNullTestExp(FunParser.NullTestExpContext ctx) {
        Object right = env.pop();
        Object left = env.pop();
        env.push(left != NULL ? left : right);
        out.println("Exiting Null Test");
    }

    @Override
    public void exitTableConstructExp(FunParser.TableConstructExpContext ctx) {
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
        } else if (env.get(functionName) instanceof FunParser.ExpressionContext body) {
            // Avalia a função como um operador unário
            env.put("it", argument); // Define o "it" para o argumento da função
            env.put("this", body); // Define o "this" para o argumento da função
            new ParseTreeWalker().walk(this, body); // Avalia o corpo da função
            env.remove("it");
            env.remove("this");
        } else { // This is in fact a variable
            env.push(env.get(functionName));
        }
    }

    @Override
    public void exitThisExp(FunParser.ThisExpContext ctx) {
        if (env.isMissing("this")) {
            err.printf("Warning: 'this' is missing from environment of %s. Null was pushed into the stack.\n", ctx.getText());
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
        err.printf("Warning: Dereference of %s is missing from Table. Null was pushed into the stack.\n", key);
        env.push(NULL);
    }

    @Override
    public void exitTableConcatSepExp(FunParser.TableConcatSepExpContext ctx) {
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
            case "-" ->
                    env.push(operand instanceof BigDecimal decimal ? decimal.negate() : operand instanceof BigInteger integer ? integer.negate() : NULL);
            case "~" -> env.push(operand instanceof Boolean bool ? !bool : NULL);
            default -> throw new RuntimeException("Operador unário desconhecido: " + ctx.getChild(0).getText());
        }
    }

    @Override
    public void exitPowerExp(FunParser.PowerExpContext ctx) {
        var right = new ElementWrapper(env.pop());
        var left = new ElementWrapper(env.pop());
        env.push(left.pow(right));
    }

    @Override
    public void exitParenthesisExp(FunParser.ParenthesisExpContext ctx) {
        // Apenas empilha o resultado da expressão dentro do parêntese
    }

    @Override
    public void exitItAtomLiteral(FunParser.ItAtomLiteralContext ctx) {
        if (env.isMissing("it")) {
            err.printf("Warning: 'it' is missing from %s. Null was pushed into the stack.\n", ctx.getText());
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
            err.printf("Warning: a test was made with null condition and %s fallback. You should use the Null Test (??) operator.\n", fallback);
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

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        super.enterEveryRule(ctx);
        if (env.isDebug()) out.printf("Entering %s\n", ctx.getText());
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        super.exitEveryRule(ctx);
        if (env.isDebug()) out.printf("Exiting %s\n", ctx.getText());
    }
}

