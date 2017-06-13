/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

   ludovic.apvrille AT enst.fr

   This software is a computer program whose purpose is to allow the
   edition of TURTLE analysis, design and deployment diagrams, to
   allow the generation of RT-LOTOS or Java code from this diagram,
   and at last to allow the analysis of formal validation traces
   obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
   from INRIA Rhone-Alpes.

   This software is governed by the CeCILL  license under French law and
   abiding by the rules of distribution of free software.  You can  use,
   modify and/ or redistribute the software under the terms of the CeCILL
   license as circulated by CEA, CNRS and INRIA at the following URL
   "http://www.cecill.info".

   As a counterpart to the access to the source code and  rights to copy,
   modify and redistribute granted by the license, users are provided only
   with a limited warranty  and the software's author,  the holder of the
   economic rights,  and the successive licensors  have only  limited
   liability.

   In this respect, the user's attention is drawn to the risks associated
   with loading,  using,  modifying and/or developing or reproducing the
   software by the user in light of its specific status of free software,
   that may mean  that it is complicated to manipulate,  and  that  also
   therefore means  that it is reserved for developers  and  experienced
   professionals having in-depth computer knowledge. Users are therefore
   encouraged to load and test the software's suitability as regards their
   requirements in conditions enabling the security of their systems and/or
   data to be ensured and,  more generally, to use and operate it in the
   same conditions as regards security.

   The fact that you are presently reading this means that you have had
   knowledge of the CeCILL license and that you accept its terms.

   /**
   * Class AvatarMADAssumption
   * Avatar assumption: to be used in Modeling Assumptions diagram of AVATAR
   * Creation: 27/08/2013
   * @version 1.0 27/08/2013
   * @author Ludovic APVRILLE
   * @see
   */

package ui.avatarmad;


import myutil.Conversion;
import myutil.GraphicLib;
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogAssumption;

import javax.swing.*;
import java.awt.*;

public class AvatarMADAssumption extends TGCScalableWithInternalComponent implements WithAttributes, TGAutoAdjust {
    public String oldValue;
    protected int textX = 5;
    protected int textY = 22;
    protected int lineHeight = 30;
    private double dlineHeight = 0.0;
    //protected int reqType = 0;
    // 0: normal, 1: formal, 2: security
    //protected int startFontSize = 10;
    protected Graphics graphics;
    //protected int iconSize = 30;

    private Font myFont, myFontB;
    private int maxFontSize = 30;
    private int minFontSize = 4;
    private int currentFontSize = -1;
    private boolean displayText = true;

    public final static String[] ASSUMPTION_TYPE_STR = {"<<System Assumption>>", "<<Environment Assumption>>"};


    public final static String[] DURABILITY_TYPE = {"Undefined", "Permanent", "Temporary"};


    public final static String[] SOURCE_TYPE = {"Undefined", "End-user", "Stakeholder", "Model creator"};


    public final static String[] STATUS_TYPE = {"Undefined", "Applied", "Alleviated"};


    public final static String[] LIMITATION_TYPE = {"Undefined", "Language", "Tool", "Modeling activity", "Verification"};


    protected String text;
    protected String []texts;
    protected int type = 0;
    protected int durability = 0;
    protected int source = 0;
    protected int status = 0;
    protected int limitation = 0;


    // Icon
    private int iconSize = 18;
    private boolean iconIsDrawn = false;

