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
 * Class AvatarSpecification
 * Avatar specification
 * Creation: 13/12/2010
 * @version 1.0 13/12/2010
 * @author Ludovic APVRILLE
 * @see
 */


package avatartranslator.directsimulation;

import java.util.*;

import avatartranslator.*;
import myutil.*;


public class AvatarSpecificationSimulation  {
	private static int MAX_TRANSACTION_IN_A_ROW = 1000; 
	
	public final static int OTHER = 0;
	public final static int STOPPED = 1;
	public final static int RUNNING = 2;
	public final static int TERMINATED = 3;
	
	private int mode;
	
    private AvatarSpecification avspec;
	private AvatarSimulationInteraction asi;
	private long clockValue;
	private LinkedList<AvatarSimulationBlock> blocks;
	private LinkedList<AvatarActionOnSignal> asynchronousMessages;
	private LinkedList<AvatarSimulationPendingTransaction> pendingTransactions;
	private LinkedList<AvatarSimulationTransaction> allTransactions;
	
	private boolean stopped = false;
	private boolean killed = false;
	
	private int nbOfCommands = -1; // means: until it blocks
	
    public AvatarSpecificationSimulation(AvatarSpecification _avspec, AvatarSimulationInteraction _asi) {
        avspec = _avspec;
		asi = _asi;
    }
	
	public LinkedList<AvatarSimulationBlock> getSimulationBlocks() {
		return blocks;
	}
	
	public LinkedList<AvatarSimulationTransaction> getAllTransactions() {
		return allTransactions;
	}
	
	public long getClockValue() {
		return clockValue;
	}
	
	public void initialize() {
		
		// Remove composite states
		avspec.removeCompositeStates();
		
		// Remove timers
		avspec.removeTimers();
		
		reset();
	}
	
	public void reset() {
		killed = false;
		unsetNbOfCommands();
		
		// Reinit clock
		clockValue = 0;
		
		// Reinit simulation 
		AvatarSimulationTransaction.reinit();
		
		// Create all simulation blocks
		blocks = new LinkedList<AvatarSimulationBlock>();
		for(AvatarBlock block: avspec.getListOfBlocks()) {
			AvatarSimulationBlock asb = new AvatarSimulationBlock(block);
			blocks.add(asb);
		}
		
		// Create all simulation asynchronous channels
		asynchronousMessages = new LinkedList<AvatarActionOnSignal>();
		
		// Create the structure for pending and executed transactions
		pendingTransactions = new LinkedList<AvatarSimulationPendingTransaction>();
		allTransactions = new LinkedList<AvatarSimulationTransaction>();
	}
	
	public boolean isInDeadlock() {
		return true;
	}
	
	public void runSimulation() {
		setMode(RUNNING);
		int index[];
		LinkedList<AvatarSimulationPendingTransaction> selectedTransactions;
		
		boolean go = true;
		stopped = true;
		
		if (stopped && go) {
			setMode(STOPPED);
			TraceManager.addDev("Simulation waiting for run");
			waitForUnstopped();
			if (go) {
				setMode(RUNNING);
			}
		}
		
		TraceManager.addDev("Simulation started at time: " + clockValue);
		
		while((go == true) && !killed) {
			while((go == true) && !stopped && !killed) {
				gatherPendingTransactions();
				
				if (pendingTransactions.size() == 0) {
					go = false;
					TraceManager.addDev("No more pending transactions");
				} else {
					selectedTransactions = selectTransactions(pendingTransactions);
					
					if (selectedTransactions.size() == 0) {
						go = false;
						TraceManager.addDev("Deadlock: no transaction can be selected");
					} else {
						TraceManager.addDev("* * * * * Nb of selected transactions: " + selectedTransactions.size());
						go = performSelectedTransactions(selectedTransactions);
						TraceManager.addDev("NbOfcommands=" + nbOfCommands);
						nbOfCommands --;
						if (nbOfCommands == 0) {
							stopSimulation();
							stopSimulation(go);
						}
					}
					
				}
			}
			if (stopped && go && !killed) {
				stopSimulation(go);
				
			}
		}
		setMode(TERMINATED);
		TraceManager.addDev("Simulation finished at time: " + clockValue + "\n--------------------------------------");
		
		printExecutedTransactions();
	}
	
	public void gatherPendingTransactions() {
		pendingTransactions.clear();
		// Gather all pending transactions from blocks
		for(AvatarSimulationBlock asb: blocks) {
			pendingTransactions.addAll(asb.getPendingTransactions(allTransactions, clockValue, MAX_TRANSACTION_IN_A_ROW));
		}
	}
	
	public LinkedList<AvatarSimulationPendingTransaction> selectTransactions(LinkedList<AvatarSimulationPendingTransaction> _pendingTransactions) {
		LinkedList<AvatarSimulationPendingTransaction> ll = new LinkedList<AvatarSimulationPendingTransaction>();
		
		// Put in ll the first possible logical transaction which is met
		// andom select the first index
		int decIndex = (int)(Math.floor(Math.random()*_pendingTransactions.size()));
		
		AvatarSimulationPendingTransaction currentTransaction;
		
		
		// First consider logical transactions only
		for(int i=0; i<_pendingTransactions.size(); i++) {
			currentTransaction = _pendingTransactions.get((i+decIndex)%_pendingTransactions.size());
			
			if (currentTransaction.elementToExecute instanceof AvatarTransition) {
				AvatarTransition tr = (AvatarTransition)(currentTransaction.elementToExecute);
				if (!tr.hasDelay()) {
					ll.add(currentTransaction);
				}
				break;
			}
		}
		
		// Then consider timed transactions
		
		return ll;
	}
	
	public boolean performSelectedTransactions(LinkedList<AvatarSimulationPendingTransaction> _pendingTransactions) {
		if (_pendingTransactions.size() == 1) {
			_pendingTransactions.get(0).asb.runSoloPendingTransaction(_pendingTransactions.get(0), allTransactions, clockValue, MAX_TRANSACTION_IN_A_ROW);
			return true;
		} else if (_pendingTransactions.size() == 1) { // synchro
			//Not yet handled
			return false;
		} else {
			 // error!
			 return false;
		}
	}
	
	public void printExecutedTransactions() {
		for(AvatarSimulationTransaction ast: allTransactions) {
			TraceManager.addDev(ast.toString() + "\n");
		}
	}
	
	
	public synchronized void waitForUnstopped() {
		while(stopped && !killed) {
			try {
				wait();
			} catch (Exception e) {
			}
		}
	}
	
	public synchronized void unstop() {
		stopped = false;
		notifyAll();
	}
	
	public synchronized void stopSimulation() {
		TraceManager.addDev("Ask for simulation stop");
		notifyAll();
		stopped = true;
	}
	
	public synchronized void killSimulation() {
		TraceManager.addDev("Simulation killed");
		killed = true;
		stopped = true;
		notifyAll();
	}
	
	public void setMode(int _mode) {
		mode = _mode;
		
		if (mode == STOPPED) {
			unsetNbOfCommands();
		}
		
		if (asi != null) {
			asi.setMode(mode);
		}
	}
	
	public void setNbOfCommands(int _nbOfCommands) {
		nbOfCommands = _nbOfCommands;
	}
	
	public void unsetNbOfCommands() {
		nbOfCommands = -1;
	}
	
	public void stopSimulation(boolean _go) {
		setMode(STOPPED);
		unsetNbOfCommands();
		TraceManager.addDev("Simulation stopped at time: " + clockValue + "\n--------------------------------------");
		waitForUnstopped();
		if (_go && !killed) {
			setMode(RUNNING);
		}
	}
	
	

}