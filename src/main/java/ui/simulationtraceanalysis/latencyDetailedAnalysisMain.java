package ui.simulationtraceanalysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import common.ConfigurationTTool;
import myutil.GraphicLib;
import myutil.PluginManager;
import myutil.TraceManager;
import tmltranslator.TMLMapping;
import ui.MainGUI;
import ui.SimulationTrace;
import ui.TGComponent;
import ui.TMLArchiPanel;
import ui.TMLComponentDesignPanel;
import ui.TURTLEPanel;
import ui.tmldd.TMLArchiArtifact;
import ui.tmldd.TMLArchiNode;
import ui.util.IconManager;
import ui.window.JDialogToChosePanel;

public class latencyDetailedAnalysisMain {

    private DirectedGraphTranslator dgraph;
    private Vector<String> checkedTransactionsFile1 = new Vector<String>();
    private Vector<String> checkedTransactionsFile2 = new Vector<String>();
    private Vector<String> checkedTransactionsFile = new Vector<String>();
    public MainGUI mainGUI_compare2,mainGUI_compare;
    
    private JFrameLatencyDetailedAnalysis latencyDetailedAnalysis;

    public latencyDetailedAnalysisMain() {

    }

    public void latencyDetailedAnalysis(SimulationTrace selectedST, TURTLEPanel selectedTab, boolean b, boolean compare, MainGUI mainGUI_compare) {

        Vector<TGComponent> tmlNodesToValidate = new Vector<TGComponent>();
        List<TMLComponentDesignPanel> cpanels;
        TMLComponentDesignPanel compdp;
        TURTLEPanel tp = selectedTab;
       
        // tmlap = (TMLArchiPanel) tp;

        if (mainGUI_compare.gtm == null) {

        } else {

            if (mainGUI_compare.gtm.getTMLMapping() != null) {
                
                TMLArchiPanel  tmlap = (TMLArchiPanel) tp;
                TMLMapping<TGComponent> map = mainGUI_compare.gtm.getTMLMapping();
                for (TGComponent component : tmlap.tmlap.getComponentList()) {
                    tmlNodesToValidate.add(component);
                }
                TGComponent tgc;
                List<TMLArchiArtifact> artifacts;
                String namePanel;
                TURTLEPanel tup;
                Iterator<? extends TGComponent> iterator = tmlNodesToValidate.listIterator();
                cpanels = new ArrayList<TMLComponentDesignPanel>();

                while (iterator.hasNext()) {
                    tgc = iterator.next();

                    if (tgc instanceof TMLArchiNode) {
                        artifacts = ((TMLArchiNode) (tgc)).getAllTMLArchiArtifacts();
                        for (TMLArchiArtifact artifact : artifacts) {
                            namePanel = artifact.getReferenceTaskName();
                            try {
                                tup = mainGUI_compare.getTURTLEPanel(namePanel);

                                if (tup instanceof TMLComponentDesignPanel) {
                                    compdp = (TMLComponentDesignPanel) (tup);
                                    if (!cpanels.contains(compdp)) {
                                        cpanels.add(compdp);
                                    }
                                }
                            } catch (Exception e) {
                                // Just in case the mentionned panel is not a TML design Panel
                            }

                        }
                    }
                }
                if (compare) {
                    dgraph = new DirectedGraphTranslator(map, cpanels);

                    for (TGComponent tgc1 : map.getTMLModeling().getCheckedComps().keySet()) {
                        String compName = map.getTMLModeling().getCheckedComps().get(tgc1);
                        TraceManager.addDev(compName + "__" + tgc1.getDIPLOID());

                        checkedTransactionsFile.add(compName + "__" + tgc1.getDIPLOID());

                    }
                } else {
                    latencyDetailedAnalysis = new JFrameLatencyDetailedAnalysis(map, cpanels, selectedST);
                    latencyDetailedAnalysis.setIconImage(IconManager.img9);
                    GraphicLib.centerOnParent(latencyDetailedAnalysis, 900, 600);
                    latencyDetailedAnalysis.setVisible(b);

                }
            } else {

                if (mainGUI_compare.gtm.getArtificialTMLMapping() != null) {

                    TMLMapping<TGComponent> map = mainGUI_compare.gtm.getArtificialTMLMapping();
                    TMLComponentDesignPanel tmlcdp = (TMLComponentDesignPanel) tp;
                    cpanels = new ArrayList<TMLComponentDesignPanel>();
                    cpanels.add(tmlcdp);
                    if (compare) {
                        dgraph = new DirectedGraphTranslator(map, cpanels);

                        for (TGComponent tgc : map.getTMLModeling().getCheckedComps().keySet()) {
                            String compName = map.getTMLModeling().getCheckedComps().get(tgc);
                            TraceManager.addDev(compName + "__" + tgc.getDIPLOID());

                            checkedTransactionsFile.add(compName + "__" + tgc.getDIPLOID());

                        }

                    } else {
                        latencyDetailedAnalysis = new JFrameLatencyDetailedAnalysis(map, cpanels, selectedST);
                        latencyDetailedAnalysis.setIconImage(IconManager.img9);
                        GraphicLib.centerOnParent(latencyDetailedAnalysis, 900, 600);
                        latencyDetailedAnalysis.setVisible(b);

                    }
                } else {

                }
            }
        }

        // dp.getPanels();

    }

