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




package ui.window;

import ui.util.IconManager;
import ui.tmlcd.TMLChannelProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;


/**
 * Class JDialogChannel
 * Dialog for managing channel properties
 * Creation: 28/10/2005
 * @version 1.0 28/20/2005
 * @author Ludovic APVRILLE
 */
public class JDialogChannel extends JDialogBase implements ActionListener {

    private String name;
    private int type, size, max;
    
    public boolean data;   
    
    // Panel1
    private JTextField nameText, sizeText, maxText;
    private JComboBox<String> typeList;
    private Vector<String> types;

    /* Creates new form  */
    public JDialogChannel(String _name, int _size, int _type, int _max, Frame f, String title) {
        super(f, title, true);

        name = _name;
        type = _type;
        size = _size;
        max = _max;
        
        data = false;
        
        myInitComponents();
        initComponents();
        setComponents();
        pack();
    }
    
    private void myInitComponents() {
        types = new Vector<>();
        types.add(TMLChannelProperties.getStringChannelType(0));
        types.add(TMLChannelProperties.getStringChannelType(1));
        types.add(TMLChannelProperties.getStringChannelType(2));
    }
    
    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel1 = new JPanel();
        panel1.setLayout(gridbag1);
        panel1.setBorder(new javax.swing.border.TitledBorder("Setting idenfier and tclass "));
        panel1.setPreferredSize(new Dimension(300, 150));
        
        // first line panel1
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        panel1.add(new JLabel(" "), c1);
        
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("name:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        nameText = new JTextField(name);
        panel1.add(nameText, c1);
        
        c1.gridwidth = 1;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("size:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        sizeText = new JTextField(""+size);
        panel1.add(sizeText, c1);
        
        // second line panel1
        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("type:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        typeList = new JComboBox<>(types);
        typeList.setSelectedIndex(type);
        typeList.addActionListener(this);
        panel1.add(typeList, c1);
        
        c1.gridwidth = 1;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("max samples:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        maxText = new JTextField(""+max);
        panel1.add(maxText, c1);
        
        
        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        c.add(panel1, c0);
        
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        
        initButtons(c0, c, this);
    }
    
    public void	actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        
        if (evt.getSource() == typeList) {
          setComponents();
        } else {
          // Compare the action command to the known actions.
          if (command.equals("Save and Close"))  {
            closeDialog();
            } else if (command.equals("Cancel")) {
            cancelDialog();
            }
        }
    }
    
    public void setComponents() {
           if (typeList.getSelectedIndex() == 0) {
              maxText.setEnabled(true);
           } else {
             maxText.setEnabled(false);
           }
    }
    
    
    public void closeDialog() {
        data = true;
        dispose();
    }
    
    public void cancelDialog() {
        dispose();
    }
    
    public boolean hasNewData() {
        return data;
    }
    
    public String getChannelName() {
        return nameText.getText();
    }
    
    public String getSizeText() {
        return sizeText.getText();
    }
    
    public int getMyType() {
        return typeList.getSelectedIndex();
    }
    
    public String getMaxText() {
        return maxText.getText();
    }
    
}
