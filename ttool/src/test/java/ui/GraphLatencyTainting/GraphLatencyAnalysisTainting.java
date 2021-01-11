package ui.GraphLatencyTainting;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import ui.AbstractUITest;
import ui.SimulationTrace;
import ui.TMLArchiPanel;
import ui.interactivesimulation.SimulationTransaction;
import ui.simulationtraceanalysis.DirectedGraphTranslator;
import ui.simulationtraceanalysis.JFrameLatencyDetailedAnalysis;
import ui.simulationtraceanalysis.latencyDetailedAnalysisMain;

public class GraphLatencyAnalysisTainting extends AbstractUITest {

    // private static final String INPUT_PATH = "/ui/graphLatencyAnalysis/input";

    private static final String INPUT_PATH = "/ui/graphLatencyAnalysis/input";
    private static final String simulationTracePath = INPUT_PATH + "/tainting.xml";

    private static final String modelPath = INPUT_PATH + "/GraphLatencyAnalysisTainting.xml";

    private static final String mappingDiagName = "Architecture";
    private Vector<SimulationTransaction> transFile1;
    private Vector<String> dropDown;

    private static final int operator1ID = 47;
    private static final int operator2ID = 37;
    private static String task1;
    private static String task2;
    private static DirectedGraphTranslator dgt;

    private static Object[][] allLatencies, minMaxArray, taskHWByRowDetails, detailedLatency;
    private JFrameLatencyDetailedAnalysis latencyDetailedAnalysis;
    private latencyDetailedAnalysisMain latencyDetailedAnalysisMain;
    private HashMap<String, Integer> checkedDropDown = new HashMap<String, Integer>();

    @Before
    public void GraphLatencyAnalysis() throws InterruptedException {

        mainGUI.openProjectFromFile(new File(getBaseResourcesDir() + modelPath));

        final TMLArchiPanel panel = findArchiPanel(mappingDiagName);

        if (panel == null) {
            System.out.println("NULL Panel");
        } else {
            System.out.println("Non NULL Panel");
        }

        mainGUI.checkModelingSyntax(panel, true);
        SimulationTrace file2 = new SimulationTrace("tainting", 6, simulationTracePath);
        latencyDetailedAnalysisMain = new latencyDetailedAnalysisMain(3, mainGUI, file2, false, false, 3);
        latencyDetailedAnalysisMain.getTc().setMainGUI(mainGUI);
        latencyDetailedAnalysisMain.latencyDetailedAnalysis(file2, panel, false, false, mainGUI);

        latencyDetailedAnalysis = latencyDetailedAnalysisMain.getLatencyDetailedAnalysis();

        if (latencyDetailedAnalysis != null) {
            latencyDetailedAnalysis.setVisible(false);

            try {
                latencyDetailedAnalysis.getT().join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            dgt = latencyDetailedAnalysis.getDgraph();

        }

    }

    @Test
    public void parseFile() {

        assertNotNull(latencyDetailedAnalysis);

        int graphsize = dgt.getGraphsize();

        assertTrue(graphsize == 34);

        checkedDropDown = latencyDetailedAnalysis.getCheckedT();

        assertTrue(checkedDropDown.size() == 2);

        transFile1 = latencyDetailedAnalysisMain.getLatencyDetailedAnalysis().parseFile(new File(getBaseResourcesDir() + simulationTracePath));

        assertTrue(transFile1.size() > 0);

        for (Entry<String, Integer> cT : checkedDropDown.entrySet()) {

            int id = cT.getValue();
            String taskName = cT.getKey();
            if (id == operator1ID) {
                task1 = taskName;

            } else if (id == operator2ID) {
                task2 = taskName;

            }
        }

        allLatencies = dgt.latencyDetailedAnalysis(task1, task2, transFile1, true, false);

        assertTrue(allLatencies.length == 1);

        assertTrue(allLatencies[0][4] == Integer.valueOf(105));

        minMaxArray = dgt.latencyMinMaxAnalysis(task1, task2, transFile1);
        dgt.getRowDetailsMinMax(1);
        taskHWByRowDetails = dgt.getTasksByRowMinMax(0);

        assertTrue(minMaxArray.length > 0);

        assertTrue(taskHWByRowDetails.length == 12);
        taskHWByRowDetails = dgt.getTaskHWByRowDetailsMinMax(0);
        assertTrue(taskHWByRowDetails.length == 6);

        detailedLatency = dgt.getTaskByRowDetails(0);
        assertTrue(detailedLatency.length == 12);

        detailedLatency = dgt.getTaskHWByRowDetails(0);
        assertTrue(detailedLatency.length == 3);

    }

}