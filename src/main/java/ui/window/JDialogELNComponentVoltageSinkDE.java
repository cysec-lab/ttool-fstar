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

import ui.eln.sca_eln_sca_de.ELNComponentVoltageSinkDE;
import ui.util.IconManager;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Class JDialogELNComponentVoltageSinkDE Dialog for managing the conversion of
 * a ELN voltage to a DE output signal Creation: 07/08/2018
 * 
 * @version 1.0 07/08/2018
 * @author Irina Kit Yan LEE
 */

@SuppressWarnings("serial")

public class JDialogELNComponentVoltageSinkDE extends JDialog implements ActionListener {
  private JTextField nameTextField;
  private JTextField valueTextField;

  private ELNComponentVoltageSinkDE vsink;

  public JDialogELNComponentVoltageSinkDE(ELNComponentVoltageSinkDE _vsink) {
    setTitle("Setting the conversion of voltage to DE output signal");
    setLocationRelativeTo(null);
    setVisible(true);
    setAlwaysOnTop(true);
    setResizable(false);

    vsink = _vsink;

    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
    getRootPane().getActionMap().put("close", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });

    dialog();
  }

  public void dialog() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    this.add(mainPanel);

    JPanel attributesMainPanel = new JPanel(new GridLayout());
    mainPanel.add(attributesMainPanel, BorderLayout.NORTH);

    Box box = Box.createVerticalBox();
    box.setBorder(BorderFactory.createTitledBorder("Setting eln_de_vsink attributes"));

    GridBagLayout gridBag = new GridBagLayout();
    GridBagConstraints constraints = new GridBagConstraints();
    JPanel boxPanel = new JPanel();
    boxPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
    boxPanel.setLayout(gridBag);

    JLabel labelName = new JLabel("nm : ");
    constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(5, 10, 5, 10), 0, 0);
    gridBag.setConstraints(labelName, constraints);
    boxPanel.add(labelName);

    nameTextField = new JTextField(vsink.getValue().toString(), 10);
    constraints = new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(5, 10, 5, 10), 0, 0);
    gridBag.setConstraints(nameTextField, constraints);
    boxPanel.add(nameTextField);

    JLabel valueLabel = new JLabel("scale : ");
    constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(5, 10, 5, 10), 0, 0);
    gridBag.setConstraints(valueLabel, constraints);
    boxPanel.add(valueLabel);

    valueTextField = new JTextField("" + vsink.getScale(), 10);
    constraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(5, 10, 5, 10), 0, 0);
    gridBag.setConstraints(valueTextField, constraints);
    boxPanel.add(valueTextField);

    box.add(boxPanel);
    attributesMainPanel.add(box);

    JPanel downPanel = new JPanel(new FlowLayout());

    JButton saveCloseButton = new JButton("Save and close");
    saveCloseButton.setIcon(IconManager.imgic25);
    saveCloseButton.setActionCommand("Save_Close");
    saveCloseButton.addActionListener(this);
    saveCloseButton.setPreferredSize(new Dimension(200, 30));
    downPanel.add(saveCloseButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.setIcon(IconManager.imgic27);
    cancelButton.setActionCommand("Cancel");
    cancelButton.addActionListener(this);
    cancelButton.setPreferredSize(new Dimension(200, 30));
    downPanel.add(cancelButton);

    mainPanel.add(downPanel, BorderLayout.CENTER);
    pack();
    this.getRootPane().setDefaultButton(saveCloseButton);
  }

  public void actionPerformed(ActionEvent e) {
    if ("Save_Close".equals(e.getActionCommand())) {
      vsink.setValue(new String(nameTextField.getText()));

      if (!(valueTextField.getText().isEmpty())) {
        Boolean valValueDouble = false;
        try {
          Double.parseDouble(valueTextField.getText());
        } catch (NumberFormatException e1) {
          JDialog msg = new JDialog(this);
          msg.setLocationRelativeTo(null);
          JOptionPane.showMessageDialog(msg, "The scale coefficient is not a Double", "Warning !",
              JOptionPane.WARNING_MESSAGE);
          valValueDouble = true;
        }
        if (valValueDouble == false) {
          vsink.setScale(Double.parseDouble(valueTextField.getText()));
        }
      } else {
        vsink.setScale(1.0);
      }

      this.dispose();
    }

    if ("Cancel".equals(e.getActionCommand())) {
      this.dispose();
    }
  }
}
