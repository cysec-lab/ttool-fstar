package ui.GraphLatencyTainting;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Vector;

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

    private static final String simulationTracePath = "/ui/graphLatencyAnalysis/input/tainting.xml";
    private static final String modelPath = "/ui/graphLatencyAnalysis/input/GraphLatencyAnalysisTainting.xml";

    private static final String mappingDiagName = "Architecture";
    private Vector<SimulationTransaction> transFile1;
    private Vector<String> dropDown;

    private static final String t1 = "f__send:writechannel:data__47";
    private static final String t2 = "f__compute:readchannel:datakk__37";
    private static String task1;
    private static String task2;
    private static DirectedGraphTranslator dgt;

    private static Object[][] allLatencies, minMaxArray, taskHWByRowDetails, detailedLatency;
    private JFrameLatencyDetailedAnalysis latencyDetailedAnalysis;
    private latencyDetailedAnalysisMain latencyDetailedAnalysisMain;

    @Before
    public void GraphLatencyAnalysis() {

        mainGUI.openProjectFromFile(new File(getBaseResourcesDir() + modelPath));
        // mainGUI.openProjectFromFile(new File( modelPath));

        final TMLArchiPanel panel = findArchiPanel(mappingDiagName);

        if (panel == null) {
            System.out.println("NULL Panel");
        } else {
            System.out.println("Non NULL Panel");
        }

        mainGUI.checkModelingSyntax(panel, true);
        SimulationTrace file2 = new SimulationTrace("tainting", 6, simulationTracePath);
        latencyDetailedAnalysisMain = new latencyDetailedAnalysisMain(3, mainGUI, file2, false, false, 3);

        latencyDetailedAnalysisMain.latencyDetailedAnalysis(file2, panel, false, false, mainGUI);

        latencyDetailedAnalysis = latencyDetailedAnalysisMain.getLatencyDetailedAnalysis();

        if (latencyDetailedAnalysis != null) {
            latencyDetailedAnalysis.setVisible(false);
            if (latencyDetailedAnalysis.graphStatus() == Thread.State.TERMINATED) {
                dgt = latencyDetailedAnalysis.getDgraph();
            }
            while (latencyDetailedAnalysis.graphStatus() != Thread.State.TERMINATED) {
                dgt = latencyDetailedAnalysis.getDgraph();
            }
        }

    }

    @Test
    public void parseFile() {

        assertNotNull(latencyDetailedAnalysis);

        int graphsize = dgt.getGraphsize();

        assertTrue(graphsize == 34);

        dropDown = latencyDetailedAnalysis.getCheckedTransactions();

        assertTrue(dropDown.size() == 2);

        transFile1 = latencyDetailedAnalysisMain.getLatencyDetailedAnalysis().parseFile(new File(getBaseResourcesDir() + simulationTracePath));

        // transFile1 =
        // latencyDetailedAnalysisMain.getLatencyDetailedAnalysis().parseFile(new File(
        // simulationTracePath));

        assertTrue(transFile1.size() > 0);

        int i = dropDown.indexOf(t1);
        int j = dropDown.indexOf(t2);

        task1 = dropDown.get(i);
        task2 = dropDown.get(j);

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
        assertTrue(detailedLatency.length == 6);

    }

}