/* Generated By:JJTree: Do not edit this line. ASTType.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
public class ASTType extends SimpleNode {
  public ASTType(int id) {
    super(id);
  }

  public ASTType(SimpleCalculatorParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SimpleCalculatorParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=8aafafcfbe0519cfa1d29c0dd8435e24 (do not edit this line) */