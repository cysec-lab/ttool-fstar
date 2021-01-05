/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */


package ui.avatarcd;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogAttribute;
import ui.window.JDialogGeneralAttribute;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Class AvatarCDBlock
 * Node. To be used in AVATAR Context Diagrams
 * Creation: 31/08/2011
 *
 * @author Ludovic APVRILLE
 * @version 1.2 03/07/2019
 */
public class AvatarCDBlock extends TGCScalableWithInternalComponent implements SwallowTGComponent, SwallowedTGComponent {
//    private int textY1 = 3;
//    private int textX = 7;
    private String stereotype = "block";

//    private int maxFontSize = 12;
//    private int minFontSize = 4;
//    private int currentFontSize = -1;
//    private boolean displayText = true;
    

//    private int limitName = -1;
//    private int limitAttr = -1;
//    private int limitMethod = -1;

    protected List<GeneralAttribute> myAttributes;

    protected Map<GeneralAttribute, Integer> attrLocMap = new HashMap<GeneralAttribute, Integer>();

    // Icon
    //private int iconSize = 15;
    //private boolean iconIsDrawn = false;


    public String oldValue;

    public AvatarCDBlock(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        textY = 3;
        textX = 7;
        width = 250;
        height = 200;
        minWidth = 5;
        minHeight = 2;
        initScaling(250,200);

        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];

        connectingPoint[0] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 0.0, 0.0);
        connectingPoint[1] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[2] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 1.0, 0.0);
        connectingPoint[3] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[4] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[5] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 0.0, 1.0);
        connectingPoint[6] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[7] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 1.0, 1.0);

        connectingPoint[8] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[9] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[10] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[11] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[12] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[13] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[14] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[15] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);

        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        //multieditable = true;
        removable = true;
        userResizable = true;

        name = tdp.findAvatarCDBlockName("Block");
        setValue(name);
        oldValue = value;

//        currentFontSize = maxFontSize;
        oldScaleFactor = tdp.getZoom();

        myImageIcon = IconManager.imgic700;

        this.myAttributes = new LinkedList<GeneralAttribute>();

        actionOnAdd();
    }
    
    /**
     * Internal Drawing function of AvatarCDBlock
     * draws the rectangle, fills it with color and writes the texts where it needs to be
     * @param g
     */
    public void internalDrawing(Graphics g)
    {
    	//Rectangle
        Color c = g.getColor();
        g.draw3DRect(x, y, width, height, true);

        Color avat = ColorManager.AVATAR_BLOCK;
        Font f = g.getFont();
        int currentHeight = f.getSize() * 2;
        g.setColor(new Color(avat.getRed(), avat.getGreen(), Math.min(255, avat.getBlue() + (getMyDepth() * 10))));
        g.fill3DRect(x + 1, y + 1, width - 1, Math.min(currentHeight, height), true);
        g.setColor(c);
        
        //Strings
        String ster = "<<" + stereotype + ">>";
        g.setFont(f.deriveFont(Font.BOLD));
        currentHeight = f.getSize();
        drawSingleString(g, ster, getCenter(g, ster), y + currentHeight);
        
        
        g.setFont(f);
//        strWidth = g.getFontMetrics().stringWidth(value);
        currentHeight = 2 * f.getSize();
        drawSingleString(g, value, getCenter(g, value), y + currentHeight);
        
     
        if (currentHeight < height) {
            //g.drawLine(x, y+h, x+width, y+h);
            g.setColor(new Color(avat.getRed(), avat.getGreen(), Math.min(255, avat.getBlue() + (getMyDepth() * 10))));
            g.fill3DRect(x + 1, y + currentHeight+1, width - 1, height - 1 - currentHeight, true);
            g.setColor(c);
        }
    }
    
