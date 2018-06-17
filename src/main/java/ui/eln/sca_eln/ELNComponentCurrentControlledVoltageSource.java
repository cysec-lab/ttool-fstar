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

package ui.eln.sca_eln;

import myutil.GraphicLib;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.eln.ELNConnectingPoint;
import ui.util.IconManager;
import ui.window.JDialogELNComponentCurrentControlledVoltageSource;

import javax.swing.*;
import java.awt.*;

/**
 * Class ELNComponentCurrentControlledVoltageSource
 * Current controlled voltage source to be used in ELN diagrams
 * Creation: 13/06/2018
 * @version 1.0 13/06/2018
 * @author Irina Kit Yan LEE
 */

public class ELNComponentCurrentControlledVoltageSource extends TGCScalableWithInternalComponent {
    protected Color myColor;
    protected int orientation;
	private int maxFontSize = 14;
    private int minFontSize = 4;
    private int currentFontSize = -1;
//    protected int oldx, oldy;
//    protected int currentOrientation = GraphicLib.NORTH;

    private int textX = 15; // border for ports
    private double dtextX = 0.0;
    protected int decPoint = 3;

    private int fact = 2;
    
	// Parameter
	private double val;
	private String unit;
    
    public ELNComponentCurrentControlledVoltageSource(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        initScaling(60*fact, 40*fact);

        dtextX = textX * oldScaleFactor;
        textX = (int)dtextX;
        dtextX = dtextX - textX;
        
        minWidth = 1;
        minHeight = 1;

        initConnectingPoint(4);
                
        addTGConnectingPointsComment();

        moveable = true;
        editable = true;
        removable = true;
        userResizable = false;
        value = tdp.findELNComponentName("CCVS");
        
        myImageIcon = IconManager.imgic1206;
        
        // Initialization of cvvs attributes
        setVal(1.0);
        setUnit("\u03A9");
    }

    public void initConnectingPoint(int nb) {
        nbConnectingPoint = nb;
        connectingPoint = new TGConnectingPoint[nb];
        connectingPoint[0] = new ELNConnectingPoint(this, 0, 0, true, true, 0.0, 0.0);
        connectingPoint[1] = new ELNConnectingPoint(this, 0, 0, true, true, 1.0, 0.0);
        connectingPoint[2] = new ELNConnectingPoint(this, 0, 0, true, true, 0.0, 1.0);
        connectingPoint[3] = new ELNConnectingPoint(this, 0, 0, true, true, 1.0, 1.0);
    }

    public Color getMyColor() {
        return myColor;
    }

    public void internalDrawing(Graphics g) {
        Font f = g.getFont();
        Font fold = f;
        
//    	if ((x != oldx) | (oldy != y)) {
//            // Component has moved!
//            manageMove();
//            oldx = x;
//            oldy = y;
//        }

    	if (this.rescaled && !this.tdp.isScaled()) {
            this.rescaled = false;
            // Must set the font size...
            // Incrementally find the biggest font not greater than max_font size
            // If font is less than min_font, no text is displayed

            int maxCurrentFontSize = Math.max(0, Math.min(this.height, (int) (this.maxFontSize * this.tdp.getZoom())));
            f = f.deriveFont((float) maxCurrentFontSize);

            while (maxCurrentFontSize > (this.minFontSize * this.tdp.getZoom() - 1)) {
            	if (g.getFontMetrics().stringWidth(value) < (width - (2 * textX))) {
            		break;
            	}
                maxCurrentFontSize--;
                f = f.deriveFont((float) maxCurrentFontSize);
            }

            if (this.currentFontSize < this.minFontSize * this.tdp.getZoom()) {
                maxCurrentFontSize++;
                f = f.deriveFont((float) maxCurrentFontSize);
            }
            g.setFont(f);
            this.currentFontSize = maxCurrentFontSize;
        } else {
            f = f.deriveFont(this.currentFontSize);
    	}

        // Zoom is assumed to be computed
    	Color c = g.getColor();
    	int [] ptx0 = {x, x+2*width/6, x+2*width/6, x, x+2*width/6, x+2*width/6};
        int [] pty0 = {y, y, y+height, y+height, y+height, y};
        g.drawPolygon(ptx0, pty0, 6);
        int [] ptx1 = {x+2*width/6-width/(12*fact), x+2*width/6+width/(12*fact), x+2*width/6};
        int [] pty1 = {y+height/2-height/(8*fact), y+height/2-height/(8*fact), y+height/2+height/(8*fact)};
        g.drawPolygon(ptx1, pty1, 3);
        g.fillPolygon(ptx1, pty1, 3);
        int [] ptx2 = {x+width, x+4*width/6, x+4*width/6, x+3*width/6, x+4*width/6, x+4*width/6, x+5*width/6, x+4*width/6, x+4*width/6, x+width, x+4*width/6, x+4*width/6};
        int [] pty2 = {y, y, y+height/4, y+2*height/4, y+3*height/4, y+height/4, y+2*height/4, y+3*height/4, y+height, y+height, y+height, y};
        g.drawPolygon(ptx2, pty2, 12);
        int [] ptx5 = {x+4*width/6+width/(12*fact), x+4*width/6+width/(6*fact), x+4*width/6+width/(6*fact), x+4*width/6+width/(6*fact), x+4*width/6+width/(6*fact), x+4*width/6+width/(6*fact)+width/(12*fact)};
        int [] pty5 = {y+height/(4*fact), y+height/(4*fact), y+height/(8*fact), y+height/(4*fact)+height/(8*fact), y+height/(4*fact), y+height/(4*fact)};
        g.drawPolygon(ptx5, pty5, 6);
        int [] ptx6 = {x+4*width/6+width/(12*fact), x+4*width/6+width/(6*fact)+width/(12*fact)};
        int [] pty6 = {y+height-height/(4*fact), y+height-height/(4*fact)};
        g.drawPolygon(ptx6, pty6, 2);
        g.setColor(c);
      
    	// Set font size
        int attributeFontSize = this.currentFontSize * 5 / 6;
        int w = g.getFontMetrics().stringWidth(value);
        g.setFont(f.deriveFont((float) attributeFontSize));
        g.setFont(f);
    	g.setFont(f.deriveFont(Font.BOLD));
    	g.drawString(value, x + (width - w)/2, y-height/(4*fact));

        g.setFont(fold);
    }

//    public void manageMove() {
//        if (father != null) {
//            Point p = GraphicLib.putPointOnRectangle(x+(width/2), y+(height/2), father.getX(), father.getY(), father.getWidth(), father.getHeight());
//
//            x = p.x - width/2;
//            y = p.y - height/2;
//
//            setMoveCd(x, y);
//
//            int orientation = GraphicLib.getCloserOrientation(x+(width/2), y+(height/2), father.getX(), father.getY(), father.getWidth(), father.getHeight());
//            if (orientation != currentOrientation) {
////                setOrientation(orientation);
//            }
//        }
//    }

