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

import ui.syscams.*;
import ui.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * Class JDialogSystemCAMSBlockDE 
 * Dialog for managing of SystemC-AMS DE Block
 * Creation: 26/04/2018
 * @version 1.0 26/04/2018
 * @author Irina Kit Yan LEE
 */

@SuppressWarnings("serial")

public class JDialogSysCAMSClock extends JDialog implements ActionListener, ListSelectionListener {

	private JTextField nameTextField;
//	private JTextField periodTextField;
//	private String listPeriodString[];
//	private JComboBox<String> periodComboBoxString;

        private JTextField frequencyTextField;
        private JTextField unitTextField;
        private JTextField dutyCycleTextField;
    
	private JTextField startTimeTextField;
	private String listUnitString[];
        private String posFirstString[];
        private JComboBox<String> unitComboBoxString;
        private JComboBox<String> posFirstComboBoxString;
	private SysCAMSClock clock;
    
        private JPanel parametersMainPanel;
	private JTextField nameStructTextField;
	private JTextField valueStructTextField;
	private JRadioButton constantStructRadioButton;
	private String listTypeStructString[];
	private JComboBox<String> typeStructComboBoxString;
	private ArrayList<String> listTmpStruct;
	private JList<String> structList;
	private DefaultListModel<String> structListModel;
	private boolean structBool = false;
	private JTextField nameTemplateTextField;
    private JTextField valueTemplateTextField;
	private String listTypeTemplateString[];
	private JComboBox<String> typeTemplateComboBoxString;
	private JTextField nameTypedefTextField;
	private String listTypeTypedefString[];
	private JComboBox<String> typeTypedefComboBoxString;
	private JButton addModifyTypedefButton;
	private ArrayList<String> listTmpTypedef;
	private JList<String> typedefList;
	private DefaultListModel<String> typedefListModel;
	private boolean typedefBool = false;
	
	private JButton upButton, downButton, removeButton;
	
	private JPanel codeMainPanel;
	private JTextField nameFnTextField;
	private JButton nameFnButton;
	private JTextArea codeTextArea;
	private String finalString;

    //	private SysCAMSClock block;