////    @Override
//    public void internalDrawin(Graphics g) {
//        String ster = "<<" + stereotype + ">>";
//        Font f = g.getFont();
//        Font fold = f;
//
//        //
//
//        if ((rescaled) && (!tdp.isScaled())) {
//
//            if (currentFontSize == -1) {
//                currentFontSize = f.getSize();
//            }
//            rescaled = false;
//            // Must set the font size ..
//            // Find the biggest font not greater than max_font size
//            // By Increment of 1
//            // Or decrement of 1
//            // If font is less than 4, no text is displayed
//
//            int maxCurrentFontSize = Math.max(0, Math.min(height, maxFontSize));
//            int w0, w1, w2;
//            f = f.deriveFont((float) maxCurrentFontSize);
//            g.setFont(f);
//            //
//            while (maxCurrentFontSize > (minFontSize - 1)) {
//                w0 = g.getFontMetrics().stringWidth(value);
//                w1 = g.getFontMetrics().stringWidth(ster);
//                w2 = Math.min(w0, w1);
//                if (w2 < (width - (2 * textX))) {
//                    break;
//                }
//                maxCurrentFontSize--;
//                f = f.deriveFont((float) maxCurrentFontSize);
//                g.setFont(f);
//            }
//            currentFontSize = maxCurrentFontSize;
//
//            if (currentFontSize < minFontSize) {
//                displayText = false;
//            } else {
//                displayText = true;
//                f = f.deriveFont((float) currentFontSize);
//                g.setFont(f);
//            }
//
//        }
//
//        //
//
//        Color c = g.getColor();
//
//        g.draw3DRect(x, y, width, height, true);
//
//        //g.setColor(ColorManager.AVATAR_BLOCK);
//        Color avat = ColorManager.AVATAR_BLOCK;
//        int h;
//        h = 2 * (currentFontSize + (int) (textY * tdp.getZoom())) + 2;
//        g.setColor(new Color(avat.getRed(), avat.getGreen(), Math.min(255, avat.getBlue() + (getMyDepth() * 10))));
//        g.fill3DRect(x + 1, y + 1, width - 1, Math.min(h, height) - 1, true);
//        g.setColor(c);
//
//        // Strings
//        int w;
//        h = 0;
//        if (displayText) {
//            f = f.deriveFont((float) currentFontSize);
//            Font f0 = g.getFont();
//            g.setFont(f.deriveFont(Font.BOLD));
//
//            w = g.getFontMetrics().stringWidth(ster);
//            h = currentFontSize + (int) (textY * tdp.getZoom());
//            if ((w < (2 * textX + width)) && (h < height)) {
//                drawSingleString(g, ster, x + (width - w) / 2, y + h);
//            }
//            g.setFont(f0);
//            w = g.getFontMetrics().stringWidth(value);
//            h = 2 * (currentFontSize + (int) (textY * tdp.getZoom()));
//            if ((w < (2 * textX + width)) && (h < height)) {
//                drawSingleString(g, value, x + (width - w) / 2, y + h);
//            }
////            limitName = y + h;
//        } else {
////            limitName = -1;
//        }
//
//        g.setFont(fold);
//
//        h = h + 2;
//        if (h < height) {
//            //g.drawLine(x, y+h, x+width, y+h);
//            g.setColor(new Color(avat.getRed(), avat.getGreen(), Math.min(255, avat.getBlue() + (getMyDepth() * 10))));
//            g.fill3DRect(x + 1, y + h, width - 1, height - 1 - h, true);
//            g.setColor(c);
//        }
//
//        // Icon
//        /*if ((width>30) && (height > (iconSize + 2*textX))) {
//			iconIsDrawn = true;
//			g.drawImage(IconManager.img5100, x + width - iconSize - textX, y + textX, null);
//		} else {
//			iconIsDrawn = false;
//		}*/
//
//        g.setFont(fold);
//
//
//        // Icon
//        //g.drawImage(IconManager.imgic1100.getImage(), x + 4, y + 4, null);
//        //g.drawImage(IconManager.img9, x + width - 20, y + 4, null);
//    }

    
    @Override
    public TGComponent isOnOnlyMe(int x1, int y1) {

        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }

    public String getStereotype() {
        return stereotype;

    }

    public String getNodeName() {
        return name;
    }
    /*
     *     public boolean editOndoubleClick(JFrame frame) {

        JDialogGeneralAttribute jda = new JDialogGeneralAttribute(myAttributes, null, frame,
                "Setting attributes of " + value, "Attribute", stereotype + "/" + value);
        //setJDialogOptions(jda);
        // jda.setSize(650, 375);
        GraphicLib.centerOnParent(jda, 750, 375);
        jda.setVisible(true); // blocked until dialog has been closed

        rescaled = true;

        String oldValue = getStereotype() + "/" + getValue();

        //String text = getName() + ": ";
        String s = jda.getValue();
		
        if (s == null) {
            return false;
        }
     * */
    @Override
    public boolean editOndoubleClick(JFrame frame) {

        oldValue = getStereotype() + "/" + getValue();

        //String text = getName() + ": ";
        String s = (String) JOptionPane.showInputDialog(frame, "Stereotype / identifier",
                "Setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
                null,
                getStereotype() + "/" + getValue());

        if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
            //boolean b;
            int index = s.indexOf("/");
            if (index == -1) {
                JOptionPane.showMessageDialog(frame,
                        "Could not change the name of the stereotype / block: no \"/\" in the name",
                        "Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

            String ster = s.substring(0, index);
            String blo = s.substring(index+1, s.length());
            if (ster.length() == 0 ) {
                JOptionPane.showMessageDialog(frame,
                        "Invalid stereotype",
                        "Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            if (blo.length() == 0 ) {
                JOptionPane.showMessageDialog(frame,
                        "Invalid stereotype",
                        "Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

            if (!TAttribute.isAValidId(ster, false, false, false)) {
                JOptionPane.showMessageDialog(frame,
                        "Could not change the name of the stereotype: the new name is not a valid name",
                        "Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

            if (!TAttribute.isAValidId(blo, false, false, false)) {
                JOptionPane.showMessageDialog(frame,
                        "Could not change the name of the Block: the new name is not a valid name",
                        "Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

            if (!tdp.isBlockNameUnique(blo)) {
                JOptionPane.showMessageDialog(frame,
                        "Could not change the name of the Block: the new name is already in use",
                        "Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

            stereotype = ster;
            setValue(blo);

            recalculateSize();

            if (tdp.actionOnDoubleClick(this)) {
                return true;
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Could not change the name of the Block: this name is already in use",
                        "Error",
                        JOptionPane.INFORMATION_MESSAGE);
                setValue(oldValue);
            }
        }
        return false;

    }
    
    @Override
    public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        return tgc instanceof AvatarCDBlock;

    }
    
    @Override
    public int getType() {
        return TGComponentManager.ACD_BLOCK;
    }
    
    @Override
    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        boolean swallowed = false;

        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof SwallowTGComponent) {
                if (tgcomponent[i].isOnMe(x, y) != null) {
                    swallowed = true;
                    ((SwallowTGComponent) tgcomponent[i]).addSwallowedTGComponent(tgc, x, y);
                    break;
                }
            }
        }

        if (swallowed) {
            return true;
        }

        if (!acceptSwallowedTGComponent(tgc)) {
            return false;
        }

        //
        // Choose its position

        // Make it an internal component
        // It's one of my son
        tgc.setFather(this);
        tgc.setDrawingZone(true);

        //Set its coordinates
        if (tgc instanceof AvatarCDBlock) {
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //
            tgc.resizeWithFather();
            //tgc.setCdRectangle(0, width - tgc.getWidth(), 0, height - tgc.getHeight());
            //tgc.setCd(x, y);
        }

        // else unknown*/

        //add it
        addInternalComponent(tgc, 0);

        return true;
    }
    
    @Override
    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeMyInternalComponent(tgc, false);
    }

    public boolean removeMyInternalComponent(TGComponent tgc, boolean actionOnRemove) {
        //TGComponent tgc;
        //TraceManager.addDev("Remove my internal component: " + tgc + ". I have " + nbInternalTGComponent + " internal components");

        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] == tgc) {
                nbInternalTGComponent = nbInternalTGComponent - 1;
                if (nbInternalTGComponent == 0) {
                    tgcomponent = null;
                } else {
                    TGComponent[] tgcomponentbis = new TGComponent[nbInternalTGComponent];
                    for (int j = 0; j < nbInternalTGComponent; j++) {
                        if (j < i) {
                            tgcomponentbis[j] = tgcomponent[j];
                        }
                        if (j >= i) {
                            tgcomponentbis[j] = tgcomponent[j + 1];
                        }
                    }
                    tgcomponent = tgcomponentbis;
                }
                if (actionOnRemove) {
                    tgc.actionOnRemove();
                    tdp.actionOnRemove(tgc);
                }
                return true;
            } else {
                if (tgcomponent[i] instanceof AvatarCDBlock) {
                    if (((AvatarCDBlock) tgcomponent[i]).removeMyInternalComponent(tgc, false)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getBlockName() {
        return value;
    }

    
    @Override
    public void hasBeenResized() {
        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarCDBlock) {
                tgcomponent[i].resizeWithFather();
            }
        }

        if (getFather() != null) {
            resizeWithFather();
        }

    }
    
    @Override
    public void resizeWithFather() {
        if ((father != null) && (father instanceof AvatarCDBlock)) {
            // Too large to fit in the father? -> resize it!
            resizeToFatherSize();

            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            setMoveCd(x, y);
        }
    }

    public LinkedList<AvatarCDBlock> getBlockList() {
        LinkedList<AvatarCDBlock> list = new LinkedList<AvatarCDBlock>();
        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarCDBlock) {
                list.add((AvatarCDBlock) (tgcomponent[i]));
            }
        }
        return list;
    }

    public LinkedList<AvatarCDBlock> getFullBlockList() {
        LinkedList<AvatarCDBlock> list = new LinkedList<AvatarCDBlock>();
        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarCDBlock) {
                list.add((AvatarCDBlock) (tgcomponent[i]));
                list.addAll(((AvatarCDBlock) tgcomponent[i]).getFullBlockList());
            }
        }
        return list;
    }


    public boolean hasInternalBlockWithName(String name) {
        LinkedList<AvatarCDBlock> list = getFullBlockList();
        for (AvatarCDBlock b : list) {
            if (b.getValue().compareTo(name) == 0) {
                return true;
            }
        }
        return false;
    }

    
    @Override
    public int getDefaultConnector() {
        return TGComponentManager.ACD_COMPOSITION_CONNECTOR;
    }
    
    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<stereotype value=\"" + GTURTLEModeling.transformString(getStereotype()));
        sb.append("\" />\n");
        for (GeneralAttribute a : this.myAttributes) {
            sb.append("<Attribute id=\"");
            sb.append(a.getId());
            sb.append("\" value=\"");
            sb.append(a.getInitialValue());
            sb.append("\" type=\"");
            sb.append(a.getType());
            sb.append("\" />\n");
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        //
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
//            int t1id;
//            String sdescription = null;
//            String prio;
//            String isRoot = null;
            String type, id, valueAtt;

            for (int i = 0; i < nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for (int j = 0; j < nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("stereotype")) {
                                stereotype = elt.getAttribute("value");
                            }
							//stay or no ?? FIXME
                            if (elt.getTagName().equals("Attribute")) {
                                //
                                type = elt.getAttribute("type");
                                id = elt.getAttribute("id");
                                valueAtt = elt.getAttribute("value");

                                if (valueAtt.equals("null")) {
                                    valueAtt = "";
                                }

                                GeneralAttribute ga = new GeneralAttribute(id, valueAtt, type);
                                this.myAttributes.add(ga);

                            }//FIXME end 
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }


    // Main Tree
    public int getChildCount() {
        //TraceManager.addDev("Counting childs!");
        return this.myAttributes.size()  + nbInternalTGComponent;
    }

    public Object getChild(int index) {

        int sa = nbInternalTGComponent;

        if (sa > index) {
            return tgcomponent[index];
        }

        index = index - nbInternalTGComponent;

        return this.myAttributes.get(index);
    }

    public int getIndexOfChild(Object child) {
        if (child instanceof AvatarCDBlock) {
            for (int i = 0; i < nbInternalTGComponent; i++) {
                if (tgcomponent[i] == child) {
                    return i;
                }
            }
        }

        if (child instanceof TAttribute) {
            return this.myAttributes.indexOf(child) + nbInternalTGComponent;
        }


        return -1;
    }

}
