/* Generated By:JavaCC: Do not edit this line. SimpleCalculatorParserVisitor.java Version 4.1d1 */

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

// TODO: マイナス符号の対応を検討
// TODO: 整数以外の型への対応

public class SimpleCalculatorParserVisitorImpl implements SimpleCalculatorParserVisitor {

    public boolean haveMinusSign = false;

    public MethodDeclaration methodDeclaration = new MethodDeclaration();

    public String rep(List<String> children, List<String> ops) {
        String x = children.get(0);
        String y = children.get(1);

        String op = ops.get(0);
        String ret = String.format("(%s %s %s)", op, x, y);
        if (op.equals("not I32.eq")) {
            String[] sepalate_op = op.split(" ", 2);
            String not = sepalate_op[0]; // not
            op = sepalate_op[1]; // I32
            ret = String.format("(%s (%s %s %s))", not, op, x, y);
        }

        if (children.size() > 2) {
            List<String> nextChildren = new ArrayList<String>();
            List<String> nextOps = new ArrayList<String>();
            nextChildren.add(ret);
            for (int i = 2; i < children.size(); i++) {
                nextChildren.add(children.get(i));
            }
            for (int i = 1; i < ops.size(); i++) {
                nextOps.add(ops.get(i));
            }
            return rep(nextChildren, nextOps);
        }

        return ret;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        System.out.println(node);
        return null;
    }

    @Override
    public Object visit(ASTConditionRoot node, Object data) {
        System.out.println(node);
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTExpr node, Object data) {
        System.out.println(node);
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTConditionalOrExpression node, Object data) {
        System.out.println(node);
        String ret = "(";

        int leafNum = node.jjtGetNumChildren();

        if (leafNum == 1) {
            return node.jjtGetChild(0).jjtAccept(this, null).toString();
        }

        for (int i = 0; i < leafNum; i++) {
            Node n = node.jjtGetChild(i);
            String leaf = n.jjtAccept(this, null).toString();
            ret += leaf;
            if (i != leafNum - 1) {
                ret += " || ";
            }
        }
        ret += ")";

        return ret;
    }

    @Override
    public Object visit(ASTConditionalAndExpression node, Object data) {
        System.out.println(node);
        String ret = "(";

        int leafNum = node.jjtGetNumChildren();

        if (leafNum == 1) {
            return node.jjtGetChild(0).jjtAccept(this, null).toString();
        }

        for (int i = 0; i < leafNum; i++) {
            Node n = node.jjtGetChild(i);
            String leaf = n.jjtAccept(this, null).toString();
            ret += leaf;
            if (i != leafNum - 1) {
                ret += " && ";
            }
        }

        ret += ")";

        return ret;
    }

    @Override
    public Object visit(ASTEqualityExpression node, Object data) {
        System.out.println(node);

        int leafNum = node.jjtGetNumChildren();

        if (leafNum == 1) {
            return node.jjtGetChild(0).jjtAccept(this, null).toString();
        }

        List<String> ops = (List<String>) node.jjtGetValue();
        List<String> fstarOps = new ArrayList<>();

        for (int i = 0; i < ops.size(); i++) {
            if (ops.get(i).equals("==")) {
                fstarOps.add("I32.eq");
            } else if (ops.get(i).equals("!=")) {
                fstarOps.add("not I32.eq");
            } else {
                System.out.println("unkown EqualityExpression ope");
            }
        }

        List<String> leafs = new ArrayList<>();
        for (int i = 0; i < leafNum; i++) {
            Node n = node.jjtGetChild(i);
            String leaf = n.jjtAccept(this, null).toString();
            leafs.add(leaf);
        }

        String ret = rep(leafs, fstarOps);

        return ret;
    }

    @Override
    public Object visit(ASTRelationalExpression node, Object data) {
        System.out.println(node);

        int leafNum = node.jjtGetNumChildren();

        if (leafNum == 1) {
            return node.jjtGetChild(0).jjtAccept(this, null).toString();
        }

        List<String> ops = (List<String>) node.jjtGetValue();
        List<String> fstarOps = new ArrayList<>();

        for (int i = 0; i < ops.size(); i++) {
            if (ops.get(i).equals("<")) {
                fstarOps.add("I32.lt");
            } else if (ops.get(i).equals("<=")) {
                fstarOps.add("I32.lte");
            } else if (ops.get(i).equals(">")) {
                fstarOps.add("I32.gt");
            } else if (ops.get(i).equals(">=")) {
                fstarOps.add("I32.gte");
            } else {
                System.out.println("unkown EqualityExpression ope");
            }
        }

        List<String> leafs = new ArrayList<>();
        for (int i = 0; i < leafNum; i++) {
            Node n = node.jjtGetChild(i);
            String leaf = n.jjtAccept(this, null).toString();
            leafs.add(leaf);
        }

        String ret = rep(leafs, fstarOps);

        return ret;
    }