    public AvatarMADAssumption(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        initScaling(200, 120);
        oldScaleFactor = tdp.getZoom();
        dlineHeight = lineHeight * oldScaleFactor;
        lineHeight = (int)dlineHeight;
        dlineHeight = dlineHeight - lineHeight;

        minWidth = 1;
        minHeight = lineHeight;

        nbConnectingPoint = 28;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.0, 0.25, TGConnectingPoint.WEST);
        connectingPoint[1] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.0, 0.5, TGConnectingPoint.WEST);
        connectingPoint[2] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.0, 0.75, TGConnectingPoint.WEST);
        connectingPoint[3] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 1.0, 0.25, TGConnectingPoint.EAST);
        connectingPoint[4] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 1.0, 0.5, TGConnectingPoint.EAST);
        connectingPoint[5] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 1.0, 0.75, TGConnectingPoint.EAST);
        connectingPoint[6] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.25, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[7] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.5, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[8] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.75, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[9] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.25, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[10] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[11] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.75, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[12] = new AvatarMADToAssumptionsConnectingPoint(this, 0, 0, false, true, 0.0, 0.25, TGConnectingPoint.WEST);
        connectingPoint[13] = new AvatarMADToAssumptionsConnectingPoint(this, 0, 0, false, true, 0.0, 0.5, TGConnectingPoint.WEST);
        connectingPoint[14] = new AvatarMADToAssumptionsConnectingPoint(this, 0, 0, false, true, 0.0, 0.75, TGConnectingPoint.WEST);
        connectingPoint[15] = new AvatarMADToAssumptionsConnectingPoint(this, 0, 0, false, true, 1.0, 0.25, TGConnectingPoint.EAST);
        connectingPoint[16] = new AvatarMADToAssumptionsConnectingPoint(this, 0, 0, false, true, 1.0, 0.5, TGConnectingPoint.EAST);
        connectingPoint[17] = new AvatarMADToAssumptionsConnectingPoint(this, 0, 0, false, true, 1.0, 0.75, TGConnectingPoint.EAST);
        connectingPoint[18] = new AvatarMADToAssumptionsConnectingPoint(this, 0, 0, false, true, 0.25, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[19] = new AvatarMADToAssumptionsConnectingPoint(this, 0, 0, false, true, 0.5, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[20] = new AvatarMADToAssumptionsConnectingPoint(this, 0, 0, false, true, 0.75, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[21] = new AvatarMADToAssumptionsConnectingPoint(this, 0, 0, false, true, 0.25, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[22] = new AvatarMADToAssumptionsConnectingPoint(this, 0, 0, false, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[23] = new AvatarMADToAssumptionsConnectingPoint(this, 0, 0, false, true, 0.75, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[24] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[25] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[26] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[27] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);


        addTGConnectingPointsCommentTop();

        nbInternalTGComponent = 0;
        //tgcomponent = new TGComponent[nbInternalTGComponent];

        int h = 1;
        //TAttributeRequirement tgc0;
        //tgc0 = new TAttributeRequirement(x, y+height+h, 0, 0, height + h, height+h, true, this, _tdp);
        //tgcomponent[0] = tgc0;

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        multieditable = true;


        // Name of the requirement
        name = "Assumption";
        try {
            value = tdp.findAvatarAssumptionName("Assumption_", 0);
        } catch (Exception e) {
            value = tdp.findAvatarAssumptionName("Assumption_", 0);
        }
        oldValue = value;

        myImageIcon = IconManager.imgic5060;

        text = "Assumption description:\nDouble-click to edit";

        actionOnAdd();
    }

    public void makeValue() {
        texts = Conversion.wrapText(text);
    }

    public void internalDrawing(Graphics g) {
        Font f = g.getFont();
        Font fold = f;
        int w, c;
        int size;

        if (texts == null) {
            makeValue();
        }

        if (!tdp.isScaled()) {
            graphics = g;
        }

        if (((rescaled) && (!tdp.isScaled())) || myFont == null) {
            currentFontSize = tdp.getFontSize();
            //System.out.println("Rescaled, font size = " + currentFontSize + " height=" + height);
            myFont = f.deriveFont((float)currentFontSize);
            myFontB = myFont.deriveFont(Font.BOLD);

            if (rescaled) {
                rescaled = false;
            }
        }

        displayText = currentFontSize >= minFontSize;

        int h  = g.getFontMetrics().getHeight();

        g.drawRect(x, y, width, height);

        g.drawLine(x, y+lineHeight, x+width, y+lineHeight);
        g.setColor(ColorManager.AVATAR_ASSUMPTION_TOP);
        g.fillRect(x+1, y+1, width-1, lineHeight-1);
        g.setColor(ColorManager.AVATAR_ASSUMPTION_ATTRIBUTES);
        g.fillRect(x+1, y+1+lineHeight, width-1, height-1-lineHeight);
        ColorManager.setColor(g, getState(), 0);
        if ((lineHeight > 23) && (width > 23)){
            g.drawImage(IconManager.img5100, x + width - iconSize + 1, y + 3, Color.yellow, null);
        }

        if (displayText) {
            size = currentFontSize - 2;
            g.setFont(myFont.deriveFont((float)(myFont.getSize() - 2)));

            drawLimitedString(g, ASSUMPTION_TYPE_STR[type], x, y + size, width, 1);

            size += currentFontSize;
            g.setFont(myFontB);
            w = g.getFontMetrics().stringWidth(value);
            drawLimitedString(g, value, x, y + size, width, 1);

        }

        /*if (verified) {
          if (satisfied) {
          Color tmp = g.getColor();
          GraphicLib.setMediumStroke(g);
          g.setColor(Color.green);
          g.drawLine(x+width-2, y-6+lineHeight, x+width-6, y-2+lineHeight);
          g.drawLine(x+width-6, y-3+lineHeight, x+width-8, y-6+lineHeight);
          g.setColor(tmp);
          GraphicLib.setNormalStroke(g);
          } else {
          //g.drawString("acc", x + width - 10, y+height-10);
          Color tmp = g.getColor();
          GraphicLib.setMediumStroke(g);
          g.setColor(Color.red);
          g.drawLine(x+width-2, y-2+lineHeight, x+width-8, y-8+lineHeight);
          g.drawLine(x+width-8, y-2+lineHeight, x+width-2, y-8+lineHeight);
          g.setColor(tmp);
          GraphicLib.setNormalStroke(g);
          }
          }*/

        g.setFont(myFont);
        String texti = "Text";
        String s ;
        int i;
        size = lineHeight + currentFontSize;

        //ID
        /*if (size < (height - 2)) {
          drawLimitedString(g, "ID=" + id, x + textX, y + size, width, 0);
          }
          size += currentFontSize;*/

        //text
        for(i=0; i<texts.length; i++) {
            if (size < (height - 2)) {
                s = texts[i];
                if (i == 0) {
                    s = texti + "=\"" + s;
                }
                if (i == (texts.length - 1)) {
                    s = s + "\"";
                }
                drawLimitedString(g, s, x + textX, y + size, width, 0);
            }
            size += currentFontSize;

        }
        if (size < (height - 2)) {
            drawLimitedString(g, "Durability=\"" + DURABILITY_TYPE[durability] + "\"", x + textX, y + size, width, 0);
            size += currentFontSize;
            if (size < (height - 2)) {
                drawLimitedString(g, "Source=\"" + SOURCE_TYPE[source] + "\"", x + textX, y + size, width, 0);
                size += currentFontSize;
                if (size < (height - 2)) {
                    drawLimitedString(g, "Status=\"" + STATUS_TYPE[status] + "\"", x + textX, y + size, width, 0);
                    size += currentFontSize;
                    if (size < (height - 2)) {
                        drawLimitedString(g, "Scope=\"" + LIMITATION_TYPE[limitation] + "\"", x + textX, y + size, width, 0);
                        size += currentFontSize;
                    }
                }
            }
        }

        // Type and risk
        /*if (size < (height - 2)) {
          drawLimitedString(g, "Kind=\"" + kind + "\"", x + textX, y + size, width, 0);
          size += currentFontSize;
          if (size < (height - 2)) {
          drawLimitedString(g, "Risk=\"" + criticality + "\"", x + textX, y + size, width, 0);
          size += currentFontSize;
          if (size < (height - 2)) {

          drawLimitedString(g, "Reference elements=\"" + referenceElements + "\"", x + textX, y + size, width, 0);

          size += currentFontSize;
          if (size < (height - 2)) {

          if (reqType == SECURITY_REQ) {
          drawLimitedString(g, "Targeted attacks=\"" + attackTreeNode + "\"", x + textX, y + size, width, 0);
          }

          if (reqType == SAFETY_REQ) {
          drawLimitedString(g, "Violated action=\"" + violatedAction + "\"", x + textX, y + size, width, 0);
          }
          }
          }
          }
          }*/


        g.setFont(f);
    }

    public boolean editOndoubleClick(JFrame frame, int _x, int _y) {

        JDialogAssumption jda = new JDialogAssumption(tdp.getGUI().getFrame(), "Setting attributes of Assumption " + getAssumptionName(), getAssumptionName(), text, type, durability, source, status, limitation);
     //   jda.setSize(750, 550);
        GraphicLib.centerOnParent(jda, 750, 550 );
        jda.setVisible(true);

        if (!jda.isRegularClose()) {
            return false;
        }

        String s = jda.getName();

        text = jda.getText();
        makeValue();

        type = jda.getAssumptionType();
        durability = jda.getDurability();
        source = jda.getSource();
        status = jda.getStatus();
        limitation = jda.getLimitation();


        if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
            //boolean b;
            if (!TAttribute.isAValidId(s, false, false)) {
                JOptionPane.showMessageDialog(frame,
                                              "Could not change the name of the Assumption: the new name is not a valid name",
                                              "Error",
                                              JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

            if (!tdp.isRequirementNameUnique(s)) {
                JOptionPane.showMessageDialog(frame,
                                              "Could not change the name of the Assumption: the new name is already in use",
                                              "Error",
                                              JOptionPane.INFORMATION_MESSAGE);
                return false;
            }


            int size = graphics.getFontMetrics().stringWidth(s) + iconSize + 5;
            minDesiredWidth = Math.max(size, minWidth);
            if (minDesiredWidth != width) {
                newSizeForSon(null);
            }
            setValue(s);
        }


        return true;

        // On the name ?


    }


    public void rescale(double scaleFactor){
        dlineHeight = (lineHeight + dlineHeight) / oldScaleFactor * scaleFactor;
        lineHeight = (int)(dlineHeight);
        dlineHeight = dlineHeight - lineHeight;

        minHeight = lineHeight;

        super.rescale(scaleFactor);
    }


    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }

    public  int getType() {
        return TGComponentManager.AVATARMAD_ASSUMPTION;
    }

    public String toString() {
        String ret =  getValue();

        ret += " " + text;

        return ret;
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");

        if (texts != null) {
            for(int i=0; i<texts.length; i++) {
                //value = value + texts[i] + "\n";
                sb.append("<textline data=\"");
                sb.append(GTURTLEModeling.transformString(texts[i]));
                sb.append("\" />\n");
            }
        }
        sb.append("<type data=\"");
        sb.append(type);
        sb.append("\" />\n");
        sb.append("<durability data=\"");
        sb.append(durability);
        sb.append("\" />\n");
        sb.append("<source data=\"");
        sb.append(source);
        sb.append("\" />\n");
        sb.append("<status data=\"");
        sb.append(status);
        sb.append("\" />\n");
        sb.append("<limitation data=\"");
        sb.append(limitation);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }


    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            String oldtext = text;
            text = "";
            String s;

            //System.out.println("Loading tclass " + getValue());
            //System.out.println(nl.toString());

            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("textline")) {
                                //System.out.println("Analyzing line0");
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    s = "";
                                }
                                text += GTURTLEModeling.decodeString(s) + "\n";

                            } else if (elt.getTagName().equals("type")) {
                                //System.out.println("Analyzing line2");
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    type = 0;
                                } else {
                                    try {
                                        type = Integer.decode(s).intValue();
                                    } catch (Exception e) {
                                        type = 0;
                                    }
                                }
                                if ((type > (ASSUMPTION_TYPE_STR.length-1)) || (type < 0)) {
                                    type = 0;
                                }

                            } else if (elt.getTagName().equals("durability")) {
                                //System.out.println("Analyzing line2");
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    durability = 0;
                                } else {
                                    try {
                                        durability = Integer.decode(s).intValue();
                                    } catch (Exception e) {
                                        durability = 0;
                                    }
                                }
                                if ((durability > (DURABILITY_TYPE.length-1)) || (durability < 0)) {
                                    type = 0;
                                }

                            } else if (elt.getTagName().equals("source")) {
                                //System.out.println("Analyzing line2");
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    source = 0;
                                } else {
                                    try {
                                        source = Integer.decode(s).intValue();
                                    } catch (Exception e) {
                                        source = 0;
                                    }
                                }
                                if ((source > (SOURCE_TYPE.length-1)) || (source < 0)) {
                                    source = 0;
                                }

                            } else if (elt.getTagName().equals("status")) {
                                //System.out.println("Analyzing line2");
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    status = 0;
                                } else {
                                    try {
                                        status = Integer.decode(s).intValue();
                                    } catch (Exception e) {
                                        status = 0;
                                    }
                                }
                                if ((status > (STATUS_TYPE.length-1)) || (status < 0)) {
                                    status = 0;
                                }

                            } else if (elt.getTagName().equals("limitation")) {
                                //System.out.println("Analyzing line2");
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    limitation = 0;
                                } else {
                                    try {
                                        limitation = Integer.decode(s).intValue();
                                    } catch (Exception e) {
                                        limitation = 0;
                                    }
                                }
                                if ((limitation > (LIMITATION_TYPE.length-1)) || (limitation < 0)) {
                                    limitation = 0;
                                }

                            }
                            //System.out.println("Analyzing line4");
                        }
                    }
                }
            }
            if (text.length() == 0) {
                text = oldtext;
            }
        } catch (Exception e) {
            TraceManager.addError("Failed when loading requirement extra parameters (AVATARMAD)");
            throw new MalformedModelingException();
        }

        makeValue();
    }


    public String getAssumptionName() {
        return value;
    }

    public String getText() {
        return text;
    }

    /*public String getID() {
      return id;
      }

      public String getKind() {
      return kind;
      }

      public String getViolatedAction() {
      return violatedAction;
      }

      public String getAttackTreeNode() {
      return attackTreeNode;
      }

      public String getReferenceElements() {
      return referenceElements;
      }

      public int getCriticality() {
      //System.out.println("Criticality=" + criticality);
      if (criticality.compareTo("High") == 0) {
      return AvatarRDRequirement.HIGH;
      } else if (criticality.compareTo("Medium") == 0) {
      return AvatarRDRequirement.MEDIUM;
      } else {
      return AvatarRDRequirement.LOW;
      }
      }*/

    public String getAttributes() {
        String attr = "";
        attr += "Text= " + text + "\n";

        return attr;
    }

    public void autoAdjust(int mode) {
        //System.out.println("Auto adjust in mode = " + mode);

        if (graphics == null) {
            return;
        }

        Font f = graphics.getFont();
        Font f0 = f.deriveFont((float)currentFontSize);
        Font f1 = f0.deriveFont(Font.BOLD);
        Font f2 = f.deriveFont((float)(currentFontSize - 2));

        /*// Must find for both modes which width is desirable
          String s0, s1;
          s0 = REQ_TYPE_STR[reqType];
          s1 = "Text=";

          graphics.setFont(f2);
          int w0 = graphics.getFontMetrics().stringWidth(s0);
          graphics.setFont(f1);
          int w1 = graphics.getFontMetrics().stringWidth(value);
          int w2 = Math.max(w0, w1) + (2 * iconSize);

          graphics.setFont(f0);
          int w3, w4 = w2;
          int i;

          if(texts.length == 1) {
          w3 = graphics.getFontMetrics().stringWidth(s1 + "=\"" + texts[0] + "\"");
          w4 = Math.max(w4, w3);
          } else {
          for(i=0; i<texts.length; i++) {
          if (i == 0) {
          w3 = graphics.getFontMetrics().stringWidth(s1 + "=\"" + texts[i]);
          } else if (i == (texts.length - 1)) {
          w3 = graphics.getFontMetrics().stringWidth(texts[i] + "\"");
          } else {
          w3 = graphics.getFontMetrics().stringWidth(texts[i]);
          }

          w4 = Math.max(w4, w3+2);
          }
          }
          w3 = graphics.getFontMetrics().stringWidth("Kind=\"" + kind + "\"") + 2;
          w4 = Math.max(w4, w3);
          w3 = graphics.getFontMetrics().stringWidth("Risk=\"" + criticality + "\"") + 2;
          w4 = Math.max(w4, w3);
          w3 = graphics.getFontMetrics().stringWidth("ID=\"" + id + "\"") + 2;
          w4 = Math.max(w4, w3);

          if (mode == 1) {
          resize(w4, lineHeight);
          return;
          }

          int h;
          if (mode == 2) {
          h = ((texts.length + 4) * currentFontSize) + lineHeight;
          } else {
          h = ((texts.length + 5) * currentFontSize) + lineHeight;
          }


          resize(w4, h);*/

    }

}