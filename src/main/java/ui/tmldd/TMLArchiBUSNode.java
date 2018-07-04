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




package ui.tmldd;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tmltranslator.HwBus;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogBUSNode;
import ui.atd.ATDAttack;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
   * Class TMLArchiBUSNode
   * Node. To be used in TML architecture diagrams.
   * Creation: 31/10/2007
   * @version 1.0 31/10/2007
   * @author Ludovic APVRILLE
 */
public class TMLArchiBUSNode extends TMLArchiCommunicationNode implements SwallowTGComponent, LinkedReference, WithAttributes, TMLArchiElementInterface {
    private int textY1 = 15;
    private int textY2 = 30;
    private int derivationx = 2;
    private int derivationy = 3;
    private static String stereotype = "Bus";

    private int byteDataSize = HwBus.DEFAULT_BYTE_DATA_SIZE;
    private int pipelineSize = HwBus.DEFAULT_PIPELINE_SIZE;
    private int arbitrationPolicy = HwBus.DEFAULT_ARBITRATION;
    private int sliceTime = HwBus.DEFAULT_SLICE_TIME;

    private int privacy = HwBus.BUS_PUBLIC;
	public String referenceAttack;
    
    public TMLArchiBUSNode(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 250;
        height = 50;
        minWidth = 100;
        minHeight = 50;

        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];

