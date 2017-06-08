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
   * Class ATDCountermeasure
   * Creation: 06/06/2017
   * @version 1.0 06/06/2017
   * @author Ludovic APVRILLE
   * @see
   */

package ui.atd;

import myutil.GraphicLib;
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.window.JDialogCountermeasure;

import javax.swing.*;
import java.awt.*;

public class ATDCountermeasure extends TGCScalableWithInternalComponent implements SwallowedTGComponent, WithAttributes {
    private int textY1 = 3;
 //   private int textY2 = 3;

   // private static int arc = 7;
    //private int textX = 10;

    protected String oldValue = "";
    protected String description = "";
    private String stereotype = "countermeasure";

    private static int decPar = 20;
    
    private static int maxFontSize = 14;
    private static int minFontSize = 4;
    private int currentFontSize = -1;
    private boolean displayText = true;
    private int textX = 10;

    public ATDCountermeasure(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 125;
        height = (int)(40 * tdp.getZoom());
        minWidth = 100;

        nbConnectingPoint = 12;
        connectingPoint = new TGConnectingPoint[12];

        connectingPoint[0] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[1] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[2] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[3] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[4] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[5] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[6] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[7] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[8] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[9] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[10] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[11] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);
        //addTGConnectingPointsComment();

        moveable = true;
        editable = true;
        removable = true;

        value = "counter01";
        description = "blah blah blah";

        currentFontSize = -1;
        oldScaleFactor = tdp.getZoom();

        myImageIcon = IconManager.imgic702;
    }

    public void internalDrawing(Graphics g) {
        String ster= "<<" + stereotype + ">>";
        Font f = g.getFont();
        Font fold = f;

        if (value != oldValue) {
            setValue(value, g);
        }


        if (currentFontSize == -1) {
            currentFontSize = f.getSize();
        }

        if ((rescaled) && (!tdp.isScaled())) {
            rescaled = false;

            float scale = (float)(f.getSize()*tdp.getZoom());
            scale = Math.min(maxFontSize, scale);
            currentFontSize = (int)scale;
            if (scale < minFontSize) {
                displayText = false;
            } else {
                displayText = true;
                setValue(value, g);
            }
        }
        // Core of the countermeasure
        Color c = g.getColor();
	Polygon p = new Polygon();
        g.draw3DRect(x, y, width, height, true);
	g.setColor(ColorManager.ATD_COUNTERMEASURE);
	

        g.fill3DRect(x+1, y+1, width-1, height-1, true);
        g.setColor(c);

        // Strings
        int w;

        //TraceManager.addDev("display text of attack=" + displayText);

        if (displayText) {
            f = f.deriveFont((float)currentFontSize);
            g.setFont(f);
            //Font f0 = g.getFont();

            boolean cannotWriteAttack = (height < (2 * currentFontSize + (int)(textY1 * tdp.getZoom())));
            //TraceManager.addDev("Zoom=" + tdp.getZoom() + " Cannot write attack=" + cannotWriteAttack + "Font=" + f0);
            if (cannotWriteAttack) {
                w  = g.getFontMetrics().stringWidth(value);
                int h =  currentFontSize + (int)(textY1 * tdp.getZoom());
                if ((w < (2*textX + width)) && (h < height)) {
                    g.drawString(value, x + (width - w)/2, y + h);
                } else {
                    w  = g.getFontMetrics().stringWidth(ster);
                    if ((w < (2*textX + width)) && (h < height)) {
                        g.drawString(ster, x + (width - w)/2, y + h);
                    }
                }
            } else {
                g.setFont(f.deriveFont(Font.BOLD));
                int h =  currentFontSize + (int)(textY1 * tdp.getZoom());
                int cumulated = 0;
                w = g.getFontMetrics().stringWidth(ster);
                if ((w < (2*textX + width)) && (h < height)) {
                    g.drawString(ster, x + (width - w)/2, y + h);
                    cumulated = h;
                }
                g.setFont(f);
                w  = g.getFontMetrics().stringWidth(value);
                h = cumulated + (int)currentFontSize + (int)(textY1 * tdp.getZoom());
                if ((w < (2*textX + width)) && (h < height)) {
                    //TraceManager.addDev("Drawing value=" + value);
                    g.drawString(value, x + (width - w)/2, y + h);
                } else {
		    g.drawString(value, x + (width - w)/2, y + h);
                    //TraceManager.addDev("--------------------------------------------------- Cannot draw value=" + value);
                    //TraceManager.addDev("w=" + w + " val=" + (2*textX + width) + "h=" + h + " height=" + height + " zoom=" + tdp.getZoom() + " Font=" + f0);
                }
            }

	    
	    
        } else {
            TraceManager.addDev("-------------------------------------------------- Cannot display text of countermeasure");
        }

        g.setFont(fold);

    }

    public void setValue(String val, Graphics g) {
        oldValue = value;
        String ster;
	ster = "<<" + stereotype + ">>";


        Font f0 = g.getFont();

        if (currentFontSize != -1) {
            if (currentFontSize != f0.getSize()) {
                g.setFont(f0.deriveFont((float)currentFontSize));
            }
        }

        int w  = Math.max(g.getFontMetrics().stringWidth(value), g.getFontMetrics().stringWidth(ster));
        int w1 = Math.max((int)(minWidth*tdp.getZoom()), w + 2 * textX);

        //System.out.println("width=" + width + " w1=" + w1 + " w2=" + w2 + " value=" + value);
        if (w1 != width) {
            width = w1;
            resizeWithFather();
        }


        g.setFont(f0);

        //System.out.println("width=" + width + " w1=" + w1 + " value=" + value);
    }

    public void resizeWithFather() {
        if ((father != null) && (father instanceof ATDBlock)) {
            //System.out.println("cdRect comp");
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            //setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y, father.getHeight() - getHeight()));
            setMoveCd(x, y);
        }
    }


    public boolean editOndoubleClick(JFrame frame) {
        String tmp;
        boolean error = false;

        JDialogCountermeasure dialog = new JDialogCountermeasure(frame, "Setting countermeasure attributes", this);
   //     dialog.setSize(450, 350);
        GraphicLib.centerOnParent(dialog, 450, 350);
        dialog.setVisible( true ); // blocked until dialog has been closed

        if (!dialog.isRegularClose()) {
            return false;
        }

        if (dialog.getName() == null) {
            return false;
        }

        if (dialog.getName().length() > 0) {
            tmp = dialog.getName();
            if (!TAttribute.isAValidId(tmp, false, false)) {
                error = true;
            } else {
                value = tmp;
            }
        }


        if (dialog.getDescription() != null) {
            description = dialog.getDescription();
        }

        if (error) {
            JOptionPane.showMessageDialog(frame,
                                          "Name is non-valid",
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
        }

        return !error;
    }

    public TGComponent isOnOnlyMe(int x1, int y1) {

        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }

    public int getType() {
        return TGComponentManager.ATD_COUNTERMEASURE;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String _description) {
        description = _description;
    }

    public String getCountermeasureName() {
        return value;
    }

    public String getAttributes() {
        String s = "Description = " + description + "\n";
        s += "Id=" + getId();
        return s;
    }

}
