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

package ui.ad;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Class TADNonDeterministicDelay Latency operator. To be used in activity
 * diagrams Creation: 10/12/2003
 * 
 * @version 1.0 10/12/2003
 * @author Ludovic APVRILLE
 */
public class TADNonDeterministicDelay extends TADComponentWithSubcomponents /* Issue #69 TGCWithInternalComponent */ {
    private int lineLength = 5;
    private int textX = 5;
    private int textY = 5;
    private int incrementY = 4;
    private int segment = 4;

    public TADNonDeterministicDelay(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
            TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 8;
        height = 32;

        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointAD(this, 0, -lineLength, true, false, 0.0, 0.0);
        connectingPoint[1] = new TGConnectingPointAD(this, 0, lineLength, false, true, 0.0, 1.0);
        addTGConnectingPointsComment();

        nbInternalTGComponent = 1;
        tgcomponent = new TGComponent[nbInternalTGComponent];

        TGCOneLineText tgc = new TGCOneLineText(x + textX + width, y + textY + height / 2, width, width + 10,
                (height / 2) + textY - 10, (height / 2) + textY + 10, true, this, _tdp);
        tgc.setValue("latency value");
        tgc.setName("value of the latency");
        tgcomponent[0] = tgc;

        moveable = true;
        editable = false;
        removable = true;

        name = "non deterministic delay";

        myImageIcon = IconManager.imgic216;
    }

    @Override
    public void internalDrawing(Graphics g) {
        int y1 = y;

        g.drawLine(x, y, x, y - lineLength);

        for (int i = 0; i < segment; i++) {
            g.drawLine(x, y1, x + width, y1 + incrementY);
            y1 += incrementY;
            g.drawLine(x + width, y1, x, y1 + incrementY);
            y1 += incrementY;
        }

        g.drawLine(x, y + height, x, y + height + lineLength);

    }

    @Override
    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }

        if ((int) (Line2D.ptSegDistSq(x, y - lineLength, x, y + lineLength + height, x1, y1)) < distanceSelected) {
            return this;
        }

        return null;
    }

    public String getLatencyValue() {
        return tgcomponent[0].getValue();
    }

    public void setLatencyValue(String value) {
        tgcomponent[0].setValue(value);
    }

    @Override
    public int getType() {
        return TGComponentManager.TAD_NON_DETERMINISTIC_DELAY;
    }

    @Override
    public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_AD_DIAGRAM;
    }
}