        connectingPoint[0] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.0, 0.0);
        connectingPoint[1] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.5, 0.0);
        connectingPoint[2] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 1.0, 0.0);
        connectingPoint[3] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.0, 0.5);
        connectingPoint[4] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 1.0, 0.5);
        connectingPoint[5] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.0, 1.0);
        connectingPoint[6] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.5, 1.0);
        connectingPoint[7] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 1.0, 1.0);

        connectingPoint[8] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.25, 0.0);
        connectingPoint[9] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.75, 0.0);
        connectingPoint[10] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.0, 0.25);
        connectingPoint[11] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 1.0, 0.25);
        connectingPoint[12] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.0, 0.75);
        connectingPoint[13] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 1.0, 0.75);
        connectingPoint[14] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.25, 1.0);
        connectingPoint[15] = new TMLArchiConnectingPoint(this, 0, 0, true, false, 0.75, 1.0);

        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;

        name = tdp.findNodeName("Bus");
        value = "name";

        myImageIcon = IconManager.imgic700;
    }

    public void internalDrawing(Graphics g) {
        Color c = g.getColor();
        g.draw3DRect(x, y, width, height, true);


        // Top lines
        g.drawLine(x, y, x + derivationx, y - derivationy);
        g.drawLine(x + width, y, x + width + derivationx, y - derivationy);
        g.drawLine(x + derivationx, y - derivationy, x + width + derivationx, y - derivationy);

        // Right lines
        g.drawLine(x + width, y + height, x + width + derivationx, y - derivationy + height);
        g.drawLine(x + derivationx + width, y - derivationy, x + width + derivationx, y - derivationy + height);

        // Filling color
        g.setColor(ColorManager.BUS_BOX);
        g.fill3DRect(x+1, y+1, width-1, height-1, true);
        g.setColor(c);

        // Strings
        String ster = "<<" + stereotype + ">>";
        int w  = g.getFontMetrics().stringWidth(ster);
        Font f = g.getFont();
        g.setFont(f.deriveFont(Font.BOLD));
        g.drawString(ster, x + (width - w)/2, y + textY1);
        g.setFont(f);
        w  = g.getFontMetrics().stringWidth(name);
        g.drawString(name, x + (width - w)/2, y + textY2);

        // Icon
        //g.drawImage(IconManager.imgic1102.getImage(), x + width - 20, y + 4, null);
        g.drawImage(IconManager.imgic1102.getImage(), x + 4, y + 4, null);
        //g.drawImage(IconManager.img9, x + width - 20, y + 4, null);

        c = g.getColor();

        //Draw bus privacy
        if (privacy== HwBus.BUS_PUBLIC){

        }
        else {
            int[] xps = new int[]{x+4, x+7, x+10, x+13, x+16, x+19, x+22, x+22, x+13, x+4};
            int[] yps = new int[]{y+18, y+22, y+22, y+18, y+22, y+22,y+18, y+35, y+43, y+35};
            g.setColor(Color.green);
            g.fillPolygon(xps, yps,10);

            // g.drawOval(x+6, y+19, 12, 18);

            //    g.fillRect(x+4, y+25, 18, 14);
            g.setColor(c);
            g.drawPolygon(xps, yps,10);
            //  g.drawRect(x+4, y+25, 18, 14);
        }
    }

    public TGComponent isOnOnlyMe(int x1, int y1) {

        Polygon pol = new Polygon();
        pol.addPoint(x, y);
        pol.addPoint(x + derivationx, y - derivationy);
        pol.addPoint(x + derivationx + width, y - derivationy);
        pol.addPoint(x + derivationx + width, y + height - derivationy);
        pol.addPoint(x + width, y + height);
        pol.addPoint(x, y + height);
        if (pol.contains(x1, y1)) {
            return this;
        }

        return null;
    }

    public String getStereotype() {
        return stereotype;

    }

	public String getRefAttack(){
		return referenceAttack;
	}

    public String getNodeName() {
        return name;
    }

    public boolean editOndoubleClick(JFrame frame) {
        boolean error = false;
        String errors = "";
        int tmp;
        String tmpName;
		Vector<String> attacks = new Vector<String>();
		for (TGComponent attack: tdp.getMGUI().getAllAttacks()){
			if (attack instanceof ATDAttack){	
				attacks.add(((ATDAttack) attack).getValue().trim());
			}
		}

        JDialogBUSNode dialog = new JDialogBUSNode(frame, "Setting Bus Attributes", this, attacks);
       // dialog.setSize(500, 450);
        GraphicLib.centerOnParent(dialog, 500, 450);
        dialog.setVisible( true ); // blocked until dialog has been closed

        if (!dialog.isRegularClose()) {
            return false;
        }

        if (dialog.getNodeName().length() != 0) {
            tmpName = dialog.getNodeName();
            tmpName = tmpName.trim();
            if (!TAttribute.isAValidId(tmpName, false, false)) {
                error = true;
                errors += "Name of the node  ";
            } else {
                name = tmpName;
            }
        }

        arbitrationPolicy = dialog.getArbitrationPolicy();
        privacy = dialog.getPrivacy();
		referenceAttack = dialog.getReferenceAttack();
        if (arbitrationPolicy == HwBus.BASIC_ROUND_ROBIN) {
            stereotype = "BUS-RR";
        }

        if (arbitrationPolicy == HwBus.PRIORITY_BASED) {
            stereotype = "BUS-PB";
        }

        if (arbitrationPolicy == HwBus.CAN) {
            stereotype = "BUS-CAN";
        }

        if (arbitrationPolicy == HwBus.CROSSBAR) {
            stereotype = "CROSSBAR";
        }

        if (dialog.getByteDataSize().length() != 0) {
            try {
                tmp = byteDataSize;
                byteDataSize = Integer.decode(dialog.getByteDataSize()).intValue();
                if (byteDataSize <= 0) {
                    byteDataSize = tmp;
                    error = true;
                    errors += "Data size  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Data size  ";
            }
        }

        if (dialog.getSliceTime().length() != 0) {
            try {
                tmp = sliceTime;
                sliceTime = Integer.decode(dialog.getSliceTime()).intValue();
                if (sliceTime <= 0) {
                    sliceTime = tmp;
                    error = true;
                    errors += "Slice time  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Slice time  ";
            }
        }

        if (dialog.getPipelineSize().length() != 0) {
            try {
                tmp = pipelineSize;
                pipelineSize = Integer.decode(dialog.getPipelineSize()).intValue();
                if (pipelineSize <= 0) {
                    pipelineSize = tmp;
                    error = true;
                    errors += "Pipeline size  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Pipeline size  ";
            }
        }

        if (dialog.getClockRatio().length() != 0) {
            try {
                tmp = clockRatio;
                clockRatio = Integer.decode(dialog.getClockRatio()).intValue();
                if (clockRatio <= 0) {
                    clockRatio = tmp;
                    error = true;
                    errors += "Clock divider  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Clock divider  ";
            }
        }

		
        if (error) {
            JOptionPane.showMessageDialog(frame,
                                          "Invalid value for the following attributes: " + errors,
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        return true;
    }


    public int getType() {
        return TGComponentManager.TMLARCHI_BUSNODE;
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info stereotype=\"" + stereotype + "\" nodeName=\"" + name);
        sb.append("\" />\n");
        sb.append("<attributes byteDataSize=\"" + byteDataSize + "\" ");
        sb.append(" arbitrationPolicy=\"" + arbitrationPolicy + "\" ");
        sb.append(" sliceTime=\"" + sliceTime + "\" ");
        sb.append(" pipelineSize=\"" + pipelineSize + "\" ");
        sb.append(" clockRatio=\"" + clockRatio + "\" ");
        sb.append(" privacy=\"" + privacy + "\" ");
        sb.append(" referenceAttack=\"" + referenceAttack + "\" ");
        sb.append("/>\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
      //      int t1id;
            String sstereotype = null, snodeName = null;
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
                                sstereotype = elt.getAttribute("stereotype");
                                snodeName = elt.getAttribute("nodeName");
                            }
                            if (sstereotype != null) {
                                stereotype = sstereotype;
                            }
                            if (snodeName != null){
                                name = snodeName;
                            }
                            if (elt.getTagName().equals("attributes")) {
                                byteDataSize = Integer.decode(elt.getAttribute("byteDataSize")).intValue();
                                referenceAttack = elt.getAttribute("referenceAttack");
                                arbitrationPolicy =Integer.decode(elt.getAttribute("arbitrationPolicy")).intValue();                                                                    pipelineSize = Integer.decode(elt.getAttribute("pipelineSize")).intValue();
                                if ((elt.getAttribute("clockRatio") != null) &&  (elt.getAttribute("clockRatio").length() > 0)){
                                    clockRatio = Integer.decode(elt.getAttribute("clockRatio")).intValue();
                                }
                                if ((elt.getAttribute("sliceTime") != null) &&  (elt.getAttribute("sliceTime").length() > 0)){
                                    sliceTime = Integer.decode(elt.getAttribute("sliceTime")).intValue();
                                }
                                if ((elt.getAttribute("privacy") != null) &&  (elt.getAttribute("privacy").length() > 0)){
                                    privacy = Integer.decode(elt.getAttribute("privacy")).intValue();
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }


    public int getByteDataSize(){
        return byteDataSize;
    }

    public int getPipelineSize(){
        return pipelineSize;
    }

    public int getSliceTime(){
        return sliceTime;
    }

    public int getArbitrationPolicy(){
        return arbitrationPolicy;
    }
    public int getPrivacy(){
        return privacy;
    }


    public void setPrivacy(int p){
        privacy=p;
    }
    public String getAttributes() {
        String attr = "";
        attr += "Data size (in byte) = " + byteDataSize + "\n";
        attr += "Pipeline size = " + pipelineSize + "\n";
        if (arbitrationPolicy == HwBus.DEFAULT_ARBITRATION) {
            attr += "Arbitration policy = basic Round Robin\n";
        } else if (arbitrationPolicy == HwBus.PRIORITY_BASED) {
            attr += "Arbitration policy = priority based\n";
        }
        attr += "Slice time (in microseconds) = " + sliceTime + "\n";
        attr += "Clock divider = " + clockRatio + "\n";
        return attr;
    }

    public int getComponentType()       {
        return TRANSFER;
    }

}
