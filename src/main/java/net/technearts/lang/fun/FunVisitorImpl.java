
package net.technearts.lang.fun;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.err;
import static net.technearts.lang.fun.ElementWrapper.Nil.NULL;
import static net.technearts.lang.fun.ElementWrapper.wrap;

public class FunVisitorImpl extends FunBaseVisitor<Object> {
    private final ExecutionEnvironment env;
    private final Table fileTable;
    @SuppressWarnings("all")
    private final static String KEY_PATTERN = "\\$\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}";

    public FunVisitorImpl(ExecutionEnvironment env) {
        this.env = env;
        this.fileTable = new Table();
    }

    @Override
    public Object visitShiftExp(FunParser.ShiftExpContext ctx) {
        var left = wrap(visit(ctx.expression(0)));
        var right = wrap(visit(ctx.expression(1)));

        if (ctx.RSHIFT() != null) {
            return left.shiftRight(right);
        } else if (ctx.LSHIFT() != null) {
            return left.shiftLeft(right);
        } else {
            throw new RuntimeException("Operador desconhecido para ShiftExp.");
        }
    }

    @Override
    public Object visitSubstExp(FunParser.SubstExpContext ctx) {
        var baseObj = visit(ctx.expression(0));
        var valuesObj = visit(ctx.expression(1));
        if (!(baseObj instanceof String base)) {
            throw new RuntimeException("O lado esquerdo do operador '$' deve ser uma String.");
        }
        Table values;
        if (valuesObj instanceof Table) {
            values = (Table) valuesObj;
        } else {
            values = new Table();
            values.put(valuesObj);
        }
        Pattern pattern = Pattern.compile("\\$(\\d+)");
        Matcher matcher = pattern.matcher(base);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            String replacement = index < values.size() ? String.valueOf(values.get(index)) : matcher.group();
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }


    public Object visitAssignOpExp(FunParser.AssignOpExpContext ctx) {
        var rightValue = wrap(visit(ctx.expression(1)));
        String variableName = ctx.expression(0).getText();
        if (!fileTable.containsKey(variableName)) {
            return NULL;
        }
        var currentValue = wrap(fileTable.get(variableName));
        Object result = switch (ctx.getChild(1).getText()) {
            case "+=" -> currentValue.add(rightValue);
            case "-=" -> currentValue.subtract(rightValue);
            case "*=" -> currentValue.multiply(rightValue);
            case "/=" -> currentValue.divide(rightValue);
            case "%=" -> currentValue.remainder(rightValue);
            default -> throw new RuntimeException("Operador de atribuição desconhecido: " + ctx.getChild(1).getText());
        };
        fileTable.put(variableName, result);
        return result;
    }

    @Override
    public Object visitRangeExp(FunParser.RangeExpContext ctx) {
        var start = wrap(visit(ctx.expression(0)));
        var end = wrap(visit(ctx.expression(1)));
        Table range = new Table();
        if (start.getInteger().compareTo(end.getInteger()) <= 0) {
            for (var i = start.getInteger(); i.compareTo(end.getInteger()) <= 0; i = i.add(BigInteger.ONE)) {
                range.put(i);
            }
        } else {
            for (var i = start.getInteger(); i.compareTo(end.getInteger()) >= 0; i = i.subtract(BigInteger.ONE)) {
                range.put(i);
            }
        }
        return range;
    }


    @Override
    public Object visitDocStringLiteral(FunParser.DocStringLiteralContext ctx) {
        // Obtem o conteúdo bruto do DocString, removendo as aspas delimitadoras
        String rawDocString = ctx.DOCSTRING().getText().replaceAll("^\"\"\"|\"\"\"$", "");
        // 1) Normaliza os terminadores de linha para ASCII LF (\n)
        String normalized = rawDocString.replace("\r\n", "\n").replace("\r", "\n");
        // 2) Remove o espaço em branco incidental com stripIndent
        String stripped = normalized.stripIndent();
        // 3) Interpreta as sequências de escape com translateEscapes e retorna.
        return stripped.translateEscapes();
    }

