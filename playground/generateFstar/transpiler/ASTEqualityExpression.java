/* Generated By:JJTree: Do not edit this line. ASTEqualityExpression.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
public class ASTEqualityExpression extends SimpleNode {
  public ASTEqualityExpression(int id) {
    super(id);
  }

  public ASTEqualityExpression(SimpleCalculatorParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SimpleCalculatorParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=10101f5124c0e53a4261d39232f89c31 (do not edit this line) */