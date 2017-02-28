/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class JDialogProverifVerification
 * Dialog for managing the generation of ProVerif code and execution of
 * ProVerif
 * Creation: 19/02/2017
 * @version 1.0 19/02/2017
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;

import myutil.*;
import avatartranslator.toproverif.*;
import avatartranslator.*;
import proverifspec.*;
import ui.*;

import launcher.*;


public class JDialogProverifVerification extends javax.swing.JDialog implements ActionListener, Runnable, MasterProcessInterface  {

    private static final Insets insets = new Insets(0, 0, 0, 0);
    
    protected MainGUI mgui;

    private String textC1 = "Generate ProVerif code in: ";
    private String textC2 = "Execute ProVerif as: ";

    protected static String pathCode;
    protected static String pathExecute;


    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;

    public final static int REACHABILITY_ALL        = 1;
    public final static int REACHABILITY_SELECTED   = 2;
    public final static int REACHABILITY_NONE       = 3;

    int mode;

    //components
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;

    
    //protected JRadioButton exe, exeint;
    //protected ButtonGroup exegroup;
    protected JLabel gen, comp, exe;
    protected JTextField code1, code2, unitcycle, compiler1, exe1, exe2, exe3, exe2int, loopLimit;
    //protected JTabbedPane jp1;
    protected JScrollPane jsp;
    protected JCheckBox outputOfProVerif, typedLanguage;
    protected JRadioButton stateReachabilityAll, stateReachabilitySelected, stateReachabilityNone;
    protected ButtonGroup stateReachabilityGroup;
    protected JComboBox versionSimulator;

    private Thread t;
    private boolean go = false;
    private boolean hasError = false;
    //protected boolean startProcess = false;

    private String hostProVerif;

    protected RshClient rshc;


    /** Creates new form  */
    public JDialogProverifVerification(Frame f, MainGUI _mgui, String title, String _hostProVerif, String _pathCode, String _pathExecute) {
        super(f, title, true);

        mgui = _mgui;

        if (pathCode == null) {
            pathCode = _pathCode;
        }

        if (pathExecute == null)
            pathExecute = _pathExecute;


        hostProVerif = _hostProVerif;

        initComponents();
        myInitComponents();
        pack();

        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    protected void myInitComponents() {
        mode = NOT_STARTED;
        setButtons();
    }

    private void addComponent(Container container, Component component, int gridx, int gridy,
			      int gridwidth, int gridheight, int anchor, int fill) {
	GridBagConstraints gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0,
							anchor, fill, insets, 0, 0);
	container.add(component, gbc);
    }