	public JDialogSysCAMSClock(SysCAMSClock clock) {
		this.setTitle("Setting DE Clock Attributes");
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setResizable(false);

		this.clock = clock;

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
		getRootPane().getActionMap().put("close", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		dialog();
	}

	public StringBuffer encode(String data) {
		StringBuffer databuf = new StringBuffer(data);
		StringBuffer buffer = new StringBuffer("");
		int endline = 0;
		int nb_arobase = 0;
		int condition = 0;

		for (int pos = 0; pos != data.length(); pos++) {
			char c = databuf.charAt(pos);
			switch (c) {
			case '\n':
				break;
			case '\t':
				break;
			case '{':
				buffer.append("{\n");
				endline = 1;
				nb_arobase++;
				break;
			case '}':
				if (nb_arobase == 1) {
					buffer.append("}\n");
					endline = 0;
				} else {
					int i = nb_arobase;
					while (i >= 1) {
						buffer.append("\t");
						i--;
					}
					buffer.append("}\n");
					endline = 1;
				}
				nb_arobase--;
				break;
			case ';':
				if (condition == 1) {
					buffer.append(";");
				} else {
					buffer.append(";\n");
					endline = 1;
				}
				break;
			case ' ':
				if (endline == 0) {
					buffer.append(databuf.charAt(pos));
				}
				break;
			case '(':
				buffer.append("(");
				condition = 1;
				break;
			case ')':
				buffer.append(")");
				condition = 0;
				break;
			default:
				if (endline == 1) {
					endline = 0;
					int i = nb_arobase;
					while (i >= 1) {
						buffer.append("\t");
						i--;
					}
				}
				buffer.append(databuf.charAt(pos));
				break;
			}
		}
		return buffer;
	}

	public void dialog() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		this.add(mainPanel);

		JPanel attributesMainPanel = new JPanel();
		if (clock.getFather() != null) {
			JTabbedPane tabbedPane = new JTabbedPane();
			parametersMainPanel = new JPanel();
			codeMainPanel = new JPanel();
			tabbedPane.add("Attributes", attributesMainPanel);
			//tabbedPane.add("Parameters", parametersMainPanel);
			//tabbedPane.add("Method Code", codeMainPanel);

			mainPanel.add(tabbedPane, BorderLayout.NORTH); 
		} else {
			mainPanel.add(attributesMainPanel, BorderLayout.NORTH); 
		}

		// --- Attributes ---//
		attributesMainPanel.setLayout(new BorderLayout());

		Box attributesBox = Box.createVerticalBox();
		attributesBox.setBorder(BorderFactory.createTitledBorder("Setting DE clock attributes"));

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel attributesBoxPanel = new JPanel();
		attributesBoxPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
		attributesBoxPanel.setLayout(gridBag);

		JLabel labelName = new JLabel("Name : ");
		constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(labelName, constraints);
		attributesBoxPanel.add(labelName);

		if (clock.getValue().toString().equals("")) {
			nameTextField = new JTextField(10);
		} else {
			nameTextField = new JTextField(clock.getValue().toString(), 10); 
		}
		constraints = new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(nameTextField, constraints);
		attributesBoxPanel.add(nameTextField);

	JLabel labelFrequency = new JLabel("Frequency : ");
		constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(labelFrequency, constraints);
		attributesBoxPanel.add(labelFrequency);

		if (clock.getValue().toString().equals("")) {
			nameTextField = new JTextField(10);
		} else {
			frequencyTextField = new JTextField(clock.getValue().toString(), 10); 
		}
		constraints = new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(frequencyTextField, constraints);
		attributesBoxPanel.add(frequencyTextField);


		JLabel labelDutyCycle = new JLabel("DutyCycle : ");
		constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(labelDutyCycle, constraints);
		attributesBoxPanel.add(labelDutyCycle);

		if (clock.getValue().toString().equals("")) {
			nameTextField = new JTextField(10);
		} else {
			frequencyTextField = new JTextField(clock.getValue().toString(), 10); 
		}
		constraints = new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(15, 10, 5, 10), 0, 0);
		gridBag.setConstraints(frequencyTextField, constraints);
		attributesBoxPanel.add(frequencyTextField);
		
		listUnitString = new String[4];
		listUnitString[0] = "s";
		listUnitString[1] = "ms";
		listUnitString[2] = "\u03BCs";
		listUnitString[3] = "ns";
		unitComboBoxString = new JComboBox<String>(listUnitString);
		if (clock.getUnit().equals("") || clock.getUnit().equals("s")) {
			unitComboBoxString.setSelectedIndex(0);
		} else if (clock.getUnit().equals("ms")){
			unitComboBoxString.setSelectedIndex(1);
		} else if (clock.getUnit().equals("\u03BCs")){
			unitComboBoxString.setSelectedIndex(2);
		} else if (clock.getUnit().equals("ns")){
			unitComboBoxString.setSelectedIndex(3);
		}
		unitComboBoxString.setActionCommand("unit");
		unitComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(unitComboBoxString, constraints);
		attributesBoxPanel.add(unitComboBoxString);

		posFirstString = new String[2];
		posFirstString[0] = "true";
		posFirstString[1] = "false";
	