    // TGConnecting points ..
    // TODO : change the orientation of the component
//    public void setOrientation(int orientation) {
//        currentOrientation = orientation;
//        double w0, h0, w1, h1;
//
////        switch(orientation) {
//////        case GraphicLib.NORTH:
//////            w0 = 0.5;
//////            h0 = 0.0;
//////            break;
////        case GraphicLib.WEST:
////            w0 = 0.0;
////            h0 = 0.5;
////            break;
//////        case GraphicLib.SOUTH:
//////            w0 = 0.5;
//////            h0 = 1.0;
//////            break;
////        case GraphicLib.EAST:
////        default:
////            w0 = 1.0;
////            h0 = 0.5;
////        }
//
//        w0 = 0.0;
//        h0 = 0.5;
//        w1 = 1.0;
//        h1 = 0.5;
//        System.out.println(connectingPoint.length);
//		((ELNConnectingPoint) connectingPoint[0]).setW(w0);
//		((ELNConnectingPoint) connectingPoint[0]).setH(h0);
//		((ELNConnectingPoint) connectingPoint[1]).setW(w1);
//		((ELNConnectingPoint) connectingPoint[1]).setH(h1);
//    }

    public TGComponent isOnOnlyMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public int getType() {
    	return TGComponentManager.ELN_CURRENT_CONTROLLED_VOLTAGE_SOURCE;
    }

    public boolean editOndoubleClick(JFrame frame) {
    	JDialogELNComponentCurrentControlledVoltageSource jde = new JDialogELNComponentCurrentControlledVoltageSource(this);
    	jde.setVisible(true);
        return true;
    }
    
    public StringBuffer encode(String data) {
    	StringBuffer databuf = new StringBuffer(data);
    	StringBuffer buffer = new StringBuffer("");
        for(int pos = 0; pos != data.length(); pos++) {
        	char c = databuf.charAt(pos);
            switch(c) {
                case '\u03A9' :  
                	buffer.append("&#x3A9;");       
                	break;
                case '\u03BC' : 
                	buffer.append("&#x3BC;");      
                	break;
                default :   
                	buffer.append(databuf.charAt(pos)); 
                	break;
            }
        }
        return buffer;
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<attributes value=\"" + val);
        sb.append("\" unit=\"" + encode(unit) + "\"");
        sb.append("/>\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

	public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            
            double value;
            String unit;

            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("attributes")) {
                            	value = Double.parseDouble(elt.getAttribute("value"));
                            	unit = elt.getAttribute("unit");
								setVal(value);
								setUnit(unit);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

    public int getDefaultConnector() {
        return TGComponentManager.ELN_CONNECTOR;
    }
    
    public double getVal() {
		return val;
	}

	public void setVal(double _val) {
		val = _val;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String _unit) {
		unit = _unit;
	}
}