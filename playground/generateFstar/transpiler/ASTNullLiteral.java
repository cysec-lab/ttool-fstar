/* Generated By:JJTree: Do not edit this line. ASTNullLiteral.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
public class ASTNullLiteral extends SimpleNode {
  public ASTNullLiteral(int id) {
    super(id);
  }

  public ASTNullLiteral(SimpleCalculatorParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SimpleCalculatorParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=a81b2eae030880200a7bef7f19ee59b5 (do not edit this line) */