import java.util.*;

public class FunVisitorImpl extends FunBaseVisitor<Object> {
    final Map<String, Object> variables = new HashMap<>();
    private final Map<String, FunParser.ExpressionContext> unaryOperators = new HashMap<>();

    @Override
    public Object visitExpressionAssignment(FunParser.ExpressionAssignmentContext ctx) {
        String variableName = ctx.getChild(0).getText();
        Object value = visit(ctx.getChild(2));
        variables.put(variableName, value);
        return value;
    }

    @Override
    public Object visitLogicalOr(FunParser.LogicalOrContext ctx) {
        boolean left = (boolean) visit(ctx.logicalOrExpression());
        if (left) return true; // Curto-circuito
        return (boolean) visit(ctx.logicalAndExpression());
    }

    @Override
    public Object visitLogicalAnd(FunParser.LogicalAndContext ctx) {
        boolean left = (boolean) visit(ctx.logicalAndExpression());
        return left && (boolean) visit(ctx.comparisonExpression());
    }

    @Override
    public Object visitComparison(FunParser.ComparisonContext ctx) {
        Object left = visit(ctx.additionExpression(0));
        Object right = visit(ctx.additionExpression(1));

        return switch (ctx.comparisonOperator().toString()) {
            case "=" -> left.equals(right);
            case "<>", "~=" -> !left.equals(right);
            case "<" -> (double) left < (double) right;
            case "<=" -> (double) left <= (double) right;
            case ">" -> (double) left > (double) right;
            case ">=" -> (double) left >= (double) right;
            default -> throw new RuntimeException("Operador desconhecido: " + ctx.comparisonOperator().toString());
        };
    }

    @Override
    public Object visitAddition(FunParser.AdditionContext ctx) {
        double left = (double) visit(ctx.additionExpression());
        double right = (double) visit(ctx.multiplicationExpression());
        return ctx.PLUS() != null ? left + right : left - right;
    }

    @Override
    public Object visitMultiplication(FunParser.MultiplicationContext ctx) {
        double left = (double) visit(ctx.multiplicationExpression());
        double right = (double) visit(ctx.powerExpression());
        return switch (ctx.op.getType()) {
            case FunLexer.ASTERISK -> left * right;
            case FunLexer.DIVISION -> left / right;
            case FunLexer.MOD -> left % right;
            default -> throw new RuntimeException("Operador desconhecido: " + ctx.op.getText());
        };
    }

    @Override
    public Object visitDefineUnaryOperator(FunParser.DefineUnaryOperatorContext ctx) {
        String operatorName = ctx.ID().getText();
        unaryOperators.put(operatorName, ctx.expression());
        return null;
    }

    @Override
    public Object visitCallUnaryOperator(FunParser.CallUnaryOperatorContext ctx) {
        String operatorName = ctx.ID().getText();
        if (!unaryOperators.containsKey(operatorName)) {
            throw new RuntimeException("Operador não definido: " + operatorName);
        }

        Object operand = visit(ctx.expression());
        variables.put("it", operand);

        FunParser.ExpressionContext body = unaryOperators.get(operatorName);
        return visit(body);
    }

    @Override
    public Object visitTableConstruct(FunParser.TableConstructContext ctx) {
        Map<Object, Object> table = new LinkedHashMap<>();
        int index = 0;

        for (FunParser.TableElementContext element : ctx.tableElement()) {
            if (element instanceof FunParser.TableKeyValueContext) {
                Object key = visit(((FunParser.TableKeyValueContext) element).expression(0));
                Object value = visit(((FunParser.TableKeyValueContext) element).expression(1));
                table.put(key, value);
            } else {
                table.put(index++, visit(element.getChild(0)));
            }
        }

        return table;
    }

    @Override
    public Object visitTableConcat(FunParser.TableConcatContext ctx) {
        Map<Object, Object> table = new LinkedHashMap<>();
        int index = 0;

        Object first = visit(ctx.unaryOperatorCall() != null ? ctx.unaryOperatorCall() : ctx.logicalOrExpression());
        table.put(index++, first);

        for (FunParser.TableElementContext element : ctx.tableElement()) {
            if (element instanceof FunParser.TableKeyValueContext) {
                Object key = visit(((FunParser.TableKeyValueContext) element).expression(0));
                Object value = visit(((FunParser.TableKeyValueContext) element).expression(1));
                table.put(key, value);
            } else {
                table.put(index++, visit(element.getChild(0)));
            }
        }

        return table;
    }

    @Override
    public Object visitNumber(FunParser.NumberContext ctx) {
        return ctx.NUMBER().getText().contains(".")
                ? Double.parseDouble(ctx.NUMBER().getText())
                : Integer.parseInt(ctx.NUMBER().getText());
    }

    @Override
    public Object visitString(FunParser.StringContext ctx) {
        return ctx.STRING().getText().replaceAll("^\"|\"$", "");
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
        return null;
    }

    @Override
    public Object visitVariable(FunParser.VariableContext ctx) {
        String variableName = ctx.ID().getText();
        if (!variables.containsKey(variableName)) {
            throw new RuntimeException("Variável não definida: " + variableName);
        }
        return variables.get(variableName);
    }
}