		posFirstComboBoxString = new JComboBox<String>(posFirstString);
		if (clock.getPosFirst().equals("") || clock.getPosFirst().equals("true")) {
			posFirstComboBoxString.setSelectedIndex(0);
		} else if (clock.getPosFirst().equals("false")){
			posFirstComboBoxString.setSelectedIndex(1);
		}
		posFirstComboBoxString.setActionCommand("positive edge first");
		posFirstComboBoxString.addActionListener(this);
		constraints = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(5, 10, 5, 10), 0, 0);
		gridBag.setConstraints(posFirstComboBoxString, constraints);
		attributesBoxPanel.add(posFirstComboBoxString);
	
		attributesBox.add(attributesBoxPanel);
		attributesMainPanel.add(attributesBox, BorderLayout.NORTH); 


		// -- Button -- /
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

	    /*    clock.setName(nameTextField.getText());		
	    clock.setFrequency(frequencyTextField.getText());
	    clock.setDutyCycle(dutyCycleTextField.getText());
	    clock.setStartTime(startTimeTextField.getText());
	    clock.setPosFirst((String) posFirstComboBoxString.getSelectedItem());
	    clock.setUnit((String) unitComboBoxString.getSelectedItem());
	    */
		if ("Name_OK".equals(e.getActionCommand())) {
			if (!nameFnTextField.getText().equals("")) {
				codeTextArea.setText("void " + nameFnTextField.getText() + "() {\n\n}");
			} else {
				JDialog msg = new JDialog(this);
				msg.setLocationRelativeTo(null);
				JOptionPane.showMessageDialog(msg, "This method has no name. Please add a name for this method.", "Warning !",
						JOptionPane.WARNING_MESSAGE);
			}
		}
		if (clock.getFather() != null) {
			if ("OK".equals(e.getActionCommand())) {
				nameTypedefTextField.setEditable(true);
				typeTypedefComboBoxString.setEnabled(true);
				addModifyTypedefButton.setEnabled(true);
			}

			if ("Add_Modify_Struct".equals(e.getActionCommand())) {
				listTmpStruct = new ArrayList<String>();
				Boolean alreadyExist = false;
				int alreadyExistId = -1;
				String type = (String) typeStructComboBoxString.getSelectedItem();
				String s = null;

				Boolean valueBoolean = false, valueInteger = false, valueDouble = false, valueLong = false, nameEmpty = false;

				if (nameStructTextField.getText().isEmpty()) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "The name of struct is empty", "Warning !",
							JOptionPane.WARNING_MESSAGE);	
					nameEmpty = true;
				}

				for (int i = 0; i < structListModel.getSize(); i++) {
					if (nameStructTextField.getText().equals(structListModel.elementAt(i).split("\\s")[0])) {
						alreadyExist = true;
						alreadyExistId = i;
					}
				}

				if (alreadyExist == false) {
					try {
						if (type.equals("bool")) {
							Boolean.parseBoolean(valueStructTextField.getText());
						} else if (type.equals("double")) {
							Double.parseDouble(valueStructTextField.getText());
						} else if (type.equals("float")) {
							Float.parseFloat(valueStructTextField.getText());
						} else if (type.equals("int")) {
							Integer.parseInt(valueStructTextField.getText());
						} else if (type.equals("long")) {
							Long.parseLong(valueStructTextField.getText());
						} else if (type.equals("short")) {
							Short.parseShort(valueStructTextField.getText());
						}
					} catch (NumberFormatException e1) {
						if (type.equals("bool")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Boolean", "Warning !",
									JOptionPane.WARNING_MESSAGE);	
							valueBoolean = true;
						} else if (type.equals("double")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameteris not a Double", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueInteger = true;
						} else if (type.equals("float")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameteris not a Float", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueInteger = true;
						} else if (type.equals("int")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Integer", "Warning !",
									JOptionPane.WARNING_MESSAGE);		
							valueDouble = true;
						} else if (type.equals("long")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Long", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueLong = true;
						} else if (type.equals("short")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Short", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueLong = true;
						}
					}

					if ((valueBoolean == false) && (valueInteger == false) && (valueDouble == false) && (valueLong == false) && (nameEmpty == false)) {
						s = nameStructTextField.getText() + " = ";

						if (type.equals("bool")) {
							s = s + Boolean.parseBoolean(valueStructTextField.getText()) + " : ";
						} else if (type.equals("double")) {	
							s = s + Double.parseDouble(valueStructTextField.getText()) + " : ";
						} else if (type.equals("float")) {	
							s = s + Float.parseFloat(valueStructTextField.getText()) + " : ";
						} else if (type.equals("int")) {
							s = s + Integer.parseInt(valueStructTextField.getText()) + " : ";
						} else if (type.equals("long")) {
							s = s + Long.parseLong(valueStructTextField.getText()) + " : ";
						} else if (type.equals("short")) {
							s = s + Short.parseShort(valueStructTextField.getText()) + " : ";
						}

						if (constantStructRadioButton.isSelected()) {
							s = s + "const " + type;
						} else {
							s = s + type;
						}
						structListModel.addElement(s);
						listTmpStruct.add(s);
					}
				} else {
					try {
						if (type.equals("bool")) {
							Boolean.parseBoolean(valueStructTextField.getText());
						} else if (type.equals("double")) {
							Double.parseDouble(valueStructTextField.getText());
						} else if (type.equals("float")) {
							Float.parseFloat(valueStructTextField.getText());
						} else if (type.equals("int")) {
							Integer.parseInt(valueStructTextField.getText());
						} else if (type.equals("long")) {
							Long.parseLong(valueStructTextField.getText());
						} else if (type.equals("short")) {
							Short.parseShort(valueStructTextField.getText());
						}
					} catch (NumberFormatException e1) {
						if (type.equals("bool")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Boolean", "Warning !",
									JOptionPane.WARNING_MESSAGE);	
							valueBoolean = true;
						} else if (type.equals("double")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameteris not a Double", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueInteger = true;
						} else if (type.equals("float")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameteris not a Float", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueInteger = true;
						} else if (type.equals("int")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Integer", "Warning !",
									JOptionPane.WARNING_MESSAGE);		
							valueDouble = true;
						} else if (type.equals("long")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Long", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueLong = true;
						} else if (type.equals("short")) {
							JDialog msg = new JDialog(this);
							msg.setLocationRelativeTo(null);
							JOptionPane.showMessageDialog(msg, "The value of the parameter is not a Short", "Warning !",
									JOptionPane.WARNING_MESSAGE);
							valueLong = true;
						}
					}

					if ((valueBoolean == false) && (valueInteger == false) && (valueDouble == false) && (valueLong == false) && (nameEmpty == false)) {
						s = nameStructTextField.getText() + " = ";

						if (type.equals("bool")) {
							s = s + Boolean.parseBoolean(valueStructTextField.getText()) + " : ";
						} else if (type.equals("double")) {	
							s = s + Double.parseDouble(valueStructTextField.getText()) + " : ";
						} else if (type.equals("float")) {	
							s = s + Float.parseFloat(valueStructTextField.getText()) + " : ";
						} else if (type.equals("int")) {
							s = s + Integer.parseInt(valueStructTextField.getText()) + " : ";
						} else if (type.equals("long")) {
							s = s + Long.parseLong(valueStructTextField.getText()) + " : ";
						} else if (type.equals("short")) {
							s = s + Short.parseShort(valueStructTextField.getText()) + " : ";
						}

						if (constantStructRadioButton.isSelected()) {
							s = s + "const " + type;
						} else {
							s = s + type;
						}
						structListModel.setElementAt(s, alreadyExistId);
						listTmpStruct.add(s);
					}
				}
			}

			if ("Add_Modify_Typedef".equals(e.getActionCommand())) {
				listTmpTypedef = new ArrayList<String>();
				Boolean alreadyExist = false;
				int alreadyExistId = -1;
				String type = (String) typeTypedefComboBoxString.getSelectedItem();
				String s = null;

				Boolean nameEmpty = false;

				if (nameTypedefTextField.getText().isEmpty()) {
					JDialog msg = new JDialog(this);
					msg.setLocationRelativeTo(null);
					JOptionPane.showMessageDialog(msg, "The name of typedef is empty", "Warning !",
							JOptionPane.WARNING_MESSAGE);	
					nameEmpty = true;
				}

				if (nameEmpty == false) {
					for (int i = 0; i < typedefListModel.getSize(); i++) {
						if (nameTypedefTextField.getText().equals(typedefListModel.elementAt(i).split("\\s")[0])) {
							alreadyExist = true;
							alreadyExistId = i;
						}
					}

					if (alreadyExist == false) {
						s = nameTypedefTextField.getText() + " : " + type;
						typedefListModel.addElement(s);
						listTmpTypedef.add(s);
					} else {
						s = nameTypedefTextField.getText() + " : " + type;
						typedefListModel.setElementAt(s, alreadyExistId);
						listTmpTypedef.add(s);
					}
				}
			}


			if ("Remove".equals(e.getActionCommand())) {
				if (structBool == true) {
					if (structListModel.getSize() >= 1) {
						structListModel.remove(structList.getSelectedIndex());
					}
				}
				if (typedefBool == true) {
					if (typedefListModel.getSize() >= 1) {
						typedefListModel.remove(typedefList.getSelectedIndex());
					}
				}
			}

			if ("Up".equals(e.getActionCommand())) {
				if (structBool == true) {
					if (structList.getSelectedIndex() >= 1) {
						String sprev = structListModel.get(structList.getSelectedIndex()-1);
						structListModel.remove(structList.getSelectedIndex()-1);
						structListModel.add(structList.getSelectedIndex()+1, sprev);
					} else {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "Cannot move the parameter up", "Warning !",
								JOptionPane.WARNING_MESSAGE);
					}
				}
				if (typedefBool == true) {
					if (typedefList.getSelectedIndex() >= 1) {
						String sprev = typedefListModel.get(typedefList.getSelectedIndex()-1);
						typedefListModel.remove(typedefList.getSelectedIndex()-1);
						typedefListModel.add(typedefList.getSelectedIndex()+1, sprev);
					} else {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "Cannot move the parameter up", "Warning !",
								JOptionPane.WARNING_MESSAGE);
					}
				}
			}

			if ("Down".equals(e.getActionCommand())) {
				if (structBool == true) {
					if (structList.getSelectedIndex() < structListModel.getSize()-1) {
						String snext = structListModel.get(structList.getSelectedIndex()+1);
						structListModel.remove(structList.getSelectedIndex()+1);
						structListModel.add(structList.getSelectedIndex(), snext);
					} else {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "Cannot move the parameter down", "Warning !",
								JOptionPane.WARNING_MESSAGE);
					}
				}
				if (typedefBool == true) {
					if (typedefList.getSelectedIndex() < typedefListModel.getSize()-1) {
						String snext = typedefListModel.get(typedefList.getSelectedIndex()+1);
						typedefListModel.remove(typedefList.getSelectedIndex()+1);
						typedefListModel.add(typedefList.getSelectedIndex(), snext);
					} else {
						JDialog msg = new JDialog(this);
						msg.setLocationRelativeTo(null);
						JOptionPane.showMessageDialog(msg, "Cannot move the parameter down", "Warning !",
								JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		}
		
		if ("Save_Close".equals(e.getActionCommand())) {
			clock.setValue(new String(nameTextField.getText()));

//			if (!(periodTextField.getText().isEmpty())) {
//				Boolean periodValueInteger = false;
//				try {
//					Integer.parseInt(periodTextField.getText());
//				} catch (NumberFormatException e1) {
//					JDialog msg = new JDialog(this);
//					msg.setLocationRelativeTo(null);
//					JOptionPane.showMessageDialog(msg, "Period Tm is not a Integer", "Warning !",
//							JOptionPane.WARNING_MESSAGE);
//					periodValueInteger = true;
//				}
//				if (periodValueInteger == false) {
//					clock.setPeriod(Integer.parseInt(periodTextField.getText()));
//					clock.setTime((String) periodComboBoxString.getSelectedItem());
//				}
//			} else {
//				clock.setPeriod(-1);
//				clock.setTime("");
//			}

			if (clock.getFather() != null) {
				clock.setListStruct(structListModel);
				clock.setNameTemplate(nameTemplateTextField.getText());
				clock.setTypeTemplate((String) typeTemplateComboBoxString.getSelectedItem());
                clock.setValueTemplate(valueTemplateTextField.getText());
				clock.setListTypedef(typedefListModel);
				clock.setNameFn(nameFnTextField.getText());
				clock.setCode(codeTextArea.getText());
			}

			this.dispose();
		}

		if ("Cancel".equals(e.getActionCommand())) {
			if (clock.getFather() != null) {
				if (listTmpStruct != null) {
					for (String s : listTmpStruct) {
						structListModel.removeElement(s);
					}
				}
				if (listTmpTypedef != null) {
					for (String s : listTmpTypedef) {
						typedefListModel.removeElement(s);
					}
				}
			}
			this.dispose();
		}
	}
	
	public void valueChanged(ListSelectionEvent e) {
		JList listTmp = (JList) e.getSource();
		if (listTmp.equals(structList)) {
			structBool = true;
			typedefBool = false;
		}
		if (listTmp.equals(typedefList)) {
			typedefBool = true;
			structBool = false;
		}

		if (e.getValueIsAdjusting() == false) {
			if (structBool == true) {
				if (structList.getSelectedIndex() != -1) {
					String select = structListModel.get(structList.getSelectedIndex());
					String[] splita = select.split(" = ");
					nameStructTextField.setText(splita[0]);
					String[] splitb = splita[1].split(" : ");
					valueStructTextField.setText(splitb[0]);
					String[] splitc = splitb[1].split(" ");

					if (splitc[0].equals("const")) {
						constantStructRadioButton.setSelected(true);
						if (splitc[1].equals("bool")) {
							typeStructComboBoxString.setSelectedIndex(0);
						} else if (splitc[1].equals("double")) {
							typeStructComboBoxString.setSelectedIndex(1);
						} else if (splitc[1].equals("float")) {
							typeStructComboBoxString.setSelectedIndex(2);
						} else if (splitc[1].equals("int")) {
							typeStructComboBoxString.setSelectedIndex(3);
						} else if (splitc[1].equals("long")) {
							typeStructComboBoxString.setSelectedIndex(4);
						} else if (splitc[1].equals("short")) {
							typeStructComboBoxString.setSelectedIndex(5);
						}
					} else {
						constantStructRadioButton.setSelected(false);
						if (splitc[0].equals("bool")) {
							typeStructComboBoxString.setSelectedIndex(0);
						} else if (splitc[0].equals("double")) {
							typeStructComboBoxString.setSelectedIndex(1);
						} else if (splitc[0].equals("float")) {
							typeStructComboBoxString.setSelectedIndex(2);
						} else if (splitc[0].equals("int")) {
							typeStructComboBoxString.setSelectedIndex(3);
						} else if (splitc[0].equals("long")) {
							typeStructComboBoxString.setSelectedIndex(4);
						} else if (splitc[0].equals("short")) {
							typeStructComboBoxString.setSelectedIndex(5);
						}
					}

					if (structListModel.getSize() >= 2) {
						upButton.setEnabled(true);
						downButton.setEnabled(true);
					}
					removeButton.setEnabled(true);
				} 
			}

			if (typedefBool == true) {
				if (typedefList.getSelectedIndex() != -1) {
					String select = typedefListModel.get(typedefList.getSelectedIndex());
					String[] split = select.split(" : ");
					nameTypedefTextField.setText(split[0]);

					if (split[1].equals("sc_dt::sc_int")) {
						typeTypedefComboBoxString.setSelectedIndex(0);
					}

					if (typedefListModel.getSize() >= 2) {
						upButton.setEnabled(true);
						downButton.setEnabled(true);
					}
					removeButton.setEnabled(true);
				}
			}
		}
	}
}
