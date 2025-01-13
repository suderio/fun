package net.technearts.lang.fun;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class FunListenerImpl extends FunBaseListener {
    @Override
    public void enterFileTable(FunParser.FileTableContext ctx) {
        System.out.println("enterFileTable(ctx)");
    }

    @Override
    public void exitFileTable(FunParser.FileTableContext ctx) {
        System.out.println("exitFileTable(ctx)");
    }

    @Override
    public void enterAssignExp(FunParser.AssignExpContext ctx) {
        System.out.println("enterAssignExp(ctx)");
    }

    @Override
    public void exitAssignExp(FunParser.AssignExpContext ctx) {
        System.out.println("exitAssignExp(ctx)");
    }

    @Override
    public void enterOperatorExp(FunParser.OperatorExpContext ctx) {
        System.out.println("enterOperatorExp(ctx)");
    }

    @Override
    public void exitOperatorExp(FunParser.OperatorExpContext ctx) {
        System.out.println("exitOperatorExp(ctx)");
    }

    @Override
    public void enterExpressionExp(FunParser.ExpressionExpContext ctx) {
        System.out.println("enterExpressionExp(ctx)");
    }

    @Override
    public void exitExpressionExp(FunParser.ExpressionExpContext ctx) {
        System.out.println("exitExpressionExp(ctx)");
    }

    @Override
    public void enterDecimalLiteral(FunParser.DecimalLiteralContext ctx) {
        System.out.println("enterDecimalLiteral(ctx)");
    }

    @Override
    public void exitDecimalLiteral(FunParser.DecimalLiteralContext ctx) {
        System.out.println("exitDecimalLiteral(ctx)");
    }

    @Override
    public void enterAndExp(FunParser.AndExpContext ctx) {
        System.out.println("enterAndExp(ctx)");
    }

    @Override
    public void exitAndExp(FunParser.AndExpContext ctx) {
        System.out.println("exitAndExp(ctx)");
    }

    @Override
    public void enterOrShortExp(FunParser.OrShortExpContext ctx) {
        System.out.println("enterOrShortExp(ctx)");
    }

    @Override
    public void exitOrShortExp(FunParser.OrShortExpContext ctx) {
        System.out.println("exitOrShortExp(ctx)");
    }

    @Override
    public void enterNullTestExp(FunParser.NullTestExpContext ctx) {
        System.out.println("enterNullTestExp(ctx)");
    }

    @Override
    public void exitNullTestExp(FunParser.NullTestExpContext ctx) {
        System.out.println("exitNullTestExp(ctx)");
    }

    @Override
    public void enterTrueLiteral(FunParser.TrueLiteralContext ctx) {
        System.out.println("enterTrueLiteral(ctx)");
    }

    @Override
    public void exitTrueLiteral(FunParser.TrueLiteralContext ctx) {
        System.out.println("exitTrueLiteral(ctx)");
    }

    @Override
    public void enterIdAtomExp(FunParser.IdAtomExpContext ctx) {
        System.out.println("enterIdAtomExp(ctx)");
    }

    @Override
    public void exitIdAtomExp(FunParser.IdAtomExpContext ctx) {
        System.out.println("exitIdAtomExp(ctx)");
    }

    @Override
    public void enterFalseLiteral(FunParser.FalseLiteralContext ctx) {
        System.out.println("enterFalseLiteral(ctx)");
    }

    @Override
    public void exitFalseLiteral(FunParser.FalseLiteralContext ctx) {
        System.out.println("exitFalseLiteral(ctx)");
    }

    @Override
    public void enterShiftExp(FunParser.ShiftExpContext ctx) {
        System.out.println("enterShiftExp(ctx)");
    }

    @Override
    public void exitShiftExp(FunParser.ShiftExpContext ctx) {
        System.out.println("exitShiftExp(ctx)");
    }

    @Override
    public void enterXorExp(FunParser.XorExpContext ctx) {
        System.out.println("enterXorExp(ctx)");
    }

    @Override
    public void exitXorExp(FunParser.XorExpContext ctx) {
        System.out.println("exitXorExp(ctx)");
    }

    @Override
    public void enterThisExp(FunParser.ThisExpContext ctx) {
        System.out.println("enterThisExp(ctx)");
    }

    @Override
    public void exitThisExp(FunParser.ThisExpContext ctx) {
        System.out.println("exitThisExp(ctx)");
    }

    @Override
    public void enterDerefExp(FunParser.DerefExpContext ctx) {
        System.out.println("enterDerefExp(ctx)");
    }

    @Override
    public void exitDerefExp(FunParser.DerefExpContext ctx) {
        System.out.println("exitDerefExp(ctx)");
    }

    @Override
    public void enterSubstExp(FunParser.SubstExpContext ctx) {
        System.out.println("enterSubstExp(ctx)");
    }

    @Override
    public void exitSubstExp(FunParser.SubstExpContext ctx) {
        System.out.println("exitSubstExp(ctx)");
    }

    @Override
    public void enterAndShortExp(FunParser.AndShortExpContext ctx) {
        System.out.println("enterAndShortExp(ctx)");
    }

    @Override
    public void exitAndShortExp(FunParser.AndShortExpContext ctx) {
        System.out.println("exitAndShortExp(ctx)");
    }

    @Override
    public void enterTestExp(FunParser.TestExpContext ctx) {
        System.out.println("enterTestExp(ctx)");
    }

    @Override
    public void exitTestExp(FunParser.TestExpContext ctx) {
        System.out.println("exitTestExp(ctx)");
    }

    @Override
    public void enterNullLiteral(FunParser.NullLiteralContext ctx) {
        System.out.println("enterNullLiteral(ctx)");
    }

    @Override
    public void exitNullLiteral(FunParser.NullLiteralContext ctx) {
        System.out.println("exitNullLiteral(ctx)");
    }

    @Override
    public void enterTableConstructExp(FunParser.TableConstructExpContext ctx) {
        System.out.println("enterTableConstructExp(ctx)");
    }

    @Override
    public void exitTableConstructExp(FunParser.TableConstructExpContext ctx) {
        System.out.println("exitTableConstructExp(ctx)");
    }

    @Override
    public void enterEqualityExp(FunParser.EqualityExpContext ctx) {
        System.out.println("enterEqualityExp(ctx)");
    }

    @Override
    public void exitEqualityExp(FunParser.EqualityExpContext ctx) {
        System.out.println("exitEqualityExp(ctx)");
    }

    @Override
    public void enterDocStringLiteral(FunParser.DocStringLiteralContext ctx) {
        System.out.println("enterDocStringLiteral(ctx)");
    }

    @Override
    public void exitDocStringLiteral(FunParser.DocStringLiteralContext ctx) {
        System.out.println("exitDocStringLiteral(ctx)");
    }

    @Override
    public void enterOrExp(FunParser.OrExpContext ctx) {
        System.out.println("enterOrExp(ctx)");
    }

    @Override
    public void exitOrExp(FunParser.OrExpContext ctx) {
        System.out.println("exitOrExp(ctx)");
    }

    @Override
    public void enterCallExp(FunParser.CallExpContext ctx) {
        System.out.println("enterCallExp(ctx)");
    }

    @Override
    public void exitCallExp(FunParser.CallExpContext ctx) {
        System.out.println("exitCallExp(ctx)");
    }

    @Override
    public void enterAddSubExp(FunParser.AddSubExpContext ctx) {
        System.out.println("enterAddSubExp(ctx)");
    }

    @Override
    public void exitAddSubExp(FunParser.AddSubExpContext ctx) {
        System.out.println("exitAddSubExp(ctx)");
    }

    @Override
    public void enterAssignOpExp(FunParser.AssignOpExpContext ctx) {
        System.out.println("enterAssignOpExp(ctx)");
    }

    @Override
    public void exitAssignOpExp(FunParser.AssignOpExpContext ctx) {
        System.out.println("exitAssignOpExp(ctx)");
    }

    @Override
    public void enterRangeExp(FunParser.RangeExpContext ctx) {
        System.out.println("enterRangeExp(ctx)");
    }

    @Override
    public void exitRangeExp(FunParser.RangeExpContext ctx) {
        System.out.println("exitRangeExp(ctx)");
    }

    @Override
    public void enterPowerExp(FunParser.PowerExpContext ctx) {
        System.out.println("enterPowerExp(ctx)");
    }

    @Override
    public void exitPowerExp(FunParser.PowerExpContext ctx) {
        System.out.println("exitPowerExp(ctx)");
    }

    @Override
    public void enterMulDivModExp(FunParser.MulDivModExpContext ctx) {
        System.out.println("enterMulDivModExp(ctx)");
    }

    @Override
    public void exitMulDivModExp(FunParser.MulDivModExpContext ctx) {
        System.out.println("exitMulDivModExp(ctx)");
    }

    @Override
    public void enterUnaryExp(FunParser.UnaryExpContext ctx) {
        System.out.println("enterUnaryExp(ctx)");
    }

    @Override
    public void exitUnaryExp(FunParser.UnaryExpContext ctx) {
        System.out.println("exitUnaryExp(ctx)");
    }

    @Override
    public void enterStringLiteral(FunParser.StringLiteralContext ctx) {
        System.out.println("enterStringLiteral(ctx)");
    }

    @Override
    public void exitStringLiteral(FunParser.StringLiteralContext ctx) {
        System.out.println("exitStringLiteral(ctx)");
    }

    @Override
    public void enterComparisonExp(FunParser.ComparisonExpContext ctx) {
        System.out.println("enterComparisonExp(ctx)");
    }

    @Override
    public void exitComparisonExp(FunParser.ComparisonExpContext ctx) {
        System.out.println("exitComparisonExp(ctx)");
    }

    @Override
    public void enterTableConcatSepExp(FunParser.TableConcatSepExpContext ctx) {
        System.out.println("enterTableConcatSepExp(ctx)");
    }

    @Override
    public void exitTableConcatSepExp(FunParser.TableConcatSepExpContext ctx) {
        System.out.println("exitTableConcatSepExp(ctx)");
    }

    @Override
    public void enterParenthesisExp(FunParser.ParenthesisExpContext ctx) {
        System.out.println("enterParenthesisExp(ctx)");
    }

    @Override
    public void exitParenthesisExp(FunParser.ParenthesisExpContext ctx) {
        System.out.println("exitParenthesisExp(ctx)");
    }

    @Override
    public void enterIntegerLiteral(FunParser.IntegerLiteralContext ctx) {
        System.out.println("enterIntegerLiteral(ctx)");
    }

    @Override
    public void exitIntegerLiteral(FunParser.IntegerLiteralContext ctx) {
        System.out.println("exitIntegerLiteral(ctx)");
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        super.enterEveryRule(ctx);
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        super.exitEveryRule(ctx);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        super.visitTerminal(node);
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        super.visitErrorNode(node);
    }
}

