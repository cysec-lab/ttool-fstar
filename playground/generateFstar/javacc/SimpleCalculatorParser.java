/* Generated By:JJTree&JavaCC: Do not edit this line. SimpleCalculatorParser.java */
import java.util.List;
import java.util.ArrayList;


public class SimpleCalculatorParser/*@bgen(jjtree)*/implements SimpleCalculatorParserTreeConstants, SimpleCalculatorParserConstants {/*@bgen(jjtree)*/
  protected JJTSimpleCalculatorParserState jjtree = new JJTSimpleCalculatorParserState();public static List<String> rpn = new ArrayList<String>();


  public static void main(String [] args)
  {
    SimpleCalculatorParser parser = new SimpleCalculatorParser(System.in);

    SimpleCalculatorParserVisitor visitor = new SimpleCalculatorParserVisitorImpl();
    try {
        parser.Root().dump("");
        parser.Root().jjtAccept(visitor, null);
    } catch (Exception e) {
        System.out.println("\u69cb\u6587\u30a8\u30e9\u30fc\u3067\u3059");
        e.printStackTrace();
    }
  }

  final public SimpleNode Root() throws ParseException {
 /*@bgen(jjtree) Root */
  ASTRoot jjtn000 = new ASTRoot(JJTROOT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      Expr();
      jj_consume_token(22);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    // Node tree = jjtree.rootNode();
    // System.out.println(tree.jjtGetChild(0));
    {if (true) return jjtn000;}
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
    throw new Error("Missing return statement in function");
  }

  final public void Expr() throws ParseException {
 /*@bgen(jjtree) Expr */
  ASTExpr jjtn000 = new ASTExpr(JJTEXPR);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      ConditionalOrExpression();
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void ConditionalOrExpression() throws ParseException {
 /*@bgen(jjtree) ConditionalOrExpression */
  ASTConditionalOrExpression jjtn000 = new ASTConditionalOrExpression(JJTCONDITIONALOREXPRESSION);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t = null;
    try {
      ConditionalAndExpression();
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case OR:
          ;
          break;
        default:
          jj_la1[0] = jj_gen;
          break label_1;
        }
        t = jj_consume_token(OR);
        ConditionalAndExpression();
                                                                      rpn.add(t.toString());
      }
  jjtree.closeNodeScope(jjtn000, true);
  jjtc000 = false;

    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  final public void ConditionalAndExpression() throws ParseException {
 /*@bgen(jjtree) ConditionalAndExpression */
  ASTConditionalAndExpression jjtn000 = new ASTConditionalAndExpression(JJTCONDITIONALANDEXPRESSION);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);//   List tokens = new ArrayList();
  Token t = null;
    try {
      EqualityExpression();
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case AND:
          ;
          break;
        default:
          jj_la1[1] = jj_gen;
          break label_2;
        }
        t = jj_consume_token(AND);
        EqualityExpression();
      rpn.add(t.toString());
      }
  jjtree.closeNodeScope(jjtn000, true);
  jjtc000 = false;

    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void EqualityExpression() throws ParseException {
 /*@bgen(jjtree) EqualityExpression */
  ASTEqualityExpression jjtn000 = new ASTEqualityExpression(JJTEQUALITYEXPRESSION);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);List<String> ts = new ArrayList();
  Token t = null;
    try {
      RelationalExpression();
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case EQUALITY:
          ;
          break;
        default:
          jj_la1[2] = jj_gen;
          break label_3;
        }
        t = jj_consume_token(EQUALITY);
        RelationalExpression();
    ts.add(t.image);
    jjtn000.jjtSetValue(ts);
    rpn.add(t.toString());
      }
  jjtree.closeNodeScope(jjtn000, true);
  jjtc000 = false;

    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void RelationalExpression() throws ParseException {
 /*@bgen(jjtree) RelationalExpression */
  ASTRelationalExpression jjtn000 = new ASTRelationalExpression(JJTRELATIONALEXPRESSION);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);//   List tokens = new ArrayList();
  Token t = null;
    try {
      AdditiveExpression();
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case RELATIONAL:
          ;
          break;
        default:
          jj_la1[3] = jj_gen;
          break label_4;
        }
        t = jj_consume_token(RELATIONAL);
        AdditiveExpression();
    rpn.add(t.toString());
      }
  jjtree.closeNodeScope(jjtn000, true);
  jjtc000 = false;

    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void AdditiveExpression() throws ParseException {
 /*@bgen(jjtree) AdditiveExpression */
  ASTAdditiveExpression jjtn000 = new ASTAdditiveExpression(JJTADDITIVEEXPRESSION);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t = null;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PLUSMINUS:
        t = jj_consume_token(PLUSMINUS);
        PrimaryPrefix();
                                      jjtree.closeNodeScope(jjtn000, true);
                                      jjtc000 = false;
      if (t.toString().equals("-")) {
        int rpnSize = rpn.size();
        rpn.set(rpnSize - 1, "-" + rpn.get(rpnSize - 1).toString());
      }
        break;
      case OPEN_BRACKET:
      case IDENTIFIER:
      case INTEGER_LITERAL:
      case FLOATING_POINT_LITERAL:
      case CHARACTER_LITERAL:
      case STRING_LITERAL:
      case 24:
      case 25:
      case 26:
        PrimaryPrefix();
        break;
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void PrimaryPrefix() throws ParseException {
 /*@bgen(jjtree) PrimaryPrefix */
  ASTPrimaryPrefix jjtn000 = new ASTPrimaryPrefix(JJTPRIMARYPREFIX);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t = null;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER_LITERAL:
      case FLOATING_POINT_LITERAL:
      case CHARACTER_LITERAL:
      case STRING_LITERAL:
      case 24:
      case 25:
      case 26:
        Literal();
        break;
      case IDENTIFIER:
        Name();
        break;
      case OPEN_BRACKET:
        jj_consume_token(OPEN_BRACKET);
        Expr();
        jj_consume_token(CLOSE_BRACKET);
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void Literal() throws ParseException {
 /*@bgen(jjtree) Literal */
  ASTLiteral jjtn000 = new ASTLiteral(JJTLITERAL);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t = null;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER_LITERAL:
        Integer();
        break;
      case FLOATING_POINT_LITERAL:
        Floating();
        break;
      case CHARACTER_LITERAL:
        Character();
        break;
      case STRING_LITERAL:
        String();
        break;
      case 24:
      case 25:
        BooleanLiteral();
        break;
      case 26:
        NullLiteral();
        break;
      default:
        jj_la1[6] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void Name() throws ParseException {
 /*@bgen(jjtree) Name */
 ASTName jjtn000 = new ASTName(JJTNAME);
 boolean jjtc000 = true;
 jjtree.openNodeScope(jjtn000);Token t = null;
    try {
      t = jj_consume_token(IDENTIFIER);
      label_5:
      while (true) {
        if (jj_2_1(2)) {
          ;
        } else {
          break label_5;
        }
        jj_consume_token(23);
        jj_consume_token(IDENTIFIER);
      }
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    rpn.add(t.toString());
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void Integer() throws ParseException {
 /*@bgen(jjtree) Integer */
 ASTInteger jjtn000 = new ASTInteger(JJTINTEGER);
 boolean jjtc000 = true;
 jjtree.openNodeScope(jjtn000);Token t = null;
    try {
      t = jj_consume_token(INTEGER_LITERAL);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    jjtn000.jjtSetValue(t.image);
    rpn.add(t.toString());
    } finally {
   if (jjtc000) {
     jjtree.closeNodeScope(jjtn000, true);
   }
    }
  }

  final public void Floating() throws ParseException {
 /*@bgen(jjtree) Floating */
 ASTFloating jjtn000 = new ASTFloating(JJTFLOATING);
 boolean jjtc000 = true;
 jjtree.openNodeScope(jjtn000);Token t = null;
    try {
      t = jj_consume_token(FLOATING_POINT_LITERAL);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    rpn.add(t.toString());
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void Character() throws ParseException {
 /*@bgen(jjtree) Character */
 ASTCharacter jjtn000 = new ASTCharacter(JJTCHARACTER);
 boolean jjtc000 = true;
 jjtree.openNodeScope(jjtn000);Token t = null;
    try {
      t = jj_consume_token(CHARACTER_LITERAL);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    rpn.add(t.toString());
    } finally {
   if (jjtc000) {
     jjtree.closeNodeScope(jjtn000, true);
   }
    }
  }

  final public void String() throws ParseException {
 /*@bgen(jjtree) String */
 ASTString jjtn000 = new ASTString(JJTSTRING);
 boolean jjtc000 = true;
 jjtree.openNodeScope(jjtn000);Token t = null;
    try {
      t = jj_consume_token(STRING_LITERAL);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    rpn.add(t.toString());
    } finally {
   if (jjtc000) {
     jjtree.closeNodeScope(jjtn000, true);
   }
    }
  }

  final public void BooleanLiteral() throws ParseException {
 /*@bgen(jjtree) BooleanLiteral */
  ASTBooleanLiteral jjtn000 = new ASTBooleanLiteral(JJTBOOLEANLITERAL);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t = null;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 24:
        t = jj_consume_token(24);
        break;
      case 25:
        jj_consume_token(25);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    rpn.add(t.toString());
        break;
      default:
        jj_la1[7] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  final public void NullLiteral() throws ParseException {
 /*@bgen(jjtree) NullLiteral */
  ASTNullLiteral jjtn000 = new ASTNullLiteral(JJTNULLLITERAL);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);Token t = null;
    try {
      t = jj_consume_token(26);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
      rpn.add(t.toString());
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_3_1() {
    if (jj_scan_token(23)) return true;
    if (jj_scan_token(IDENTIFIER)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public SimpleCalculatorParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  /** Whether we are looking ahead. */
  private boolean jj_lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[8];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x80,0x40,0x20,0x10,0x7344d00,0x7344900,0x7344000,0x3000000,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[1];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public SimpleCalculatorParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public SimpleCalculatorParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new SimpleCalculatorParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public SimpleCalculatorParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new SimpleCalculatorParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public SimpleCalculatorParser(SimpleCalculatorParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(SimpleCalculatorParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = jj_lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List jj_expentries = new java.util.ArrayList();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Iterator it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.add(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[27];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 8; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 27; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