    @Override
    public Object visit(ASTAdditiveExpression node, Object data) {
        System.out.println(node);
        if (node.jjtGetValue() != null && node.jjtGetValue().toString().equals("-")) {
            this.haveMinusSign = true;
        }
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTPrimaryPrefix node, Object data) {
        System.out.println(node);
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTLiteral node, Object data) {
        System.out.println(node);
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTName node, Object data) {
        System.out.println(node);
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTInteger node, Object data) {
        System.out.println(node);

        String ret = (String) node.jjtGetValue();

        if (this.haveMinusSign) {
            ret = "-" + ret;
            this.haveMinusSign = false;
        }

        return ret;
    }

    @Override
    public Object visit(ASTFloating node, Object data) {
        System.out.println(node);

        String ret = (String) node.jjtGetValue();

        if (this.haveMinusSign) {
            ret = "-" + ret;
            this.haveMinusSign = false;
        }

        return ret;
    }

    @Override
    public Object visit(ASTCharacter node, Object data) {
        System.out.println(node);
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTString node, Object data) {
        System.out.println(node);
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTBooleanLiteral node, Object data) {
        System.out.println(node);
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTNullLiteral node, Object data) {
        System.out.println(node);
        return node.jjtGetValue();
    }

    // Method

    @Override
    public Object visit(ASTMethodDeclarationRoot node, Object data) {
        System.out.println(node);

        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        System.out.println(node);

        Node resultTypeNode = (Node) node.jjtGetChild(0);
        while (true) {
            if (resultTypeNode.jjtGetNumChildren() == 0) {
                methodDeclaration.returnType = (String) (resultTypeNode.jjtAccept(this, null));
                break;
            }
            resultTypeNode = resultTypeNode.jjtGetChild(0);
        }

        return node.jjtGetChild(1).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTResultType node, Object data) {
        System.out.println(node);
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTMethodDeclarator node, Object data) {
        System.out.println(node);

        String functionName = (String) node.jjtGetValue();
        methodDeclaration.funcName = functionName;
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTFormalParameters node, Object data) {
        System.out.println(node);

        methodDeclaration.args = new HashMap<String, String>();

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node formalParameters = (Node) node.jjtGetChild(i);
            Node formalParameter = (Node) formalParameters.jjtGetChild(0);
            Node variableDeclaratorId = (Node) formalParameters.jjtGetChild(1);

            String argType = "";
            String argName = "";
            while (true) {
                if (formalParameter.jjtGetNumChildren() == 0) {
                    argType = (String) (formalParameter.jjtAccept(this, null));
                    break;
                }

                formalParameter = formalParameter.jjtGetChild(0);

            }

            argName += (String) variableDeclaratorId.jjtAccept(this, null);
            if (variableDeclaratorId.jjtGetNumChildren() == 1) {
                argName += "[]";
            }

            methodDeclaration.args.put(argName, argType);
        }

        return (Object) methodDeclaration;
    }

    @Override
    public Object visit(ASTFormalParameter node, Object data) {
        System.out.println(node);
        return null;
    }

    public Object visit(ASTBooleanType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTCharType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTByteType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTShortType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTIntType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        // System.out.println(123);
        return node.jjtGetValue();
    }

    public Object visit(ASTLongType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTFloatType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTDoubleType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTVoidType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTBooleanBlacketType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTCharBlacketType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTByteBlacketType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTShortBlacketType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTIntBlacketType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        // System.out.println(123);
        return node.jjtGetValue();
    }

    public Object visit(ASTLongBlacketType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTFloatBlacketType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTDoubleBlacketType node, Object data) {
        System.out.println(node);
        // System.out.println(node.jjtGetValue());
        return node.jjtGetValue();
    }

    public Object visit(ASTType node, Object data) {
        System.out.println(node);
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTArrayBrackets node, Object data) {
        System.out.println(node);
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        System.out.println(node);
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTPrimitiveType node, Object data) {
        System.out.println(node);
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTPrimitiveBlacketType node, Object data) {
        System.out.println(node);
        return node.jjtGetChild(0).jjtAccept(this, null);
    }
}
/*
 * JavaCC - OriginalChecksum=b885522fef29c058f490c57bdc41604f (do not edit this
 * line)
 */