package net.technearts.lang.fun;

import java.util.*;

public class FunVisitorImpl extends FunBaseVisitor<Object> {
    final Map<String, Object> variables = new HashMap<>();

    @Override
    public Object visitThisExp(FunParser.ThisExpContext ctx) {
        return super.visitThisExp(ctx);
    }

    @Override
    public Object visitTestExp(FunParser.TestExpContext ctx) {
        Object left = visit(ctx.expression(0));
        Object right = visit(ctx.expression(1));

        if (left instanceof Boolean) {
            return (Boolean)left ? left : right;
        } else {
            throw new RuntimeException("Não implementado.");
        }
    }

    @Override
    public Object visitCallExp(FunParser.CallExpContext ctx) {
        return super.visitCallExp(ctx);
    }

    @Override
    public Object visitFile(FunParser.FileContext ctx) {
        return super.visitFile(ctx);
    }

    @Override
    public Object visitAssignExp(FunParser.AssignExpContext ctx) {
        String variableName = ctx.ID().getText();
        Object value = visit(ctx.expression());
        variables.put(variableName, value);
        return value;
    }

    @Override
    public Object visitOperatorExp(FunParser.OperatorExpContext ctx) {
        String operatorName = ctx.ID().getText();
        Object body = visit(ctx.expression());
        variables.put(operatorName, body); // Trata o operador como uma variável
        return body;
    }

    @Override
    public Object visitNonAssignExp(FunParser.NonAssignExpContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitParenthesisExp(FunParser.ParenthesisExpContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitUnaryExp(FunParser.UnaryExpContext ctx) {
        Object operand = visit(ctx.expression());
        return switch (ctx.getChild(0).getText()) {
            case "+" -> operand; // Valor inalterado
            case "-" -> -(double) operand;
            case "~" -> !(boolean) operand;
            default -> throw new RuntimeException("Operador unário desconhecido: " + ctx.getChild(0).getText());
        };
    }

    @Override
    public Object visitDerefExp(FunParser.DerefExpContext ctx) {
        Object left = visit(ctx.expression(0));
        Object right = visit(ctx.expression(1));
        if (left instanceof Map) {
            return ((Map<?, ?>) left).get(right);
        }
        throw new RuntimeException("Operação de derefência inválida.");
    }

    @Override
    public Object visitPowerExp(FunParser.PowerExpContext ctx) {
        double base = (double) visit(ctx.expression(0));
        double exponent = (double) visit(ctx.expression(1));
        return Math.pow(base, exponent);
    }

    @Override
    public Object visitMulDivModExp(FunParser.MulDivModExpContext ctx) {
        double left = (double) visit(ctx.expression(0));
        double right = (double) visit(ctx.expression(1));
        return switch (ctx.getChild(1).getText()) {
            case "*" -> left * right;
            case "/" -> left / right;
            case "%" -> left % right;
            default -> throw new RuntimeException("Operador de multiplicação/divisão/módulo desconhecido.");
        };
    }

    @Override
    public Object visitAddSubExp(FunParser.AddSubExpContext ctx) {
        double left = (double) visit(ctx.expression(0));
        double right = (double) visit(ctx.expression(1));
        return ctx.getChild(1).getText().equals("+") ? left + right : left - right;
    }

    @Override
    public Object visitComparisonExp(FunParser.ComparisonExpContext ctx) {
        Object left = visit(ctx.expression(0));
        Object right = visit(ctx.expression(1));
        return switch (ctx.comparisonOperator().getText()) {
            case "=" -> left.equals(right);
            case "<>", "~=" -> !left.equals(right);
            case "<" -> (double) left < (double) right;
            case "<=" -> (double) left <= (double) right;
            case ">" -> (double) left > (double) right;
            case ">=" -> (double) left >= (double) right;
            default -> throw new RuntimeException("Operador de comparação desconhecido.");
        };
    }

    @Override
    public Object visitAndExp(FunParser.AndExpContext ctx) {
        boolean left = (boolean) visit(ctx.expression(0));
        boolean right = (boolean) visit(ctx.expression(1));
        return left && right;
    }

    @Override
    public Object visitOrExp(FunParser.OrExpContext ctx) {
        boolean left = (boolean) visit(ctx.expression(0));
        boolean right = (boolean) visit(ctx.expression(1));
        return left || right;
    }

    @Override
    public Object visitXorExp(FunParser.XorExpContext ctx) {
        boolean left = (boolean) visit(ctx.expression(0));
        boolean right = (boolean) visit(ctx.expression(1));
        return left ^ right;
    }

    @Override
    public Object visitTableConcatSemi(FunParser.TableConcatSemiContext ctx) {
        List<Object> table = new ArrayList<>();
        for (FunParser.ExpressionContext exprCtx : ctx.expression()) {
            table.add(visit(exprCtx));
        }
        return table;
    }

    @Override
    public Object visitTableConcatSep(FunParser.TableConcatSepContext ctx) {
        List<Object> table = new ArrayList<>();
        for (FunParser.ExpressionContext exprCtx : ctx.expression()) {
            table.add(visit(exprCtx));
        }
        return table;
    }

    @Override
    public Object visitTableConstruct(FunParser.TableConstructContext ctx) {
        List<Object> table = new ArrayList<>();
        for (FunParser.ExpressionContext exprCtx : ctx.expression()) {
            table.add(visit(exprCtx));
        }
        return table;
    }

    @Override
    public Object visitStringLiteral(FunParser.StringLiteralContext ctx) {
        return ctx.STRING().getText().replaceAll("^\"|\"$", ""); // Remove aspas
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
    public Object visitIntegerExp(FunParser.IntegerExpContext ctx) {
        return Integer.parseInt(ctx.INTEGER().getText());
    }

    @Override
    public Object visitDecimalExp(FunParser.DecimalExpContext ctx) {
        return Double.parseDouble(ctx.DECIMAL().getText());
    }

    @Override
    public Object visitIdAtomExp(FunParser.IdAtomExpContext ctx) {
        String variableName = ctx.ID().getText();
        if (!variables.containsKey(variableName)) {
            throw new RuntimeException("Variável não definida: " + variableName);
        }
        return variables.get(variableName);
    }

    @Override
    public Object visitItAtomExp(FunParser.ItAtomExpContext ctx) {
        if (!variables.containsKey("it")) {
            throw new RuntimeException("`it` não está definido.");
        }
        return variables.get("it");
    }

    @Override
    public Object visitNullTestExp(FunParser.NullTestExpContext ctx) {
        Object left = visit(ctx.expression(0));
        Object right = visit(ctx.expression(1));

        // Retorna o valor do lado esquerdo, exceto se for nulo
        return left != null ? left : right;
    }

}
