/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class TMLCPath
 * Notion of Path. To be used to analyze the correctness of paths in the model
 * Creation: 7/03/2014
 * @version 1.0 7/03/2014
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlcompd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

import tmltranslator.*;

public class TMLCPath  {
	
	
	public ArrayList<TMLCCompositePort> cports;
	public ArrayList<TMLCPrimitivePort> producerPorts;
	public ArrayList<TMLCPrimitivePort> consumerPorts;
	public ArrayList<TMLCFork> forks; 
	public ArrayList<TMLCJoin> joins;
	
	private int errorNumber; 
	
	private String[] errors = {"Fork and Join operators in the same path",
	"More than one producer in a path with a fork"};
	
	public TMLCPath() {
		cports = new ArrayList<TMLCCompositePort>();
		producerPorts = new ArrayList<TMLCPrimitivePort>();
		consumerPorts = new ArrayList<TMLCPrimitivePort>();
		forks = new ArrayList<TMLCFork>();
		joins = new ArrayList<TMLCJoin>();
	}
	
	public void addComponent(TGComponent _tgc) {
		if (_tgc instanceof TMLCCompositePort) {
			cports.add((TMLCCompositePort)_tgc);
		}
		
		if (_tgc instanceof TMLCPrimitivePort) {
			TMLCPrimitivePort p = (TMLCPrimitivePort)_tgc;
			if (p.isOrigin()) {
				producerPorts.add(p);
			} else {
				consumerPorts.add(p);
			}
		}
		
		if (_tgc instanceof TMLCFork) {
			forks.add((TMLCFork)_tgc);
		}
		
		if (_tgc instanceof TMLCJoin) {
			joins.add((TMLCJoin)_tgc);
		}
		
		
	}
	
	public void mergeWith(TMLCPath _path) {
		cports.addAll(_path.cports);
	}
	
	
	public boolean hasError() {
		return (errorNumber != -1);
	}
	
	
	public void checkRules() {
		errorNumber = -1;
		
		//rule0: fork or join, but not both
		if ((forks.size() > 0) && (joins.size() >0)) {
			errorNumber = 0;
		}
		
		// If fork: must have only one producer
		if ((forks.size() > 0) && (producerPorts.size() >1)) {
			errorNumber = 1;
		}
	}
	 
	

	 
}