    @Override
    public Object visitItAtomLiteral(FunParser.ItAtomLiteralContext ctx) {
        if (!fileTable.containsKey("it")) {
            debug("Warning: 'it' is missing from %s. Null was pushed into the stack.\n", ctx.getText());
            return NULL;
        } else {
            return fileTable.get("it");
        }
    }

    @Override
    public Object visitFileTable(FunParser.FileTableContext ctx) {
        String fileName = ctx.start.getInputStream().getSourceName();
        fileTable.setName(fileName);
        for (var expression : ctx.assign()) {
            Object value = visit(expression);
            fileTable.put(value);
        }
        return fileTable;
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
        var operand = wrap(visit(ctx.expression()));
        return switch (ctx.getChild(0).getText()) {
            case "+" -> operand.get();
            case "-" -> operand.negate();
            case "~" -> !operand.getBoolean();
            case "++" -> operand.add(wrap(BigInteger.ONE));
            case "--" -> operand.subtract(wrap(BigInteger.ONE));
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
        fileTable.put(variableName, value);
        return value;
    }

    @Override
    public Object visitOperatorExp(FunParser.OperatorExpContext ctx) {
        fileTable.turnOff();
        var body = ctx.op;
        fileTable.turnOn();
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
        String rawString = ctx.SIMPLESTRING().getText().replaceAll("^\"|\"$", "");
        Pattern pattern = Pattern.compile(KEY_PATTERN);
        Matcher matcher = pattern.matcher(rawString);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String variableName = matcher.group(1);
            String replacement;
            if (!fileTable.containsKey(variableName)) {
                replacement = matcher.group();
            } else {
                replacement = String.valueOf(fileTable.get(variableName));
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
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
        if (!fileTable.containsKey(variableName)) {
            debug("Warning: %s is missing from environment. Null was returned.\n", variableName);
            return NULL;
        }
        return fileTable.get(variableName);
    }

    @Override
    public Object visitAddSubExp(FunParser.AddSubExpContext ctx) {
        var left = wrap(visit(ctx.expression(0)));
        var right = wrap(visit(ctx.expression(1)));
        return switch (ctx.getChild(1).getText()) {
            case "+" -> left.add(right);
            case "-" -> left.subtract(right);
            default -> throw new RuntimeException("Unknown AddSub operator.");
        };
    }

    @Override
    public Object visitMulDivModExp(FunParser.MulDivModExpContext ctx) {
        var left = wrap(visit(ctx.expression(0)));
        var right = wrap(visit(ctx.expression(1)));
        return switch (ctx.getChild(1).getText()) {
            case "*" -> left.multiply(right);
            case "/" -> left.divide(right);
            case "%" -> left.remainder(right);
            default -> throw new RuntimeException("Unknown MultDivMod operator.");
        };
    }

    @Override
    public Object visitComparisonExp(FunParser.ComparisonExpContext ctx) {
        var left = wrap(visit(ctx.expression(0)));
        var right = wrap(visit(ctx.expression(1)));
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
        for (var keyValue : ctx.keyValue()) {
            table.put(keyValue.ID(), visit(keyValue.expression()));
        }
        return table;
    }

    @Override
    public Object visitAndShortExp(FunParser.AndShortExpContext ctx) {
        var left = wrap(visit(ctx.expression(0)));
        if (!left.getBoolean()) return false; // Short-circuit
        return wrap(visit(ctx.expression(1))).getBoolean();
    }

    @Override
    public Object visitAndExp(FunParser.AndExpContext ctx) {
        var left = wrap(visit(ctx.expression(0)));
        var right = wrap(visit(ctx.expression(1)));
        return left.getBoolean() && right.getBoolean();
    }

    @Override
    public Object visitXorExp(FunParser.XorExpContext ctx) {
        var left = wrap(visit(ctx.expression(0)));
        var right = wrap(visit(ctx.expression(1)));
        return left.getBoolean() ^ right.getBoolean();
    }

    @Override
    public Object visitOrShortExp(FunParser.OrShortExpContext ctx) {
        var left = wrap(visit(ctx.expression(0)));
        if (left.getBoolean()) return true;
        return wrap(visit(ctx.expression(1))).getBoolean();
    }

    @Override
    public Object visitOrExp(FunParser.OrExpContext ctx) {
        var left = wrap(visit(ctx.expression(0)));
        var right = wrap(visit(ctx.expression(1)));
        return left.getBoolean() || right.getBoolean();
    }

    @Override
    public Object visitCallExp(FunParser.CallExpContext ctx) {
        String functionName = ctx.ID().getText();
        Object argument = visit(ctx.expression());
        if (!fileTable.containsKey(functionName)) {
            debug("Warning: %s is missing in environment. Null was returned.", functionName);
            return NULL;
        } else if (fileTable.get(functionName) instanceof FunParser.ExpressionContext body) {
            fileTable.put("it", argument);
            fileTable.put("this", body);
            Object result = visit(body);
            fileTable.remove("this");
            fileTable.remove("it");
            return result;
        } else {
            return fileTable.get(functionName);
        }
    }

    @Override
    public Object visitThisExp(FunParser.ThisExpContext ctx) {
        if (!fileTable.containsKey("this")) {
            debug("Warning: 'this' is missing in environment. Null was returned.");
            return NULL;
        } else if (fileTable.get("this") instanceof FunParser.ExpressionContext body) {
            var argument = visit(ctx.expression());
            var oldIt = fileTable.get("it");
            fileTable.put("it", argument);
            Object result = visit(body);
            fileTable.put("it", oldIt);
            return result;
        } else {
            debug("Warning: 'this' is missing in environment. Null was returned.");
            return NULL;
        }
    }

    @Override
    public Object visitDerefExp(FunParser.DerefExpContext ctx) {
        Object left = visit(ctx.expression(0));
        Object right = visit(ctx.expression(1));
        if (left instanceof Table t) {
            if (t.containsKey(right)) {
                return t.get(right);
            } else if (right instanceof Table ktable) {
                Table result = new Table();
                for (Map.Entry<Object, Object> e : t.entrySet()) {
                    if (e.getValue().equals(ktable.get(e.getKey()))) {
                        result.put(e.getKey(), e.getValue());
                    }
                }
                return result;
            } else if (right instanceof FunParser.ExpressionContext body) {
                Table result = new Table();
                t.forEach((k, v) -> {
                    var oldIt = fileTable.get("it");
                    fileTable.put("it", k);
                    var filterTest = wrap(visit(body));
                    fileTable.put("it", oldIt);
                    if (filterTest.getBoolean()) {
                        result.put(v);
                    }
                });
                return result;
            }
        } else if (right instanceof Table t) {
            if (t.containsValue(left)) {
                return t.get(left);
            } else if (left instanceof FunParser.ExpressionContext body) {
                Table result = new Table();
                t.forEach((k, v) -> {
                    var oldIt = fileTable.get("it");
                    fileTable.put("it", v);
                    var filterTest = wrap(visit(body));
                    fileTable.put("it", oldIt);
                    if (filterTest.getBoolean()) {
                        result.put(v);
                    }
                });
                return result;
            }
        }
        debug("Warning: Dereference of %s is missing in table. Null was returned.\n", right);
        return NULL;
    }

    @Override
    public Object visitTestExp(FunParser.TestExpContext ctx) {
        var condition = wrap(visit(ctx.expression()));
        if (condition.isNull()) {
            debug("Warning: Test with null condition.");
            return NULL;
        } else {
            return condition.getBoolean();
        }
    }

    private void debug(String msg, Object... args) {
        if (env.isDebug()) {
            if (args.length == 0) {
                err.println(msg);
            } else {
                err.printf(msg, args);
            }
        }
    }
}

