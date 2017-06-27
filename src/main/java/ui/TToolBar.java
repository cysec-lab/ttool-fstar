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




package ui;

import javax.swing.*;
import java.util.*;
//import java.awt.event.*;

import myutil.*;
import common.*;


/**
 * Class TToolBar
 * Abstract toolbar to be used by TURTLE diagrams
 * Creation: 21/12/2003
 * @version 1.0 21/12/2003
 * @author Ludovic APVRILLE
 * @see TGComponent
 */
public abstract class TToolBar extends JToolBar {

    protected ArrayList<TAction> pluginActions;

    //protected ActionListener buttonTB;
    protected MainGUI mgui;
    /*protected int typeSelected = TGComponentManager.EDIT;
      protected int idSelected = TGComponentManager.EDIT;*/

    public TToolBar(MainGUI _mgui) {
        super();
        mgui = _mgui;
        setOrientation(SwingConstants.HORIZONTAL);
        setFloatable(true) ;
        setButtons();
    }

    // asbtract operations
    protected abstract void setButtons();

    protected abstract void setActive(boolean b);

    protected void setPluginButtons(String diag) {
        pluginActions = new ArrayList<TAction>();
        this.addSeparator();
        for(int i=0; i<ConfigurationTTool.PLUGIN_GRAPHICAL_COMPONENT.length; i++) {
            Plugin p = PluginManager.pluginManager.getPluginOrCreate(ConfigurationTTool.PLUGIN_GRAPHICAL_COMPONENT[i]);
            if (p != null) {
                String panelName = p.executeRetStringMethod("CustomizerGraphicalComponent", "getPanelClassName");
                if (panelName.compareTo(diag) == 0){
                    String shortText = p.executeRetStringMethod("CustomizerGraphicalComponent", "getShortText");
                    String longText = p.executeRetStringMethod("CustomizerGraphicalComponent", "getLongText");
                    String veryShortText = p.executeRetStringMethod("CustomizerGraphicalComponent", "veryShortText");
                    ImageIcon img = p.executeRetImageIconMethod("CustomizerGraphicalComponent", "getImageIcon");
                    if ((imgIcon != null)  && (shortText != null)) {
                        TraceManager.addDev("Plugin: " + p.getName() + " short name:" + shortText);
                        TAction t = new TAction("command-" + i, shortText, img, img, veryShortText, longText, 0);
                        TGUIAction tguia = new TGUIAction(t);
                        pluginActions.add(t);
                        JButton button = add(tguia);
                        button.addMouseListener(mgui.mouseHandler);
                    }
                }

            }
        }
    }

} // Class