    public void compareLatencyForXML( MainGUI mainGUI,SimulationTrace selectedST, boolean b) {

     

        final DirectedGraphTranslator dgraph1, dgraph2;
        try {

            checkedTransactionsFile = new Vector<String>();
            latencyDetailedAnalysisForXML(mainGUI,selectedST, false, true, 1);

            checkedTransactionsFile1 = checkedTransactionsFile;
        } catch (XPathExpressionException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ParserConfigurationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (SAXException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        while (dgraph.getGraphsize() == 0) {

        }

        if (dgraph.getGraphsize() > 0) {
            dgraph1 = dgraph;
            dgraph = null;

            JFileChooser fc;

            if (ConfigurationTTool.SystemCCodeDirectory.length() > 0) {
                fc = new JFileChooser(ConfigurationTTool.SystemCCodeDirectory);
            } else {
                fc = new JFileChooser();
            }

            FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
            fc.setFileFilter(filter);

            int returnVal = fc.showOpenDialog(mainGUI.frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File filefc = fc.getSelectedFile();
                // file2.setText(file.getPath());

                // Object obj = filefc;

                checkedTransactionsFile = new Vector<String>();
                SimulationTrace file2 = new SimulationTrace(filefc.getName(), 6, filefc.getAbsolutePath());

                if (file2 instanceof SimulationTrace) {

                    try {
                        latencyDetailedAnalysisForXML(mainGUI,file2, false, true, 2);
                    } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    while (dgraph.getGraphsize() == 0) {

                    }

                    if (dgraph.getGraphsize() > 0) {
                        dgraph2 = dgraph;
                        checkedTransactionsFile2 = checkedTransactionsFile;
                        JFrameCompareLatencyDetail cld = new JFrameCompareLatencyDetail(dgraph1, dgraph2, checkedTransactionsFile1,
                                checkedTransactionsFile2, selectedST, file2, true);

                        mainGUI_compare2.closeTurtleModeling();

                    }
                }

            }

        }
    }

    public void latencyDetailedAnalysisForXML(MainGUI mainGUI,SimulationTrace selectedST, boolean b, boolean compare, int j)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        String xml = ""; // Populated XML String....

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document dDoc = builder.parse(selectedST.getFullPath());

        XPath xPath = XPathFactory.newInstance().newXPath();

        String modelNode = (String) xPath.evaluate("/siminfo/global/model", dDoc, XPathConstants.STRING);
        TURTLEPanel panel = null;
        Vector<TURTLEPanel> allTabs = new Vector<TURTLEPanel>();

        if (j == 1) {
            mainGUI_compare = mainGUI;

        } else if (j == 2) {
            String fileName = null;

            Document dDoc1 = null;

            if (modelNode.contains("DIPLODOCUS architecture and mapping Diagram")) {

                fileName = modelNode.replace(" / DIPLODOCUS architecture and mapping Diagram", "");
            }

            if (modelNode.contains("TML Component Task Diagram")) {

                fileName = modelNode.replace(" / TML Component Task Diagram", "");
            }

            try {
                mainGUI_compare = null;
                mainGUI_compare = new MainGUI(false, false, false, false, false, false, false, false, false, false, true, false, false);
                mainGUI_compare.build();
                // mainGUI_compare.dtree = new JDiagramTree(mainGUI_compare);
                // mainGUI_compare.frame = new JFrame("TTool");
                // mainGUI_compare.panelForTab = new JPanel();
                // mainGUI_compare.panelForTab.setLayout(new BorderLayout());
                // Actions
                // mainGUI_compare.initActions();

                // mainGUI_compare.actions = new TGUIAction[TGUIAction.NB_ACTION];
                // for (int i = 0; i < TGUIAction.NB_ACTION; i++) {
                // mainGUI_compare.actions[i] = new TGUIAction(i);
                // mainGUI_compare.actions[i].addActionListener(mainGUI_compare);
                // }
                // mainGUI_compare.actionsLast = new
                // TGUIAction[ConfigurationTTool.NB_LAST_OPEN_FILE];
                // for (int j2 = 0; j2 < mainGUI_compare.actionsLast.length; j2++) {
                // mainGUI_compare.actionsLast[j2] = new TGUIAction(TGUIAction.ACT_OPEN_LAST,
                // "Open recent: " + ConfigurationTTool.LastOpenFiles[j]);
                // mainGUI_compare.actionsLast[j2].addActionListener(mainGUI_compare);

                // }
                // if (mainGUI_compare.jmenubarturtle != null) {
                // mainGUI_compare.jmenubarturtle.makeFileMenu(mainGUI_compare);
                // }

                // mode
                // mainGUI_compare.setMode(NOT_OPENED);
                PluginManager.pluginManager = new PluginManager();

                // if (testCall) {
                // mainGUI_compare.openProjectFromFile(new
                // File("/home/maysam/eclipse/TTool/ttool/src/test/resources/ui/graphLatencyAnalysis/input/GraphTestModelSecure.xml"));

                // } else {
                mainGUI_compare.openProjectFromFile(new File(fileName));
                // }

                mainGUI_compare.frame.setVisible(false);
                mainGUI_compare2 = mainGUI_compare;

                // checkModelingSyntax(mainGUI_test.tabs.get(1), true);

            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        if (modelNode.contains("DIPLODOCUS architecture and mapping Diagram")) {

            for (int i = 0; i < mainGUI_compare.tabs.size(); i++) {

                if (mainGUI_compare.tabs.get(i) instanceof TMLArchiPanel) {

                    allTabs.add(mainGUI_compare.tabs.get(i));

                }

            }

            if (allTabs.size() == 1) {
                mainGUI_compare.checkModelingSyntax(allTabs.get(0), true);
                TURTLEPanel selectedTab = allTabs.get(0);

                if (compare) {

                    latencyDetailedAnalysis(selectedST, selectedTab, b, true, mainGUI_compare);

                } else {
                    latencyDetailedAnalysis(selectedST, selectedTab, b, false, mainGUI_compare);

                }

                mainGUI_compare.setMode(mainGUI_compare.MODEL_CHANGED);

            } else if (allTabs.size() > 1) {

                JDialogToChosePanel jdmc = new JDialogToChosePanel(mainGUI_compare.frame, allTabs, "Choosing panel to validate");
                // if (b) {
                GraphicLib.centerOnParent(jdmc);
                jdmc.setVisible(true); // blocked until dialog has been closed

                // }
                mainGUI_compare.setMode(mainGUI_compare.MODEL_CHANGED);
                TURTLEPanel selectedTab = jdmc.getSelectedTab();
                if (selectedTab != null) {

                    mainGUI_compare.checkModelingSyntax(selectedTab, true);

                    if (compare) {

                        latencyDetailedAnalysis(selectedST, selectedTab, b, true, mainGUI_compare);

                    } else {
                        latencyDetailedAnalysis(selectedST, selectedTab, b, false, mainGUI_compare);
                    }
                    mainGUI_compare.setMode(mainGUI_compare.MODEL_CHANGED);

                }
            }

        } else if (modelNode.contains("TML Component Task Diagram")) {

            for (int i = 0; i < mainGUI_compare.tabs.size(); i++) {
                panel = mainGUI_compare.tabs.get(i);

                if (mainGUI_compare.tabs.get(i) instanceof TMLComponentDesignPanel) {
                    allTabs.add(mainGUI_compare.tabs.get(i));
                }

            }

            if (allTabs.size() == 1) {
                mainGUI_compare.checkModelingSyntax(allTabs.get(0), true);

                TURTLEPanel selectedTab = allTabs.get(0);
                if (compare) {

                    latencyDetailedAnalysis(selectedST, selectedTab, b, true, mainGUI_compare);

                } else {
                    latencyDetailedAnalysis(selectedST, selectedTab, b, false, mainGUI_compare);
                }
                mainGUI_compare.setMode(mainGUI_compare.MODEL_CHANGED);

            } else if (allTabs.size() > 1) {

                JDialogToChosePanel jdmc = new JDialogToChosePanel(mainGUI_compare.frame, allTabs, "Choosing panel to validate");

                // if (b) {
                GraphicLib.centerOnParent(jdmc);
                jdmc.setVisible(true); // blocked until dialog has been closed

                // }

                mainGUI_compare.setMode(mainGUI_compare.MODEL_CHANGED);
                TURTLEPanel selectedTab = jdmc.getSelectedTab();
                if (selectedTab != null) {

                    mainGUI.checkModelingSyntax(selectedTab, true);
                    if (compare) {

                        latencyDetailedAnalysis(selectedST, selectedTab, b, true, mainGUI_compare);

                    } else {
                        latencyDetailedAnalysis(selectedST, selectedTab, b, false, mainGUI_compare);
                    }
                    mainGUI_compare.setMode(mainGUI_compare.MODEL_CHANGED);

                }
            }

        }

    }

    public DirectedGraphTranslator getDgraph() {
        return dgraph;
    }

    public void setDgraph(DirectedGraphTranslator dgraph) {
        this.dgraph = dgraph;
    }

    public Vector<String> getCheckedTransactionsFile() {
        return checkedTransactionsFile;
    }

    public void setCheckedTransactionsFile(Vector<String> checkedTransactionsFile) {
        this.checkedTransactionsFile = checkedTransactionsFile;
    }
    
    public JFrameLatencyDetailedAnalysis getLatencyDetailedAnalysis() {
        return latencyDetailedAnalysis;
    }

    
    protected static String getBaseResourcesDir() {
        final String systemPropResDir = System.getProperty("resources_dir");

        if (systemPropResDir == null) {
            return "resources/test/";
        }

        return systemPropResDir;
    }

}
