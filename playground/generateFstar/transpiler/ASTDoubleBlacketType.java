/* Generated By:JJTree: Do not edit this line. ASTDoubleBlacketType.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
public class ASTDoubleBlacketType extends SimpleNode {
  public ASTDoubleBlacketType(int id) {
    super(id);
  }

  public ASTDoubleBlacketType(SimpleCalculatorParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SimpleCalculatorParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=52d49a172924c82469428b0e89e41abd (do not edit this line) */