package net.technearts.lang.fun;

import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.technearts.lang.fun.FunParser.AddSubExpContext;

public class FunListenerImpl extends FunBaseListener {
  private ParseTreeProperty<String> values = new ParseTreeProperty<>();
  private StringBuffer sb = new StringBuffer();

  @Override
  public void visitTerminal(TerminalNode node) {
    sb.append("Node: %s\n".formatted(node.getText()));
  }

  @Override
  public void enterAddSubExp(AddSubExpContext ctx) {
    sb.append(ctx.left.toString()).append(ctx.right).append(ctx.expression(1));
  }

  @Override
  public String toString() {
    return sb.toString();
  }

}