    protected void initComponents() {

        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //jp1 = new JTabbedPane();

        JPanel jp01 = new JPanel();
        GridBagLayout gridbag01 = new GridBagLayout();
        //GridBagConstraints c01 = new GridBagConstraints();
        jp01.setLayout(gridbag01);
        jp01.setBorder(new javax.swing.border.TitledBorder("Verification options"));

        
        gen = new JLabel(textC1);
	addComponent(jp01, gen, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        //genJava.addActionListener(this);
        //jp01.add(gen, c01);

	//c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        code1 = new JTextField(pathCode, 100);
	addComponent(jp01, code1, 1, 0, 3, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH);
        //jp01.add(code1, c01);

	
        //jp01.add(new JLabel(" "), c01);
        //c01.gridwidth = GridBagConstraints.REMAINDER; //end row

	exe = new JLabel(textC2);
	//jp01.add(exe, c01);
	addComponent(jp01, exe, 0, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH);

        exe2 = new JTextField(pathExecute +  " -in pi ", 100);
	addComponent(jp01, exe2, 1, 1, 3, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
 

        //jp01.add(new JLabel(" "), c01);

	    //c01.gridx = 0;
	//        //c01.gridy = 3;
        //c01.gridwidth = 1;
	//c01.gridwidth = GridBagConstraints.REMAINDER; //end row


        //jp01.add(new JLabel("Compute state reachability: "), c01);
	  addComponent(jp01, new JLabel("Compute state reachability: "), 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	  //c01.gridwidth = GridBagConstraints.REMAINDER; //end row

	//jp01.add(new JLabel("hi there: "), c01);
	//jp01.add(new JLabel("hi hi there: "), c01);

	
	
        stateReachabilityGroup = new ButtonGroup ();

	//c01.gridy = 5;
        //c01.gridx = 1;

	/*JPanel bl1 = new JPanel();
	
	//c01.gridwidth = 1;*/
        stateReachabilityAll = new JRadioButton("all");
        //bl1.add(stateReachabilityAll);
	addComponent(jp01, stateReachabilityAll, 1, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	

        //c01.gridx = 2;
        stateReachabilitySelected = new JRadioButton("selected");
        //bl1.add(stateReachabilitySelected);
	addComponent(jp01, stateReachabilitySelected, 2, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        //c01.gridx = 3;
        //c01.gridwidth = GridBagConstraints.REMAINDER; //end row
	stateReachabilityNone = new JRadioButton("none");
	addComponent(jp01, stateReachabilityNone, 3, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        //bl1.add(stateReachabilityNone);
	//jp01.add(bl1, c01);

        stateReachabilityGroup.add (stateReachabilityAll);
        stateReachabilityGroup.add (stateReachabilitySelected);
        stateReachabilityGroup.add (stateReachabilityNone);
        stateReachabilityAll.setSelected(true);

        //c01.gridx = GridBagConstraints.RELATIVE;
        //c01.gridy = GridBagConstraints.RELATIVE;
        typedLanguage = new JCheckBox("Generate typed Pi calculus");
        typedLanguage.setSelected(true);
        //jp01.add(typedLanguage, c01);
	addComponent(jp01, typedLanguage, 0, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

	/*c01.gridwidth= 1;
	//c01.gridwidth = GridBagConstraints.REMAINDER; //end row
	//JPanel pan1 = new JPanel();*/
	addComponent(jp01, new JLabel("Limit on loop iterations:"), 0, 5, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	//jp01.add(new JLabel("Limit on loop iterations:"), c01);
	//c01.gridwidth= GridBagConstraints.REMAINDER;
        loopLimit = new JTextField("1", 3);
	addComponent(jp01, loopLimit, 1, 5, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	//jp01.add(loopLimit, c01);
	//jp01.add(pan1, c01);*/
        /*optimizemode = new JCheckBox("Optimize code");
          optimizemode.setSelected(optimizeModeSelected);
          jp01.add(optimizemode, c01);

          jp01.add(new JLabel("Simulator used:"), c01);

          versionSimulator = new JComboBox(simus);
          versionSimulator.setSelectedIndex(selectedItem);
          versionSimulator.addActionListener(this);
          jp01.add(versionSimulator, c01);
        //System.out.println("selectedItem=" + selectedItem);

        //devmode = new JCheckBox("Development version of the simulator");
        //devmode.setSelected(true);
        //jp01.add(devmode, c01);

        //jp01.add(new JLabel(" "), c01);

        //jp1.add("Generate code", jp01);*/


        	

        outputOfProVerif = new JCheckBox("Show output of ProVerif");
        outputOfProVerif.setSelected(false);
        //jp01.add(outputOfProVerif, c01);*/
	addComponent(jp01, outputOfProVerif, 0, 6, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);


        //jp1.add("Execute", jp03);

        c.add(jp01, BorderLayout.NORTH);


        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select options and then, click on 'start' to launch ProVerif code generation / compilation\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	jsp.setPreferredSize(new Dimension(300,300));
        c.add(jsp, BorderLayout.CENTER);

        start = new JButton("Start", IconManager.imgic53);
        stop = new JButton("Stop", IconManager.imgic55);
        close = new JButton("Close", IconManager.imgic27);

        start.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));
        close.setPreferredSize(new Dimension(120, 30));

        start.addActionListener(this);
        stop.addActionListener(this);
        close.addActionListener(this);

        JPanel jp2 = new JPanel();
        jp2.add(start);
        jp2.add(stop);
        jp2.add(close);

        c.add(jp2, BorderLayout.SOUTH);

    }

    public void actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();

        if (command.equals("Start"))  {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        }
    }

    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
    }

    public void stopProcess() {
        if (rshc != null ){
            try {
                rshc.stopCommand();
            } catch (LauncherException le) {
            }
        }
        rshc = null;
        mode =  STOPPED;
        setButtons();
        go = false;
    }

    public void startProcess() {
        t = new Thread(this);
        mode = STARTED;
        setButtons();
        go = true;
        t.start();
    }

    private void testGo() throws InterruptedException {
        if (go == false) {
            throw new InterruptedException("Stopped by user");
        }
    }

    public void run() {
        String cmd;
        String list, data;
        int cycle = 0;

        hasError = false;

        TraceManager.addDev("Thread started");
        File testFile;
        try {
            // Code generation
            //if (jp1.getSelectedIndex() == 0) {
                jta.append("Generating ProVerif code\n");

                testGo();
                pathCode = code1.getText().trim ();

                if (pathCode.isEmpty()){
                    pathCode="pvspec";
                }

                testFile = new File(pathCode);

                if (testFile.isDirectory()){
                    if (!pathCode.endsWith (File.separator)){
                        pathCode += File.separator;
                    }
                    pathCode += "pvspec";
                    testFile = new File(pathCode);
                }
                
                if (testFile.exists()){
                    // FIXME Raise error
                    System.out.println("FILE EXISTS!!!");
                }
                if (mgui.gtm.generateProVerifFromAVATAR(pathCode, stateReachabilityAll.isSelected () ? REACHABILITY_ALL : stateReachabilitySelected.isSelected () ? REACHABILITY_SELECTED : REACHABILITY_NONE, typedLanguage.isSelected(), loopLimit.getText())) {
                    jta.append("ProVerif code generation done\n");
                } else {
		    setError();
                    jta.append("Could not generate proverif code\n");
                }

                if (typedLanguage.isSelected()){
                    exe2.setText(pathExecute +  " -in pitype ");		
                }
                else {
                    exe2.setText(pathExecute +  " -in pi ");	
                }
                exe2.setText(exe2.getText()+pathCode);
                //if (mgui.gtm.getCheckingWarnings().size() > 0) {
                jta.append("" +  mgui.gtm.getCheckingWarnings().size() + " warning(s)\n");
                //}
		//}
            testGo();
            // Execute
            //if (jp1.getSelectedIndex() == 1) {
                try {

                    cmd = exe2.getText();

                    jta.append("Executing ProVerif code with command: \n" + cmd + "\n");

                    rshc = new RshClient(hostProVerif);
                    // Assuma data are on the remote host
                    // Command

                    data = processCmd(cmd);


                    if (outputOfProVerif.isSelected()) {
                        jta.append(data);
                    }

                    ProVerifOutputAnalyzer pvoa = mgui.gtm.getProVerifOutputAnalyzer ();
                    pvoa.analyzeOutput(data, typedLanguage.isSelected());

                    if (pvoa.getErrors().size() != 0) {
                        jta.append("\nErrors found in the generated code:\n----------------\n");
                        for(String error: pvoa.getErrors()) {
                            jta.append(error+"\n");
                        }

                    } else {

                        jta.append("\nReachable states:\n----------------\n");
                        for(String re: pvoa.getReachableEvents()) {
                            jta.append(re+"\n");
                        }

                        jta.append("\nNon reachable states:\n----------------\n");
                        for(String re: pvoa.getNonReachableEvents()) {
                            jta.append(re+"\n");
                        }

                        jta.append("\nConfidential Data:\n----------------\n");
                        for(AvatarAttribute attr: pvoa.getSecretTerms()) {
                            jta.append(attr.getBlock ().getName () + "." + attr.getName () + "\n");
                        }

                        jta.append("\nNon Confidential Data:\n----------------\n");
                        for(AvatarAttribute attr: pvoa.getNonSecretTerms()) {
                            jta.append(attr.getBlock ().getName () + "." + attr.getName () + "\n");
                        }

                        jta.append("\nSatisfied Strong Authenticity:\n----------------\n");
                        for(String re: pvoa.getSatisfiedAuthenticity()) {
                            jta.append(re+"\n");
                        }

                        jta.append("\nSatisfied Weak Authenticity:\n----------------\n");
                        for(String re: pvoa.getSatisfiedWeakAuthenticity()) {
                            jta.append(re+"\n");
                        }

                        jta.append("\nNon Satisfied Strong Authenticity:\n----------------\n");
                        for(String re: pvoa.getNonSatisfiedAuthenticity()) {
                            jta.append(re+"\n");
                        }

                        jta.append("\nNon proved queries:\n----------------\n");
                        for(String re: pvoa.getNotProved()) {
                            jta.append(re+"\n");
                        }
                    }

                    mgui.modelBacktracingProVerif(pvoa);

                    jta.append("\nAll done\n");
                } catch (LauncherException le) {
                    jta.append("Error: " + le.getMessage() + "\n");
                    mode =      STOPPED;
                    setButtons();
                    return;
                } catch (Exception e) {
                    mode =      STOPPED;
                    setButtons();
                    return;
                }
		//}

		/*if ((hasError == false) && (jp1.getSelectedIndex() < 1)) {
                jp1.setSelectedIndex(jp1.getSelectedIndex() + 1);
		}*/

        } catch (InterruptedException ie) {
            jta.append("Interrupted\n");
        }

        jta.append("\n\nReady to process next command\n");

        checkMode();
        setButtons();

        //System.out.println("Selected item=" + selectedItem);
    }

    protected String processCmd(String cmd) throws LauncherException {
        rshc.setCmd(cmd);
        String s = null;
        rshc.sendExecuteCommandRequest();
        s = rshc.getDataFromProcess();
        return s;
    }

    protected void checkMode() {
        mode = NOT_STARTED;
    }

    protected void setButtons() {
        switch(mode) {
            case NOT_STARTED:
                start.setEnabled(true);
                stop.setEnabled(false);
                close.setEnabled(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                break;
            case STARTED:
                start.setEnabled(false);
                stop.setEnabled(true);
                close.setEnabled(false);
                getGlassPane().setVisible(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                break;
            case STOPPED:
            default:
                start.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
        }
    }

    public boolean hasToContinue() {
        return (go == true);
    }

    public void appendOut(String s) {
        jta.append(s);
    }

    public void setError() {
        hasError = true;
    }
}