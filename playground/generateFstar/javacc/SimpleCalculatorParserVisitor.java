/* Generated By:JavaCC: Do not edit this line. SimpleCalculatorParserVisitor.java Version 4.1d1 */
public interface SimpleCalculatorParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTRoot node, Object data);
  public Object visit(ASTExpr node, Object data);
  public Object visit(ASTConditionalOrExpression node, Object data);
  public Object visit(ASTConditionalAndExpression node, Object data);
  public Object visit(ASTEqualityExpression node, Object data);
  public Object visit(ASTRelationalExpression node, Object data);
  public Object visit(ASTPrimaryPrefix node, Object data);
  public Object visit(ASTLiteral node, Object data);
  public Object visit(ASTName node, Object data);
  public Object visit(ASTInteger node, Object data);
  public Object visit(ASTFloating node, Object data);
  public Object visit(ASTCharacter node, Object data);
  public Object visit(ASTString node, Object data);
  public Object visit(ASTBooleanLiteral node, Object data);
  public Object visit(ASTNullLiteral node, Object data);
}
/* JavaCC - OriginalChecksum=9f10153ad73a3b80b1c3c584dfd680af (do not edit this line) */
