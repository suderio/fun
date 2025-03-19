package net.technearts.lang.fun;

import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.technearts.lang.fun.FunParser.ExpressionExpContext;

public class FunListenerImpl extends FunBaseListener {
  private ParseTreeProperty<String> values = new ParseTreeProperty<>();
  private StringBuffer sb = new StringBuffer();

  @Override
  public void visitTerminal(TerminalNode node) {
    sb.append(node.getText());
  }

  @Override
  public void exitEveryRule(ParserRuleContext ctx) {
    sb.append("Rule Children: %s\n".formatted(ctx.getText()));
  }

  @Override
  public void exitExpressionExp(ExpressionExpContext ctx) {
    sb.append("Expression: %s\n".formatted(ctx.getText()));
  }

  @Override
  public String toString() {
    return sb.toString();
  }

}
