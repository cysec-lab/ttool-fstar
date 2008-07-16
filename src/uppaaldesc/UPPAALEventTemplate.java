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
 * Class UPPAALEventTemplate
 * Creation: 07/11/2006
 * @version 1.0 07/11/2006
 * @author Ludovic APVRILLE
 * @see
 */

package uppaaldesc;

import java.awt.Point;

import tmltranslator.*;


public class UPPAALEventTemplate extends  UPPAALTemplate{

    public UPPAALEventTemplate(String name, TMLEvent event, String defaultSizeValue) {
           super();
           
           int i;
           setName(name);
           if (event.isInfinite()) {
              declaration = "const int maxR = " + defaultSizeValue + ";\nint size = 0;\nint head = 0;\nint tail = 0;\n";
           } else {
             declaration = "const int maxR = " + event.getMaxSize() + ";\nint size = 0;\nint head = 0;\nint tail = 0;\n";
           }
           for(i=0;i<event.getNbOfParams(); i++) {
             declaration += event.getType(i).toString() + " listR" + i + "[maxR];\n\n";
           }

           // Function code: enqueueR
           declaration += "void enqueueE(){\n";
           for(i=0;i<event.getNbOfParams(); i++) {
            declaration += "  listR" + i + "[tail] = eventTail" + i + "__" + event.getName() + ";\n";
           }
           declaration += "  tail = (tail+1)%maxR;\n  size ++;\n}\n\n";

           // Function code: dequeueR
           declaration += "void dequeueE(){\n";
           for(i=0;i<event.getNbOfParams(); i++) {
             declaration += "  eventHead" + i + "__" + event.getName() + " = listR" + i + "[head];\n";
           }
           declaration += "  head = (head+1)%maxR;\n  size --;\n}\n\n";

           // Main state
           initLocation = new UPPAALLocation();
           initLocation.idPoint = new Point(-64, -80);
           initLocation.namePoint = new Point(-80, -56);
           initLocation.name = "main_state";
           locations.add(initLocation);

           // Transition for getting event (infinite + finite)
           UPPAALTransition tr = new UPPAALTransition();
           tr.sourceLoc = initLocation;
           tr.destinationLoc = initLocation;
           tr.guard = "size < maxR";
           tr.guardPoint = new Point(-336, -112);
           tr.synchronization = "eventSend__" +  event.getName() + "?";
           tr.synchronizationPoint = new Point(-280, -88);
           tr.assignment = "enqueueE()";
           tr.assignmentPoint = new Point(-304, -64);
           tr.points.add(new Point(-208, -232));
           tr.points.add(new Point(-208, 104));
           transitions.add(tr);
           
           // Transition for releasing event (infinite + finite)
           tr = new UPPAALTransition();
           tr.sourceLoc = initLocation;
           tr.destinationLoc = initLocation;
           tr.guard = "size>0";
           tr.guardPoint = new Point(16, -112);
           tr.synchronization = "eventNotify__" + event.getName() + "!";
           tr.synchronizationPoint = new Point(40, -88);
           tr.assignment = "dequeueE()";
           tr.assignmentPoint = new Point(24, -72);
           tr.points.add(new Point(64, 80));
           tr.points.add(new Point(64, -232));
           transitions.add(tr);

           // Transition for getting event (finite and non-blocking)
           if (!event.isInfinite() && !event.isBlocking()) {
             tr = new UPPAALTransition();
             tr.sourceLoc = initLocation;
             tr.destinationLoc = initLocation;
             tr.guard = "size == maxR";
             tr.guardPoint = new Point(-120, -256);
             tr.synchronization = "eventSend__" +  event.getName() + "?";
             tr.synchronizationPoint = new Point(-104, -232);
             tr.assignment = "dequeueE(),\nenqueueE()";
             tr.assignmentPoint = new Point(-88, -216);
             tr.points.add(new Point(-176, -232));
             tr.points.add(new Point(0, -232));
             transitions.add(tr);
           }
           
           // Notified
           tr = new UPPAALTransition();
           tr.sourceLoc = initLocation;
           tr.destinationLoc = initLocation;
           tr.synchronization = "eventNotified__" + event.getName() + "!";
           tr.synchronizationPoint = new Point(-104, 56);
           tr.assignment = "notified__" + event.getName() + " = (size != 0)" ;
           tr.assignmentPoint = new Point(-96, 72);
           tr.points.add(new Point(8, 88));
           tr.points.add(new Point(-152, 96));
           transitions.add(tr);

    }


}
