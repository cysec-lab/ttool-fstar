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


package avatartranslator.modelchecker;

import avatartranslator.*;
import myutil.BoolExpressionEvaluator;
import myutil.Conversion;
import myutil.IntExpressionEvaluator;
import myutil.TraceManager;
import rationals.properties.isEmpty;

import java.util.*;


/**
 * Class AvatarModelChecker
 * Avatar Model Checker
 * Creation: 31/05/2016
 *
 * @author Ludovic APVRILLE, Alessandro TEMPIA CALVINO
 * @version 1.0 31/05/2016
 */
public class AvatarModelChecker implements Runnable, myutil.Graph {
    private final static int DEFAULT_NB_OF_THREADS = 12;
    private final static int SLEEP_DURATION = 500;

    private AvatarSpecification initialSpec;
    private AvatarSpecification spec;
    private int nbOfThreads = DEFAULT_NB_OF_THREADS;
    private int nbOfCurrentComputations;
    private boolean stoppedBeforeEnd;
    private boolean stoppedConditionReached;

    // ReachabilityGraph
    private Map<Integer, SpecificationState> states;
    private Map<Long, SpecificationState> statesByID;
    private List<SpecificationState> pendingStates;
    private Map<Integer, SpecificationState> safetyLeadStates;
    private Map<AvatarTransition, Set<AvatarTransition>> signalRelation;
    //private List<SpecificationLink> links;
    private int nbOfLinks;
    private long stateID = 0;
    private int blockValues;
    private boolean freeIntermediateStateCoding;


    // Options
    private boolean ignoreEmptyTransitions;
    private boolean ignoreConcurrenceBetweenInternalActions;
    private boolean ignoreInternalStates;

    // RG
    private boolean computeRG;

    // Reachability
    private boolean studyReachability;
    private ArrayList<SpecificationReachability> reachabilities;
    private int nbOfRemainingReachabilities;

    // Dealocks
    private boolean checkNoDeadlocks;
    private boolean deadlockStop;
    private int nbOfDeadlocks;

    // Safety and Liveness
    private boolean studySafety;
    private boolean studyLiveness;
    private boolean propertyDone;
    private ArrayList<SafetyProperty> livenesses;
    private ArrayList<SafetyProperty> safeties;
    private SafetyProperty safety;
    
    // Re-Initialization
    private boolean studyReinit;
    private SpecificationReinit initState;
    
    //RG limits
    private boolean stateLimitRG;
    private boolean timeLimitRG;
    private boolean timeLimitReached;
    private boolean limitReached;
    private long stateLimit;
    private long timeLimit;
    

    public AvatarModelChecker(AvatarSpecification _spec) {
        if (_spec != null) {
            initialSpec = _spec;
            //TraceManager.addDev("Before clone:\n" + spec);
            initialSpec.removeLibraryFunctionCalls();
            initialSpec.removeCompositeStates();
            //TraceManager.addDev("Before clone:\n" + initialSpec);
            spec = initialSpec.advancedClone();
            //TraceManager.addDev("After clone:\n" + spec);
        }
        ignoreEmptyTransitions = true;
        ignoreConcurrenceBetweenInternalActions = true;
        ignoreInternalStates = true;
        checkNoDeadlocks = false;
        deadlockStop = false;
        studyReachability = false;
        studyReinit = false;
        computeRG = false;
        stateLimitRG = false; //No state limit in RG computation
        timeLimitRG = false;
        stateLimit = Integer.MAX_VALUE;
        timeLimit = 500;
        freeIntermediateStateCoding = true;
    }

    public AvatarSpecification getInitialSpec() {
        return initialSpec;
    }

    public int getWeightOfTransition(int originState, int destinationState) {
        if (statesByID == null) {
            return 0;
        }
        SpecificationState st = statesByID.get(originState);
        if (st == null) {
            return 0;
        }
        return st.getWeightOfTransitionTo(destinationState);

    }

    public AvatarSpecification getReworkedAvatarSpecification() {
        return spec;
    }

    public int getNbOfStates() {
        if (states == null) {
            return 0;
        }
        return states.size();
    }

    public int getNbOfLinks() {
        //return links.size();
        return nbOfLinks;
    }

    public int getNbOfPendingStates() {
        return pendingStates.size();
    }

    /*public synchronized long getStateID() {
        long tmp = stateID;
        stateID ++;
        return tmp;
	}*/

    public void setFreeIntermediateStateCoding(boolean _b) {
        freeIntermediateStateCoding = _b;
    }

    public void setIgnoreEmptyTransitions(boolean _b) {
        ignoreEmptyTransitions = _b;
    }

    public void setIgnoreConcurrenceBetweenInternalActions(boolean _b) {
        ignoreConcurrenceBetweenInternalActions = _b;
    }

    public void setIgnoreInternalStates(boolean _b) {
        TraceManager.addDev("ignore internal state?" + ignoreInternalStates);
        ignoreInternalStates = _b;
    }
    
    public void setCheckNoDeadlocks(boolean _checkNoDeadlocks) {
        checkNoDeadlocks = _checkNoDeadlocks;
        deadlockStop = false;
    }

    public int setLivenessOfSelected() {
        livenesses = new ArrayList<SafetyProperty>();
        for (AvatarBlock block : spec.getListOfBlocks()) {
            for (AvatarStateMachineElement elt : block.getStateMachine().getListOfElements()) {
                //TraceManager.addDev("null elt in state machine of block=" + block.getName());
                //if (elt.canBeVerified() && elt.isChecked()) {
                if (elt.isChecked()) {
                    SafetyProperty sp = new SafetyProperty(block, elt, SafetyProperty.ALLTRACES_ONESTATE);
                    livenesses.add(sp);
                }
            }
        }
        studyLiveness = true;
        return livenesses.size();
    }
    
    public int setLivenessOfAllStates() {        
        livenesses = new ArrayList<SafetyProperty>();
        for (AvatarBlock block : spec.getListOfBlocks()) {
            for (AvatarStateMachineElement elt : block.getStateMachine().getListOfElements()) {
                if (elt.isCheckable()) {
                    SafetyProperty sp = new SafetyProperty(block, elt, SafetyProperty.ALLTRACES_ONESTATE);
                    livenesses.add(sp);
                }
            }
        }
        studyLiveness = true;
        return livenesses.size();
    }
    
    public ArrayList<SafetyProperty> getLivenesses() {
        return livenesses;
    }

    public int setReachabilityOfSelected() {
        reachabilities = new ArrayList<SpecificationReachability>();
        for (AvatarBlock block : spec.getListOfBlocks()) {
            for (AvatarStateMachineElement elt : block.getStateMachine().getListOfElements()) {
                //TraceManager.addDev("null elt in state machine of block=" + block.getName());
                if (elt.isChecked()) {
                    SpecificationReachability reach = new SpecificationReachability(elt, block);
                    reachabilities.add(reach);
                }
            }
        }
        nbOfRemainingReachabilities = reachabilities.size();
        studyReachability = true;
        return nbOfRemainingReachabilities;
    }

    public int setReachabilityOfAllStates() {
        reachabilities = new ArrayList<SpecificationReachability>();
        for (AvatarBlock block : spec.getListOfBlocks()) {
            for (AvatarStateMachineElement elt : block.getStateMachine().getListOfElements()) {
                if (elt.isCheckable()) {
                    SpecificationReachability reach = new SpecificationReachability(elt, block);
                    reachabilities.add(reach);
                }
            }
        }
        nbOfRemainingReachabilities = reachabilities.size();
        studyReachability = true;
        return nbOfRemainingReachabilities;
    }

    public ArrayList<SpecificationReachability> getReachabilities() {
        return reachabilities;
    }

    public int getNbOfRemainingReachabilities() {
        return nbOfRemainingReachabilities;
    }

    public int getNbOfReachabilities() {
        if (reachabilities == null) {
            return -1;
        }
        return reachabilities.size() - nbOfRemainingReachabilities;
    }


    public int getNbOfDeadlocks() {
        return nbOfDeadlocks;
    }


    public int setSafetyAnalysis() {
        if (safeties == null) {
            safeties = new ArrayList<SafetyProperty>();
        }
        for (String property : spec.getSafetyPragmas()) {
            SafetyProperty sp = new SafetyProperty(property, spec);
            if (!sp.hasError()) {
                safeties.add(sp);
            }
        }
        studySafety = safeties.size() > 0;
        return safeties.size();
    }


    public boolean addSafety(String pragma) {
        if (safeties == null) {
            safeties = new ArrayList<SafetyProperty>();
        }
        SafetyProperty sp = new SafetyProperty(pragma, spec);
        if (!sp.hasError()) {
            safeties.add(sp);
            studySafety = true;
            return true;
        }
        return false;
    }
    
    public ArrayList<SafetyProperty> getSafeties() {
        return safeties;
    }
    

    public void setReinitAnalysis(boolean studyReinit) {
        this.studyReinit = studyReinit;
    }
    
    
    public boolean getReinitResult() {
        if (studyReinit && initState != null) {
            return initState.getResult();
        }
        return false;
    }

    public void setComputeRG(boolean _rg) {
        computeRG = _rg;
    }
    
    public void setStateLimit(boolean _stateLimitRG) {
    	stateLimitRG = _stateLimitRG; //_stateLimitRG;
    }
    
    public void setStateLimitValue(long _stateLimit) {
    	stateLimit = _stateLimit;
    }
    
    public void setTimeLimit(boolean _timeLimitRG) {
        timeLimitRG = _timeLimitRG; //_stateLimitRG;
    }
    
    public void setTimeLimitValue(long _timeLimit) {
        timeLimit = _timeLimit;
    }

    /*private synchronized boolean startMC() {
	
      }*/

    public boolean startModelCheckingProperties() {
        boolean studyS, studyL, studyR, studyRI, genRG;
        boolean emptyTr;
        long deadlocks = 0;
        
        if (spec == null) {
            return false;
        }

        if ((studyLiveness && livenesses == null) || (studySafety && safeties == null) || (studyReachability && reachabilities == null)) {
            return false;
        }
        
        initModelChecking();
        
        stateLimitRG = false;
        timeLimitRG = false;
        emptyTr = ignoreEmptyTransitions;
        studyR = studyReachability;
        studyL = studyLiveness;
        studyS = studySafety;
        studyRI = studyReinit;
        genRG = computeRG;
        
        //then compute livenesses
        computeRG = false;
        propertyDone = false;
        studySafety = false;
        studyLiveness = false;
        studyReachability = false;
        studyReinit = false;

        if (studyL) {
            studySafety = true;
            for (SafetyProperty sp : livenesses) {
                safety = sp;
                startModelChecking(nbOfThreads);
                deadlocks += nbOfDeadlocks;
                resetModelChecking();
                safety.setComputed();
            }
            studySafety = false;
        }
        
        if (studyS) {
            studySafety = true;
            for (SafetyProperty sp : safeties) {
                safety = sp;
                if (safety.safetyType == SafetyProperty.LEADS_TO) {
                    // prepare to save second pass states
                    safetyLeadStates = Collections.synchronizedMap(new HashMap<Integer, SpecificationState>());
                    ignoreEmptyTransitions = false;
                }
                startModelChecking(nbOfThreads);
                if (safety.safetyType == SafetyProperty.LEADS_TO) {
                    // second pass
                    safety.initLead();
                    ignoreEmptyTransitions = emptyTr;
                    for (SpecificationState state : safetyLeadStates.values()) {
                        deadlocks += nbOfDeadlocks;
                        resetModelChecking();
                        startModelChecking(state, nbOfThreads);
                        if (safety.result == false) {
                            break;
                        }
                    }
                    System.out.println("Dimensions of lead states to elaborate: " + safetyLeadStates.size());
                    safetyLeadStates = null;
                }
                safety.setComputed();
                deadlocks += nbOfDeadlocks;
                resetModelChecking();
            }
            studySafety = false;
        }
        
        if (studyRI) {
            studyReinit = true;
            startModelChecking(nbOfThreads);
            deadlocks += nbOfDeadlocks;
            resetModelChecking();
            studyReinit = false;
        }
        
        if (studyR || genRG) {
            if (genRG) {
                deadlocks = 0;
            }
            studyReachability = studyR;
            computeRG = genRG;
            startModelChecking(nbOfThreads);
            deadlocks += nbOfDeadlocks;
            resetModelChecking();
            studyReachability = false;
            computeRG = false;
        }
        
        if (genRG) {
            nbOfDeadlocks = (int) deadlocks;
        } else if (checkNoDeadlocks) {
            //If a complete study with reachability graph generation has been executed,
            //there is no need to study deadlocks again
            if (deadlocks == 0) {
                deadlockStop = true;
                startModelChecking(nbOfThreads);
            } else {
                nbOfDeadlocks = 1;
            }
        }
        
        computeRG = genRG;
        studyLiveness = studyL;
        studySafety = studyS;
        studyReachability = studyR;
        studyReinit = studyRI;
        
        TraceManager.addDev("Model checking done");
        return true;
    }

    public void startModelChecking() {
        TraceManager.addDev("String model checking");

        if (spec == null) {
            return;
        }
        
        initModelChecking();
        
        startModelChecking(nbOfThreads);
        TraceManager.addDev("Model checking done");
    }

    public boolean hasBeenStoppedBeforeCompletion() {
        return stoppedBeforeEnd;
    }


    public void startModelChecking(int _nbOfThreads) {
        nbOfThreads = _nbOfThreads;

        // Init data stuctures
        states = Collections.synchronizedMap(new HashMap<Integer, SpecificationState>());
        statesByID = Collections.synchronizedMap(new HashMap<Long, SpecificationState>());
        pendingStates = Collections.synchronizedList(new LinkedList<SpecificationState>());
        //links = Collections.synchronizedList(new ArrayList<SpecificationLink>());
        nbOfLinks = 0;

        // Check stop conditions
        if (mustStop()) {
            return;
        }

        // Compute initial state
        SpecificationState initialState = new SpecificationState();
        //initialState.setInit(spec, ignoreEmptyTransitions);
        initialState.setInit(spec, false);
        
        for (AvatarBlock block : spec.getListOfBlocks()) {
            AvatarStateMachineElement asme = block.getStateMachine().getStartState();
            checkElement(asme, initialState);
            if (studySafety && safety.propertyType == SafetyProperty.BLOCK_STATE) {
                initialState.property |= safety.getSolverResult(initialState, asme);
            }
        }
        if (studySafety && safety.propertyType == SafetyProperty.BLOCK_STATE) {
                if (safety.safetyType == SafetyProperty.ALLTRACES_ALLSTATES || safety.safetyType == SafetyProperty.ONETRACE_ALLSTATES) {
                    initialState.property = !initialState.property;
                }
        }
        
        if (ignoreEmptyTransitions) {
            handleNonEmptyUniqueTransition(initialState);
        }
//        
//        prepareTransitionsOfState(initialState);
        blockValues = initialState.getBlockValues();
        initialState.distance = 0;

        //TraceManager.addDev("initialState=" + initialState.toString() + "\n nbOfTransitions" + initialState.transitions.size());
        initialState.computeHash(blockValues);
        addState(initialState);
        
        if (studySafety) {
            if (safety.propertyType == SafetyProperty.BOOL_EXPR) {
                initialState.property = evaluateSafetyOfProperty(initialState, null, false);
            }
            actionOnProperty(initialState, 0, null, null);
        } else {
            pendingStates.add(initialState);
        }
        
        if (studyReinit) {
            initState = new SpecificationReinit(initialState);
        }

        //states.put(initialState.hashValue, initialState);
        //statesByID.put(initialState.id, initialState);
        nbOfCurrentComputations = 0;

        if (timeLimitRG) {
            computeAllStatesTime();
        } else {
            computeAllStates();
        }

        // All done
    }
    
    public void startModelChecking(SpecificationState initialState, int _nbOfThreads) {
        nbOfThreads = _nbOfThreads;
        stateID = 0;
        nbOfDeadlocks = 0;
        
        // Init data stuctures
        states = Collections.synchronizedMap(new HashMap<Integer, SpecificationState>());
        statesByID = Collections.synchronizedMap(new HashMap<Long, SpecificationState>());
        pendingStates = Collections.synchronizedList(new LinkedList<SpecificationState>());
        //links = Collections.synchronizedList(new ArrayList<SpecificationLink>());
        nbOfLinks = 0;

        // Check stop conditions
        if (mustStop()) {
            return;
        }
        
        int cpt = 0;
        
        initialState.property = safety.getSolverResult(initialState);

        if (studySafety && safety.propertyType == SafetyProperty.BLOCK_STATE) {
                if (safety.safetyType == SafetyProperty.ALLTRACES_ALLSTATES || safety.safetyType == SafetyProperty.ONETRACE_ALLSTATES) {
                    initialState.property = !initialState.property;
                }
        }
        
        if (ignoreEmptyTransitions) {
            handleNonEmptyUniqueTransition(initialState);
        }

        // initialState's transitions and blocks must be already initialized
        blockValues = initialState.getBlockValues();
        initialState.distance = 0;

        //TraceManager.addDev("initialState=" + initialState.toString() + "\n nbOfTransitions" + initialState.transitions.size());
        initialState.computeHash(blockValues);
        addState(initialState);
        
        if (studySafety) {
            if (safety.propertyType == SafetyProperty.BOOL_EXPR) {
                initialState.property = evaluateSafetyOfProperty(initialState, null, false);
            }
            actionOnProperty(initialState, 0, null, null);
        } else {
            pendingStates.add(initialState);
        }
        //pendingStates.add(initialState);
           
        //states.put(initialState.hashValue, initialState);
        //statesByID.put(initialState.id, initialState);
        nbOfCurrentComputations = 0;

        computeAllStates();

        // All done
    }

    
    public void stopModelChecking() {
        emptyPendingStates();
        stoppedBeforeEnd = true;
        TraceManager.addDev("Model checking stopped");
    }
    
    
    private void initModelChecking() {
        stoppedBeforeEnd = false;
        limitReached = false;
        timeLimitReached = false;
        stateID = 0;
        nbOfDeadlocks = 0;

        // Remove timers, composite states, randoms
        TraceManager.addDev("Reworking Avatar specification");
        spec.removeElseGuards();
        spec.removeTimers();
        spec.removeRandoms();
        spec.removeFIFOs(4);
        spec.makeFullStates();

        if (ignoreEmptyTransitions) {
            spec.removeEmptyTransitions(!(studyReachability || studyLiveness || studySafety));
        }

        //TraceManager.addDev("Preparing Avatar specification :" + spec.toString());
        prepareStates();

        initExpressionSolvers();

        prepareTransitions();
        prepareBlocks();


        nbOfThreads = Runtime.getRuntime().availableProcessors();
        TraceManager.addDev("Starting the model checking with " + nbOfThreads + " threads");
        TraceManager.addDev("Ignore internal state:" + ignoreInternalStates);
    }
    
    
    private void resetModelChecking() {
        propertyDone = false;
        stoppedConditionReached = false;
        limitReached = false;
        timeLimitReached = false;
        stateID = 0;
        nbOfDeadlocks = 0;    
    }

    
    private void computeAllStates() {
        int i;
        Thread[] ts = new Thread[nbOfThreads];

        for (i = 0; i < nbOfThreads; i++) {
            ts[i] = new Thread(this);
            ts[i].start();
        }

        //TraceManager.addDev("Waiting for threads termination (nb of threads:" + nbOfThreads + ")");
        for (i = 0; i < nbOfThreads; i++) {
            try {
                ts[i].join();
            } catch (Exception e) {
                TraceManager.addDev("Join on avatar model checker thread failed for thread #" + i);
            }
        }

        TraceManager.addDev("Threads terminated");

        // Set to non reachable not computed elements
        if ((studyReachability) && (!stoppedBeforeEnd)) {
            for (SpecificationReachability re : reachabilities) {
                if (re.result == SpecificationPropertyPhase.NOTCOMPUTED) {
                    re.result = SpecificationPropertyPhase.NONSATISFIED;
                }
            }
        }

    }
    
    private void computeAllStatesTime() {
        int i;
        Thread[] ts = new Thread[nbOfThreads];
        TimerTask stopExecTask = new TimerTask() {
            public void run() {
                timeLimitReached = true;
            }
        };
        Timer timer = new Timer("Timer");

        for (i = 0; i < nbOfThreads; i++) {
            ts[i] = new Thread(this);
            ts[i].start();
        }
        
        timer.schedule(stopExecTask, timeLimit);        

        //TraceManager.addDev("Waiting for threads termination (nb of threads:" + nbOfThreads + ")");
        for (i = 0; i < nbOfThreads; i++) {
            try {
                ts[i].join();
            } catch (Exception e) {
                TraceManager.addDev("Join on avatar model checker thread failed for thread #" + i);
            }
        }
        
        timer.cancel();

        TraceManager.addDev("Threads terminated");

        if (timeLimitReached) {
            // Deadlock value is not reliable due to a immediate stop
            nbOfDeadlocks = 0;
            for (SpecificationState state : statesByID.values()) {
                if (state.isDeadlock()) {
                    nbOfDeadlocks++;
                }
            }
        }
        // Set to non reachable not computed elements
        if ((studyReachability) && (!stoppedBeforeEnd)) {
            for (SpecificationReachability re : reachabilities) {
                if (re.result == SpecificationPropertyPhase.NOTCOMPUTED) {
                    re.result = SpecificationPropertyPhase.NONSATISFIED;
                }
            }
        }

    }

    // MAIN LOOP
    /////////////////////////////////////////////
    public void run() {
        SpecificationState s;

        boolean go = true;
        while (go) {
            // Pickup a state
            if ((stoppedBeforeEnd) || (stoppedConditionReached)) {
                //TraceManager.addDev("In Avatar modelchecher thread: stopped before end or terminated");
                return;
            }
            
            if (timeLimitReached || propertyDone) {
                emptyPendingStates();
                return;
            }

            // Pickup a state
            s = pickupState();

            if ((stoppedBeforeEnd) || (stoppedConditionReached)) {
                return;
            }

            if (s == null) {
                // Terminate
                go = false;
            } else {
                // Handle one given state
                computeAllStatesFrom(s);
                // Release the computation
                releasePickupState(s);
            }
        }
    }

    private synchronized SpecificationState pickupState() {
        int size = pendingStates.size();
        while (size == 0) {
            if (nbOfCurrentComputations <= 0) {
                return null;
            } else {
                try {
                    wait(SLEEP_DURATION);
                } catch (Exception e) {
                }
                if ((stoppedBeforeEnd) || (stoppedConditionReached)) {
                    return null;
                }
                size = pendingStates.size();
            }
        }

        SpecificationState s = pendingStates.get(0);
        pendingStates.remove(s); //remove(0) has a race condition I insert elements also in 0
        nbOfCurrentComputations++;
        return s;
    }

    private synchronized void releasePickupState(SpecificationState s) {
        nbOfCurrentComputations--;
        notifyAll();
    }

    private synchronized int getNbOfComputations() {
        return nbOfCurrentComputations;
    }

    private synchronized void emptyPendingStates() {
        if (pendingStates != null) {
            pendingStates.clear();
        }
        nbOfCurrentComputations = 0;
    }
    
    private void initExpressionSolvers() {
        AvatarTransition at;
        
        for (AvatarBlock block : spec.getListOfBlocks()) {
            AvatarStateMachine asm = block.getStateMachine();

            for (AvatarStateMachineElement elt : asm.getListOfElements()) {
                if (elt instanceof AvatarTransition) {
                    at = (AvatarTransition) elt;
                    if (at.isGuarded()) {
                        at.buildGuardSolver();
                    }
                    for (AvatarAction aa : at.getActions()) {
                        if (aa instanceof AvatarActionAssignment) {
                            ((AvatarActionAssignment) aa).buildActionSolver(block);
                        }
                    }
                } else if (elt instanceof AvatarActionOnSignal) {
                    ((AvatarActionOnSignal) elt).buildActionSolver(block);
                }
            }
        }
        
        //To have all the leadsTo functionalities allStates in stateMachines must be filled with states
        if (studySafety && safeties != null) {
            for (SafetyProperty sp : safeties) {
               sp.linkSolverStates();
            }
        }
    }

    private void prepareTransitionsOfState(SpecificationState _ss) {

        int cpt;
        _ss.transitions = new ArrayList<SpecificationTransition>();
        //TraceManager.addDev("Preparing transitions of state " + _ss);


        // At first, do not merge synchronous transitions
        // Simply get basics transitions
        cpt = 0;

        for (AvatarBlock block : spec.getListOfBlocks()) {
            AvatarStateMachine asm = block.getStateMachine();
            SpecificationBlock sb = _ss.blocks[cpt];
            AvatarStateElement ase = asm.allStates[sb.values[SpecificationBlock.STATE_INDEX]];

            for (AvatarStateMachineElement elt : ase.getNexts()) {
                if (elt instanceof AvatarTransition) {
                    handleAvatarTransition((AvatarTransition) elt, block, sb, cpt, _ss.transitions, ase.getNexts().size() > 1);
                }
            }

            cpt++;
        }
    }


    private void computeAllStatesFrom(SpecificationState _ss) {
        //TraceManager.addDev("Compute all state of: " + _ss);
        if (_ss == null) {
            TraceManager.addDev("null state");
            mustStop();
            return;
        }
        
        prepareTransitionsOfState(_ss);

        
        if (_ss.transitions == null) {
            TraceManager.addDev("null transitions");
            nbOfDeadlocks++;
            mustStop();
            return;
        }

        //TraceManager.addDev("Possible transitions 1:" + transitions.size());
        ArrayList<SpecificationTransition> transitions = computeValidTransitions(_ss.transitions);
        
        //TraceManager.addDev("Possible transitions 3:" + transitions.size());

        if (ignoreConcurrenceBetweenInternalActions) {
            SpecificationTransition st = null;
            // See whether there is at least one transition with an immediate internal action with no alternative in the same block
            for (SpecificationTransition tr : transitions) {
                if ((AvatarTransition.isActionType(tr.getType()) && (tr.clockMin == tr.clockMax) && (tr.clockMin == 0)) || tr.getType() == AvatarTransition.TYPE_EMPTY) {
                    // Must look for possible transitions from the same state
                    if (!(tr.fromStateWithMoreThanOneTransition)) {
                        st = tr;
                        if (ignoreInternalStates) { // New behavior
                            computeAllInternalStatesFrom(_ss, st);
                            _ss.elaborated = true;
                            return;
                        }
                        break; //old behaviour
                    }

                    // Must look for similar transitions in the the same block
                    /*TraceManager.addDev("\n *** Looking for same block for " + tr);
                    boolean foundSameBlock = false;
                    for(SpecificationTransition tro: transitions) {
			if (tro != tr) {
			    TraceManager.addDev("\tAnalyzing a candidate " + tro);
			    if (tro.hasBlockOf(tr)) {
				foundSameBlock = true;
				TraceManager.addDev("\tFound same block tr=" + tr + " tro=" + tro);
				break;
			    }
                        } else {
			    TraceManager.addDev("\t-> not a candidate");
			}
                    }
                    if (!foundSameBlock) {
			TraceManager.addDev("\tFound no same block for " + tr);
                        st = tr;
                        break;
			}*/
                }
            }
            if (st != null) {
                transitions.clear();
                transitions.add(st);
            }
        }

        //TraceManager.addDev("Possible transitions 4:" + transitions.size());
        if (transitions.size() == 0) {
            checkPropertyOnDeadlock(_ss);
            nbOfDeadlocks++;
            propertyDone = deadlockStop; //use this flag to stop the execution
            //TraceManager.addDev("Deadlock found");
        }

        //TraceManager.addDev("Possible transitions 4:" + transitions.size());
        // For each realizable transition
        //   Make it, reset clock of the involved blocks to 0, increase clockmin/clockhmax of each block
        //   compute new state, and compare with existing ones
        //   If not a new state, create the link rom the previous state to the new one
        //   Otherwise create the new state and its link, and add it to the pending list of states

        int i = 0;
        for (SpecificationTransition tr : transitions) {
            //TraceManager.addDev("Handling transitions #" + cptt + " type =" + tr.getType());

            // Make tr
            // to do so, must create a new state
            SpecificationState newState = _ss.advancedClone();

            // For non impacted blocks, increase their clock value, or set their clock to 0
            newState.increaseClockOfBlocksExcept(tr);

            // Impact the variable of the state, either by executing actions, or by
            // doing the synchronization
            String action = executeTransition(_ss, newState, tr);
            
            if (studySafety) {
                newState.property = evaluateSafetyOfProperty(newState, tr, _ss.property);
            }

            // Remove empty transitions if applicable
            if (ignoreEmptyTransitions) {
                handleNonEmptyUniqueTransition(newState);
            }
            

            // Compute the hash of the new state, and create the link to the right next state
            SpecificationLink link = new SpecificationLink();
            link.originState = _ss;
            action += " [" + tr.clockMin + "..." + tr.clockMax + "]";
            link.action = action;
            newState.computeHash(blockValues);
            //SpecificationState similar = states.get(newState.getHash(blockValues));
            
            //SpecificationState similar = addStateIfNotExisting(newState);
            SpecificationState similar;
            synchronized (this) {
                similar = states.get(newState.getHash(blockValues));
                if (similar == null) {
                    if (!((stateLimitRG && stateID >= stateLimit)/* || timeLimitReached*/)) {
                        addState(newState);
                    } else {
                        limitReached = true;
                        continue;
                    }
                }
            }
            
            if (similar == null) {
                //  Unknown state

                //states.put(newState.getHash(blockValues), newState);
                //addState(newState);
                //newState.id = getStateID();
                //TraceManager.addDev("P//hereutting new state with id = " +  newState.id + " stateID = " + stateID + " states size = " + states.size() + " states by id size = " + statesByID.size());
                //statesByID.put(newState.id, newState);

                link.destinationState = newState;
                newState.distance = _ss.distance + 1;
                
                if (!studySafety) {
                    pendingStates.add(newState);
                } else {
                    actionOnProperty(newState, i, similar, _ss);
                }
                i++;
                //newState.id = getStateID();
                //TraceManager.addDev("Creating new state for newState=" + newState);

            } else {
                // Create a link from former state to the existing one
                //TraceManager.addDev("Similar state found State=" + newState.getHash(blockValues) + "\n" + newState + "\nsimilar=" + similar.getHash(blockValues) + "\n" + similar);

                link.destinationState = similar;

                // If liveness, must verify that from similar it is possible to go to the considered
                // state or not.
                actionOnProperty(newState, i, similar, _ss);
            }

            //links.add(link);
            nbOfLinks++;
            _ss.addNext(link);
        }
        
        _ss.elaborated = true;
        
        if (limitReached) {
        	if (_ss.isDeadlock()) {
        		// have to register current state as deadlock of the graph
        		nbOfDeadlocks++;
        	}
        }

        if (freeIntermediateStateCoding) {
            _ss.freeUselessAllocations();
        } else {
            _ss.finished();
        }

        mustStop();
    }


    private void computeAllInternalStatesFrom(SpecificationState _ss, SpecificationTransition st) {
        SpecificationState newState = _ss.advancedClone();
        SpecificationState previousState = _ss;

        while (st != null) {
            //TraceManager.addDev("cpt=" + cpt + " Working on transition:" + st);
            newState.increaseClockOfBlocksExcept(st);
            String action = executeTransition(previousState, newState, st);
            
            if (studySafety) {
                newState.property = evaluateSafetyOfProperty(newState, st, previousState.property);
            }
            
            if (ignoreEmptyTransitions) {
                handleNonEmptyUniqueTransition(newState);
            }


            newState.computeHash(blockValues);

            SpecificationState similar = findSimilarState(newState);
            if (similar != null) {
                SpecificationLink link = new SpecificationLink();
                link.originState = _ss;
                action += " [" + st.clockMin + "..." + st.clockMax + "]";
                link.action = action;
                link.destinationState = similar;
                nbOfLinks++;
                _ss.addNext(link);
                actionOnProperty(newState, 0, similar, _ss);
                break;
            } else if (studySafety && safety.safetyType == SafetyProperty.LEADS_TO && newState.property) {
                newState = reduceCombinatorialExplosionProperty(newState);
                
                similar = findSimilarState(newState);
                SpecificationLink link = new SpecificationLink();
                link.originState = _ss;
                action += " [" + st.clockMin + "..." + st.clockMax + "]";
                link.action = action;
                if (similar != null) {
                    link.destinationState = similar;
                } else {
                    link.destinationState = newState;
                    addState(newState);
                    actionOnProperty(newState, 0, similar, _ss);
                }
                nbOfLinks++;
                _ss.addNext(link);
                break;
            }


            // Compute next transition
            //prepareTransitionsOfState(previousState);
            prepareTransitionsOfState(newState);
            
            if (newState.transitions == null) {
                TraceManager.addDev("null transitions");
                nbOfDeadlocks++;
                propertyDone = deadlockStop; //use this flag to stop the execution
                mustStop();
                return;
            }
            
            ArrayList<SpecificationTransition> transitions = computeValidTransitions(newState.transitions);
            
            st = null;
            if (ignoreConcurrenceBetweenInternalActions) {

                // See whether there is at least one transition with an immediate internal action with no alternative in the same block
                for (SpecificationTransition tr : transitions) {
                    //TraceManager.addDev("tr=" + tr + " type=" + tr.getType());
                    if ((AvatarTransition.isActionType(tr.getType()) && (tr.clockMin == tr.clockMax) && (tr.clockMin == 0))
                            || tr.getType() == AvatarTransition.TYPE_EMPTY) {
                        // Must look for possible transitions from the same state
                        if (!(tr.fromStateWithMoreThanOneTransition)) {
                            st = tr;
                            break;
                        }
                    }
                }
            }
            if (st == null) {
                //Creating new link
                SpecificationLink link = new SpecificationLink();
                link.originState = _ss;
                action += " [" +  "0...0" +  "]";
                link.action = action;
                synchronized (this) {
                    similar = states.get(newState.getHash(blockValues));
                    if (similar == null) {
                        if (!(stateLimitRG && stateID >= stateLimit)/* || timeLimitReached*/) {
                            addState(newState);
                        } else {
                            limitReached = true; //can be removed
                            break;
                        }
                    }
                }
                if (similar != null) {
                	// check if it has been created by another thread in the meanwhile
                	link.destinationState = similar;
                	actionOnProperty(newState, 0, similar, _ss);
                } else {
                    link.destinationState = newState;
                    newState.distance = _ss.distance + 1;
                    if (!studySafety) {
                        pendingStates.add(newState);
                    } else {
                        actionOnProperty(newState, 0, similar, _ss);
                    }
                }
                nbOfLinks++;
                _ss.addNext(link);
                break;
            }
            previousState = newState;
            newState = previousState.advancedClone();
        }
        
        if (limitReached) {
        	if (_ss.isDeadlock()) {
        		// have to register current state as deadlock of the graph
        		nbOfDeadlocks++;
        	}
        }
        
        if (freeIntermediateStateCoding) {
            _ss.freeUselessAllocations();
        } else {
            _ss.finished();
        }

        mustStop();
    }
    
    
    private SpecificationState reduceCombinatorialExplosionProperty(SpecificationState ss) {
        // ss with true property. Find and execute transitions that maintain the property
        // true
        SpecificationState prevState = ss;
        boolean found;
        
        do {
            prepareTransitionsOfState(prevState);
            
            if (prevState.transitions == null) {
                return prevState;
            }
            
            ArrayList<SpecificationTransition> transitions = computeValidTransitions(prevState.transitions);
            
            found = false;
            for (SpecificationTransition tr : transitions) {
                if ((AvatarTransition.isActionType(tr.getType()) && (tr.clockMin == tr.clockMax) && (tr.clockMin == 0))
                        || tr.getType() == AvatarTransition.TYPE_EMPTY) {
                    if (!(tr.fromStateWithMoreThanOneTransition)) {
                        // try this transition
                        SpecificationState newState = prevState.advancedClone();
                        executeTransition(prevState, newState, tr);
                        newState.property = evaluateSafetyOfProperty(newState, tr, false);
                        if (newState.property) {
                            newState.computeHash(blockValues);
                            SpecificationState similar = findSimilarState(newState);
                            if (similar != null) {
                                return newState;
                            }
                            found = true;
                            prevState = newState;
                            break;
                        }
                    }
                }
            }
        } while (found);
        
        return prevState;
    }
    
    private ArrayList<SpecificationTransition> computeValidTransitions(ArrayList<SpecificationTransition> transitions) {
        ArrayList<SpecificationTransition> newTransitions = new ArrayList<SpecificationTransition>();
        ArrayList<SpecificationTransition> validTransitions = new ArrayList<SpecificationTransition>();

        // All locally executable transitions are now gathered.
        // We simply need to select the ones that are executable
        // Two constraints: synchronous transactions must have a counter part
        // then, we select only the transitions which clock intervals are within the lowest clock interval

        // Reworking sync/non sync. We create one new transition for all possible synchros, and we remove the ones
        // with only one synchro
        for (SpecificationTransition tr : transitions) {
            if (tr.getType() == AvatarTransition.TYPE_SEND_SYNC) {
                for (SpecificationTransition tro : transitions) {
                    if (tro.getType() == AvatarTransition.TYPE_RECV_SYNC) {
                        SpecificationTransition newT = computeSynchronousTransition(tr, tro);
                        if (newT != null) newTransitions.add(newT);
                    }
                }
            } else if (AvatarTransition.isActionType(tr.getType())) {
                newTransitions.add(tr);
            } else if (tr.getType() == AvatarTransition.TYPE_EMPTY) {
                newTransitions.add(tr);
            }
        }

        // Selecting only the transactions within the smallest clock interval
        int clockMin = Integer.MAX_VALUE, clockMax = Integer.MAX_VALUE;
        for (SpecificationTransition tr : newTransitions) {
            clockMin = Math.min(clockMin, tr.clockMin);
            clockMax = Math.min(clockMax, tr.clockMax);
        }
        
        for (SpecificationTransition tr : newTransitions) {
            if (tr.clockMin <= clockMax) {
                tr.clockMax = clockMax;
                validTransitions.add(tr);
            }
        }
        return validTransitions;
    }

    private boolean guardResult(AvatarTransition _at, AvatarBlock _block, SpecificationBlock _sb) {
        if (!_at.isGuarded()) {
            return true;
        }

        // Must evaluate the guard
        //String guard = _at.getGuard().toString();
        //String s = Conversion.replaceAllString(guard, "[", "").trim();
        //s = Conversion.replaceAllString(s, "]", "").trim();
        return (_at.getGuardSolver().getResult(_sb) == 0) ?  false : true;
        //return evaluateBoolExpression(s, _block, _sb);
    }

    private void handleAvatarTransition(AvatarTransition _at, AvatarBlock _block, SpecificationBlock _sb, int _indexOfBlock, ArrayList<SpecificationTransition> _transitionsToAdd, boolean _fromStateWithMoreThanOneTransition) {
        if (_at.type == AvatarTransition.UNDEFINED) {
            return;
        }

        // Must see whether the guard is ok or not
        boolean guard = guardResult(_at, _block, _sb);
        if (!guard) {
            return;
        }

        SpecificationTransition st = new SpecificationTransition();
        st.fromStateWithMoreThanOneTransition = _fromStateWithMoreThanOneTransition;
        _transitionsToAdd.add(st);
        st.init(1, _at, _indexOfBlock);

        // Must compute the clockmin and clockmax values
        String minDelay = _at.getMinDelay().trim();
        if ((minDelay == null) || (minDelay.length() == 0)) {
            st.clockMin = 0 - _sb.values[SpecificationBlock.CLOCKMAX_INDEX];
        } else {
            st.clockMin = evaluateIntExpression(_at.getMinDelay(), _block, _sb) - _sb.values[SpecificationBlock.CLOCKMAX_INDEX];
        }
        String maxDelay = _at.getMaxDelay().trim();
        if ((maxDelay == null) || (maxDelay.length() == 0)) {
            st.clockMax = 0 - _sb.values[SpecificationBlock.CLOCKMIN_INDEX];
        } else {
            int resMax = evaluateIntExpression(_at.getMaxDelay(), _block, _sb);
            _sb.maxClock = Math.max(_sb.maxClock, resMax);
            st.clockMax = resMax - _sb.values[SpecificationBlock.CLOCKMIN_INDEX];
        }

        if (st.clockMin > st.clockMax) {
            int tmp = st.clockMin;
            st.clockMin = st.clockMax;
            st.clockMax = tmp;
        }

    }


    private void prepareStates() {
        // Put states in a list
        for (AvatarBlock block : spec.getListOfBlocks()) {
            AvatarStateMachine asm = block.getStateMachine();
            if (asm != null) {
                asm.makeAllStates();
            }
        }
    }

    private void prepareTransitions() {
        // Compute the id of each transition
        // Assumes the allStates list has been computed in AvatarStateMachine
        // Assumes that it is only after states that transitions have non empty
        signalRelation = new HashMap<AvatarTransition, Set<AvatarTransition>>();

        for (AvatarBlock block : spec.getListOfBlocks()) {
            AvatarStateMachine asm = block.getStateMachine();
            if (asm != null) {
                for (int i = 0; i < asm.allStates.length; i++) {
                    for (int j = 0; j < asm.allStates[i].nbOfNexts(); j++) {
                        AvatarStateMachineElement elt = asm.allStates[i].getNext(j);
                        if (elt instanceof AvatarTransition) {
                            AvatarTransition at = (AvatarTransition) elt;
                            AvatarStateMachineElement next = at.getNext(0);
                            if (next != null) {
                                if (next instanceof AvatarActionOnSignal) {
                                    AvatarSignal sig = ((AvatarActionOnSignal) next).getSignal();
                                    if (sig != null) {
                                        if (sig.isIn()) {
                                            at.type = AvatarTransition.TYPE_RECV_SYNC;
                                        } else {
                                            at.type = AvatarTransition.TYPE_SEND_SYNC;
                                            synchronizeSignalRelation(at, sig);
                                        }
                                    }
                                } else {
                                    if (at.hasAction()) {
                                        if (at.hasMethod()) {
                                            at.type = AvatarTransition.TYPE_ACTION_AND_METHOD;
                                        } else {
                                            at.type = AvatarTransition.TYPE_ACTIONONLY;
                                        }
                                    } else {
                                        if (at.hasMethod()) {
                                            at.type = AvatarTransition.TYPE_METHODONLY;
                                        } else {
                                            at.type = AvatarTransition.TYPE_EMPTY;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void prepareBlocks() {
        int i = 0;
        for (AvatarBlock ab : spec.getListOfBlocks()) {
            ab.setBlockIndex(i++);
        }
    }
    
    private void synchronizeSignalRelation(AvatarTransition sender, AvatarSignal sig) {
        HashSet<AvatarTransition> set = new HashSet<>();

        for (AvatarBlock block : spec.getListOfBlocks()) {
            AvatarStateMachine asm = block.getStateMachine();
            if (asm != null) {
                for (int i = 0; i < asm.allStates.length; i++) {
                    for (int j = 0; j < asm.allStates[i].nbOfNexts(); j++) {
                        AvatarStateMachineElement e = asm.allStates[i].getNext(j);
                        if (e instanceof AvatarTransition) {
                            AvatarStateMachineElement n = e.getNext(0);
                            if (n != null && n instanceof AvatarActionOnSignal) {
                                AvatarSignal asr = ((AvatarActionOnSignal) n).getSignal();
                                if (asr.isIn() && spec.areSynchronized(sig, asr)) {
                                    set.add((AvatarTransition) e);
                                }
                            }
                        }
                    }
                }
            }
        }
        signalRelation.put(sender, set);
    }

    public boolean oldEvaluateBoolExpression(String _expr, AvatarBlock _block, SpecificationBlock _sb) {
        String act = _expr;
        int cpt = 0;
        for (AvatarAttribute at : _block.getAttributes()) {
            String val = "";
            if (at.isInt()) {
                val = "" + _sb.values[cpt + SpecificationBlock.ATTR_INDEX];
                if (val.startsWith("-")) {
                    val = "(0" + val + ")";
                }
            } else if (at.isBool()) {
                if (_sb.values[cpt + SpecificationBlock.ATTR_INDEX] == 0) {
                    val = "false";
                } else {
                    val = "true";
                }
            }
            act = Conversion.putVariableValueInString(AvatarSpecification.ops, act, _block.getAttribute(cpt).getName(), val);
            cpt++;
        }

        BoolExpressionEvaluator bee = new BoolExpressionEvaluator();

        if (act.trim().startsWith("100")) {
            //TraceManager.addDev("Current block " + _block.getName());
        }

        boolean result = bee.getResultOf(act);
        if (bee.getError() != null) {
            TraceManager.addDev("Error: " + bee.getError());
        }

        //TraceManager.addDev("Result of " + _expr + " = " + result);
        return result;
    }

    public boolean evaluateBoolExpression(String _expr, AvatarBlock _block, SpecificationBlock _sb) {
        String act = _expr;
        int cpt = 0;
        for (AvatarAttribute at : _block.getAttributes()) {
            String val = "";
            if (at.isInt()) {
                val = "" + _sb.values[cpt + SpecificationBlock.ATTR_INDEX];
                if (val.startsWith("-")) {
                    val = "(0" + val + ")";
                }
            } else if (at.isBool()) {
                if (_sb.values[cpt + SpecificationBlock.ATTR_INDEX] == 0) {
                    val = "f";
                } else {
                    val = "t";
                }
            }
            act = Conversion.putVariableValueInString(AvatarSpecification.ops, act, _block.getAttribute(cpt).getName(), val);
            cpt++;
        }

        BoolExpressionEvaluator bee = new BoolExpressionEvaluator();

        if (act.trim().startsWith("100")) {
            //TraceManager.addDev("Current block " + _block.getName());
        }

        boolean result = bee.getResultOfWithIntExpr(act);
        if (bee.getError() != null) {
            TraceManager.addDev("Error: " + bee.getError());
        }

        //TraceManager.addDev("Result of " + _expr + " = " + result);
        return result;
    }

    public int evaluateIntExpression(String _expr, AvatarBlock _block, SpecificationBlock _sb) {
        //TraceManager.addDev("Evaluating Int expression 1: " + _expr);
        String act = _expr;
        int cpt = 0;
        for (AvatarAttribute at : _block.getAttributes()) {
            String val = "";
            if (at.isInt()) {
                val = "" + _sb.values[cpt + SpecificationBlock.ATTR_INDEX];
                if (val.startsWith("-")) {
                    val = "(0" + val + ")";
                }
            } else if (at.isBool()) {
                if (_sb.values[cpt + SpecificationBlock.ATTR_INDEX] == 0) {
                    val = "false";
                } else {
                    val = "true";
                }
            }
            act = Conversion.putVariableValueInString(AvatarSpecification.ops, act, _block.getAttribute(cpt).getName(), val);
            cpt++;
        }

        //TraceManager.addDev("Evaluating Int expression S: " + act);
        //Thread.currentThread().dumpStack();
        return (int) (new IntExpressionEvaluator().getResultOf(act));
    }

    private SpecificationTransition computeSynchronousTransition(SpecificationTransition sender, SpecificationTransition receiver) {
//        AvatarTransition trs = sender.transitions[0];
//        AvatarTransition trr = receiver.transitions[0];
//
//        AvatarStateMachineElement asmes, asmer;
//        asmes = trs.getNext(0);
//        asmer = trr.getNext(0);
//        if ((asmes == null) || (asmer == null)) return null;
//        if (!(asmes instanceof AvatarActionOnSignal)) return null;
//        if (!(asmer instanceof AvatarActionOnSignal)) return null;
//
//        AvatarSignal ass = ((AvatarActionOnSignal) asmes).getSignal();
//        AvatarSignal asr = ((AvatarActionOnSignal) asmer).getSignal();
//
//        if (spec.areSynchronized(ass, asr)) {
//            SpecificationTransition st = new SpecificationTransition();
//            st.makeFromTwoSynchronous(sender, receiver);
//            return st;
//        }
        
        if (signalRelation.get(sender.transitions[0]).contains(receiver.transitions[0])) {
            SpecificationTransition st = new SpecificationTransition();
            st.makeFromTwoSynchronous(sender, receiver);
            return st;
        }

        return null;
    }


    private String executeTransition(SpecificationState _previousState, SpecificationState _newState, SpecificationTransition _st) {
        int type = _st.transitions[0].type;
        AvatarStateElement ase;

        // Fill the new states of the involved blocks
        for (int i = 0; i < _st.transitions.length; i++) {
            ase = getNextState(_st.transitions[i], _newState, 10);
            if (ase != null) {
                checkElement(ase, _newState);
                //int index = _st.blocks[i].getStateMachine().getIndexOfState(ase);
                int index = _st.transitions[i].getBlock().getStateMachine().getIndexOfState(ase);
                if (index > -1) {
                    _newState.blocks[((AvatarBlock) _st.transitions[i].getBlock()).getBlockIndex()].values[SpecificationBlock.STATE_INDEX] = index;
                }
            }
        }


        if ((AvatarTransition.isActionType(type)) || (type == AvatarTransition.TYPE_EMPTY)) {
            return executeActionTransition(_previousState, _newState, _st);
        } else if (type == AvatarTransition.TYPE_SEND_SYNC) {
            return executeSyncTransition(_previousState, _newState, _st);
        }

        return "not implemented";
    }

    private AvatarStateElement getNextState(AvatarStateMachineElement e, SpecificationState _newState, int maxNbOfIterations) {
        checkElement(e, _newState);
        e = e.getNext(0);
        if (e instanceof AvatarStateElement) {
            return (AvatarStateElement) e;
        }
        maxNbOfIterations--;
        if (maxNbOfIterations == 0) {
            return null;
        }
        return getNextState(e, _newState, maxNbOfIterations);
    }

    // Execute the actions of a transition, and correspondingly impact the variables of the
    // block executing the transition
    private String executeActionTransition(SpecificationState _previousState, SpecificationState _newState, SpecificationTransition _st) {
        // We use the attributes value as in the _newState
        // Get the attributes value list
//        AvatarBlock block = _st.blocks[0];

        String retAction = null;

        for (AvatarAction aAction : _st.transitions[0].getActions()) {
            // Variable affectation
            if (!(aAction.containsAMethodCall())) {
                //if (!(aAction.isAMethodCall ())) {
                /*String action = aAction.toString ();
                  int ind = action.indexOf("=");
                  if (ind == -1) {
                  return "";
                  }*/
                String nameOfVar = ((AvatarActionAssignment) aAction).getLeftHand().getName();
                String act = ((AvatarActionAssignment) aAction).getRightHand().getName();

                //TraceManager.addDev("*** act=" + act);

                if (retAction == null) {
                    retAction = nameOfVar + "=" + act;
                }
                
                ((AvatarActionAssignment) aAction).executeActionSolver(_newState.blocks[((AvatarBlock) _st.transitions[0].getBlock()).getBlockIndex()]);

//                int indexVar = block.getIndexOfAvatarAttributeWithName(nameOfVar);
//                AvatarType type = block.getAttribute(indexVar).getType();
//                if (indexVar != -1) {
//                    if (type == AvatarType.INTEGER) {
//                        //TraceManager.addDev("Evaluating int expr=" + act);
//                        int result = evaluateIntExpression(act, _st.blocks[0], _newState.blocks[_st.blocksInt[0]]);
//                        _newState.blocks[_st.blocksInt[0]].values[SpecificationBlock.ATTR_INDEX + indexVar] = result;
//                    } else if (type == AvatarType.BOOLEAN) {
//                        boolean bool = evaluateBoolExpression(act, _st.blocks[0], _newState.blocks[_st.blocksInt[0]]);
//                        if (bool) {
//                            _newState.blocks[_st.blocksInt[0]].values[SpecificationBlock.ATTR_INDEX + indexVar] = 1;
//                        } else {
//                            _newState.blocks[_st.blocksInt[0]].values[SpecificationBlock.ATTR_INDEX + indexVar] = 0;
//                        }
//                    }
//                }
            }
        }

        if (retAction == null) {
            retAction = "";
        }

        //return "i(" + _st.blocks[0].getName() + "/" + retAction + ")";
        return "i(" + _st.transitions[0].getBlock().getName() + "/" + retAction + ")";

    }

    private String executeSyncTransition(SpecificationState _previousState, SpecificationState _newState, SpecificationTransition _st) {
//        AvatarBlock block0 = _st.blocks[0];
//        AvatarBlock block1 = _st.blocks[1];
        AvatarActionOnSignal aaoss, aaosr;
//        AvatarAttribute avat;
//        String value;
        int result;
//        boolean resultB;
//        int indexVar;
//        String nameOfVar;
        String ret = "";


        try {
            aaoss = (AvatarActionOnSignal) (_st.transitions[0].getNext(0));
            aaosr = (AvatarActionOnSignal) (_st.transitions[1].getNext(0));
        } catch (Exception e) {
            return "";
        }

        // copy the value of attributes from one block to the other one
//        for (int i = 0; i < aaoss.getNbOfValues(); i++) {           
//            value = aaoss.getValue(i);
//            try {
//                avat = aaoss.getSignal().getListOfAttributes().get(i);
//                if (avat.getType() == AvatarType.INTEGER) {
//                    //TraceManager.addDev("Evaluating expression, value=" + value);
//                    //TraceManager.addDev("Evaluating int expr=" + value);
//                    result = evaluateIntExpression(value, block0, _newState.blocks[_st.blocksInt[0]]);
//                } else if (avat.getType() == AvatarType.BOOLEAN) {
//                    resultB = evaluateBoolExpression(value, block0, _newState.blocks[_st.blocksInt[0]]);
//                    result = resultB ? 1 : 0;
//                } else {
//                    result = 0;
//                }
//
//                // Putting the result to the destination var
//                nameOfVar = aaosr.getValue(i);
//                indexVar = block1.getIndexOfAvatarAttributeWithName(nameOfVar);
//                _newState.blocks[_st.blocksInt[1]].values[SpecificationBlock.ATTR_INDEX + indexVar] = result;
//                ret += "" + result;
//            } catch (Exception e) {
//                TraceManager.addDev("EXCEPTION on adding value " + aaoss);
//            }
//        }
        for (int i = 0; i < aaoss.getNbOfValues(); i++) {
//            try {
                result = aaoss.getExpressionAttribute(i).getValue(_newState.blocks[((AvatarBlock) _st.transitions[0].getBlock()).getBlockIndex()]);
                aaosr.getExpressionAttribute(i).setValue(_newState.blocks[((AvatarBlock) _st.transitions[1].getBlock()).getBlockIndex()], result);
                ret += result;
//            } catch (Exception e) {
//                TraceManager.addDev("EXCEPTION on adding value " + aaoss);
//            }
        }


        return "!" + aaoss.getSignal().getName() + "_?" + aaosr.getSignal().getName() + "(" + ret + ")";

    }

    public void handleNonEmptyUniqueTransition(SpecificationState _ss) {
        int cpt = 0;
        for (AvatarBlock block : spec.getListOfBlocks()) {
            AvatarStateMachine asm = block.getStateMachine();
            SpecificationBlock sb = _ss.blocks[cpt];
            AvatarStateElement ase = asm.allStates[sb.values[SpecificationBlock.STATE_INDEX]];

            AvatarStateElement aseAfter = getStateWithNonEmptyUniqueTransition(ase, block, sb, _ss);
            if (aseAfter != ase) {
                //checkElement(aseAfter, _ss);
                // Must modify the state of the considered block
                sb.values[SpecificationBlock.STATE_INDEX] = asm.getIndexOfState(aseAfter);
            }
            cpt++;
        }
    }

    private AvatarStateElement getStateWithNonEmptyUniqueTransition(AvatarStateElement _ase, AvatarBlock _block, SpecificationBlock _sb, SpecificationState _ss) {
        return getStateWithNonEmptyUniqueTransitionArray(_ase, _block, _sb, _ss, null);
    }

    private AvatarStateElement getStateWithNonEmptyUniqueTransitionArray(AvatarStateElement _ase, AvatarBlock _block, SpecificationBlock _sb, SpecificationState _ss, ArrayList<AvatarStateElement> listOfStates) {

        //      TraceManager.addDev("Handling Empty transition of previous=" + _ase.getName());
        
        if (studySafety && safety.propertyType == SafetyProperty.BLOCK_STATE) {
            boolean result = safety.getSolverResult(_ss, _ase);
            if (safety.safetyType == SafetyProperty.ALLTRACES_ALLSTATES || safety.safetyType == SafetyProperty.ONETRACE_ALLSTATES) {
                result = !result;
            }
            _ss.property |= result;
            
            if (safety.safetyType == SafetyProperty.LEADS_TO && result) {
                return _ase;
            }
        }

        if (_ase.getNexts().size() != 1) {
            return _ase;
        }


        //TraceManager.addDev("Handling Empty transition of previous= 1 " + _ase.getName());

        AvatarTransition at = (AvatarTransition) (_ase.getNext(0));

        //TraceManager.addDev("Handling Empty transition of previous= 2 " + _ase.getName() + " trans=" + at.getName());

        if (!((at.type == AvatarTransition.TYPE_EMPTY) || (at.type == AvatarTransition.TYPE_METHODONLY))) {
            return _ase;
        }

        //TraceManager.addDev("Handling Empty transition of previous= 3 " + _ase.getName() + " trans=" + at.getName());

        // Check time
        boolean hasTime = at.hasDelay();
        if (hasTime) {
            return _ase;
        }


        // Check guard;
        boolean guard = guardResult(at, _block, _sb);

        if (!guard) {
            return _ase;
        }

        //TraceManager.addDev("Handling Empty transition of previous= 4 " + _ase.getName() + " trans=" + at.getName() + " delay=" + at.getMinDelay() + " guard=" + at.getGuard() + " next=" + at.getNext(0));

        AvatarStateElement ase = (AvatarStateElement) (at.getNext(0));
        checkElement(ase, _ss);
        //TraceManager.addDev("Handling Empty transition of " + _block.getName() + " with nextState = " + ase.getName() + " and previous=" + _ase.getName());

        if (listOfStates == null) {
            if (ase == _ase) {
                return _ase;
            }
            //TraceManager.addDev("New list of states " + _block.getName());
            listOfStates = new ArrayList<AvatarStateElement>();
        } else {
            if (listOfStates.contains(ase)) {
                return _ase;
            }
        }
        listOfStates.add(_ase);

        return getStateWithNonEmptyUniqueTransitionArray(ase, _block, _sb, _ss, listOfStates);


    }

    
    private boolean evaluateSafetyOfProperty(SpecificationState newState, SpecificationTransition tr, boolean precProperty) {
        AvatarStateMachineElement asme;
        boolean result;
        
        if (safety.propertyType == SafetyProperty.BLOCK_STATE) {
            result = safety.getSolverResult(newState);
            
            if (!result) {
                for (int i = 0; i < tr.transitions.length; i++) {
                    int k = 10;
                    asme = tr.transitions[i].getNext(0);
                    while (k > 0) {
                        result |= safety.getSolverResult(newState, asme);
                        if (asme instanceof AvatarStateElement || result == true) {
                            break;
                        }
                        asme = asme.getNext(0);
                        k--;
                    }
                }
            }
        } else {
            result = safety.getSolverResult(newState);
        }
        
        // the value to be associated to the state property depends on the type of property to be checked
        // A[] -> !result; A<> -> result; E[] -> !result; E<> -> result
        if (safety.safetyType == SafetyProperty.ALLTRACES_ALLSTATES || safety.safetyType == SafetyProperty.ONETRACE_ALLSTATES) {
            result = !result;
        }

        return result || precProperty;
    }
    
    private void actionOnProperty(SpecificationState newState, int i, SpecificationState similar, SpecificationState _ss) {
        if (studySafety) {
            if (safety.safetyType == SafetyProperty.ALLTRACES_ALLSTATES) {
                if (newState.property) {
                    propertyDone = true;
                    safety.result = false;
                } else if (similar == null){
                    pendingStates.add(newState);
                }
            } else if (safety.safetyType == SafetyProperty.ONETRACE_ALLSTATES) {
                if (similar == null) {
                    if (!newState.property) {
                        if (i == 0) {
                            //Priority for parallel DFS on the first transition
                            pendingStates.add(0, newState);
                        } else {
                            //Not priority for parallel BFS on the other transitions
                            pendingStates.add(newState);
                        }
                    } else {
                        newState.freeUselessAllocations();
                    }
                } else if (!newState.property) {
                    if (!similar.property && stateIsReachableFromState(similar, _ss)) {
                        //found a loop with true property
                        propertyDone = true;
                        safety.result = true;
                    }
                }
            } else if (safety.safetyType == SafetyProperty.ALLTRACES_ONESTATE) {
                if (similar == null) {
                    if (!newState.property) { 
                        if (i == 0) {
                            //Priority for parallel DFS on the first transition
                            pendingStates.add(0, newState);
                        } else {
                            //Not priority for parallel BFS on the other transitions
                            pendingStates.add(newState);
                        }
                    } else {
                        newState.freeUselessAllocations();
                    }
                } else if (newState.property == false) {
                    if (!similar.property && stateIsReachableFromState(similar, _ss)) {
                        //found a loop with false property
                        propertyDone = true;
                        safety.result = false;
                    }
                }
            } else if (safety.safetyType == SafetyProperty.ONETRACE_ONESTATE) {
                if (newState.property) {
                    propertyDone = true;
                    safety.result = true;
                } else if (similar == null) {
                    pendingStates.add(newState);
                }
            } else if (safety.safetyType == SafetyProperty.LEADS_TO) {
                if (similar == null) {
                    if (newState.property) {
                        SpecificationState state = newState.advancedClone();
                        if (!ignoreConcurrenceBetweenInternalActions) {
                            state = reduceCombinatorialExplosionProperty(state);
                        }
                        safetyLeadStates.put(state.getHash(state.getBlockValues()), state);
                        newState.property = false;
                    }
                    pendingStates.add(newState);
                }
            }
        }

        if (studyReinit && similar != null) {
            if (similar != initState.initState && stateIsReachableFromState(similar, _ss)) {
                propertyDone = true;
                initState.setResult(false);
            }
        }
    }
    
    private void checkPropertyOnDeadlock(SpecificationState ss) {
        if (studySafety) {
            if (safety.safetyType == SafetyProperty.ALLTRACES_ONESTATE && ss.property == false) {
                propertyDone = true;
                safety.result = false;
            } else if (safety.safetyType == SafetyProperty.ONETRACE_ALLSTATES && ss.property == false) {
                propertyDone = true;
                safety.result = true;
            }
        }
        if (studyReinit) {
            propertyDone = true;
            initState.setResult(false);
        }
    }


    // Checking elements
    public void checkElement(AvatarStateMachineElement elt, SpecificationState _ss) {
        if (studyReachability) {
            checkElementReachability(elt, _ss);
        }
    }

    public void checkElementReachability(AvatarStateMachineElement elt, SpecificationState _ss) {
        for (SpecificationReachability re : reachabilities) {
            if (re.result == SpecificationPropertyPhase.NOTCOMPUTED) {
                if (re.ref1 == elt) {
                    re.result = SpecificationPropertyPhase.SATISFIED;
                    re.state = _ss;
                    nbOfRemainingReachabilities--;
                    //TraceManager.addDev("Remaining reachabilities:" + nbOfRemainingReachabilities);
                }
            }
        }
    }


    // Stop condition

    public synchronized boolean mustStop() {

        if (stoppedBeforeEnd) {
            return true;
        }


        if (stoppedConditionReached) {
            return true;
        }

        if (studySafety && propertyDone) {
            stoppedConditionReached = true;
            return true;
        }


        if (studyReachability && nbOfRemainingReachabilities == 0) {
            //TraceManager.addDev("***** All reachability found");
            stoppedConditionReached = true;
        }

        if (studyReachability && nbOfRemainingReachabilities > 0) {
            stoppedConditionReached = false;
        }

        if (computeRG) {
            stoppedConditionReached = false;
        }

        if (studySafety) {
            stoppedConditionReached = false;
        }

        return stoppedConditionReached;
    }


    // Generators

    public String toString() {
        StringBuffer sb = new StringBuffer("States:\n");
        for (SpecificationState state : states.values()) {
            sb.append(state.toString() + "\n");
        }
        sb.append("\nLinks:\n");
        for (SpecificationState state : states.values()) {
            if (state.nexts != null) {
                for (SpecificationLink link : state.nexts) {
                    sb.append(link.toString() + "\n");
                }
            }
        }
        return sb.toString();
    }


    public String statesToString() {
        StringBuffer sb = new StringBuffer("States:\n");
        List<SpecificationState> sortedStates = new ArrayList<SpecificationState>(states.values());
        Collections.sort(sortedStates);

        for (SpecificationState state : sortedStates) {
            sb.append(state.toString() + "\n");
        }
        /*for(SpecificationState state: states.values()) {
            sb.append(state.toString() + "\n");
	    }*/
        return sb.toString();
    }


    public String toAUT() {
        StringBuffer sb = new StringBuffer();
        sb.append("des(0," + getNbOfLinks() + "," + getNbOfStates() + ")\n");

        for (SpecificationState state : states.values()) {
            //TraceManager.addDev("State:" + state.id);
            if (state.nexts != null) {
                for (SpecificationLink link : state.nexts) {
                    sb.append("(" + link.originState.id + ",\"" + link.action + "\"," + link.destinationState.id + ")\n");
                }
            }
        }
        //TraceManager.addDev("StateID=" + stateID);
        return new String(sb);
    }

    public String toDOT() {
        StringBuffer sb = new StringBuffer();
        sb.append("digraph TToolAvatarGraph {\n");

        for (SpecificationState state : states.values()) {
            if (state.nexts != null) {
                for (SpecificationLink link : state.nexts) {

                    sb.append(" " + link.originState.id + " -> " + link.destinationState.id + "[label=\"" + link.action + "\"];\n");
                }
            }
        }
        sb.append("}");
        return new String(sb);
    }

    public ArrayList<SpecificationReachability> getAllReachabilities() {
        return reachabilities;
    }

    public String reachabilityToString() {
        if (!studyReachability) {
            return "Reachability not activated";
        }


        StringBuilder ret = new StringBuilder();
        if (stoppedBeforeEnd) {
            ret.append("Beware: Full study of reachability might not have been fully completed\n");
        }

        int cpt = 0;
        for (SpecificationReachability re : reachabilities) {
            ret.append((cpt + 1) + ". " + re.toString() + "\n");
            cpt++;
        }
        return ret.toString();
    }
    
    public String reachabilityToStringGeneric() {
        if (!studyReachability) {
            return "Reachability not activated\n";
        }


        StringBuilder ret = new StringBuilder();
        if (stoppedBeforeEnd) {
            ret.append("Beware: Full study of reachability might not have been fully completed\n");
        }

        int cpt = 0;
        for (SpecificationReachability re : reachabilities) {
            ret.append((cpt + 1) + ". " + re.toStringGeneric() + "\n");
            cpt++;
        }
        return ret.toString();
    }
    
    public String livenessToString() {
        if (!studyLiveness) {
            return "Liveness not activated\n";
        }


        StringBuilder ret = new StringBuilder();
        if (stoppedBeforeEnd) {
            ret.append("Beware: Full study of liveness might not have been fully completed\n");
        }

        int cpt = 0;
        for (SafetyProperty sp : livenesses) {
            ret.append((cpt + 1) + ". " + sp.toLivenessString() + "\n");
            cpt++;
        }
        return ret.toString();
    }
    
    public String deadlockToString() {
        if (!checkNoDeadlocks) {
            return "Deadlock check not activeted\n";
        }
        
        String ret;
        if (nbOfDeadlocks > 0) {
            ret = "property is NOT satisfied\n";
        } else {
            ret = "property is satisfied\n";
        }
        
        return ret;
    }
    
    public String safetyToString() {
        if (!studySafety) {
            return "Safety check is not activated\n";
        }


        StringBuilder ret = new StringBuilder();
        if (stoppedBeforeEnd) {
            ret.append("Beware: Full study of safety might not have been fully completed\n");
        }

        for (SafetyProperty sp : safeties) {
            ret.append(sp.toString() + "\n");
        }
        return ret.toString();
    }
    
    
    public String reinitToString() {
        if (!studyReinit || initState == null) {
            return "Reinitialization check not activeted\n";
        }
        
        String ret;
        if (initState.getResult()) {
            ret = "property is satisfied\n";
        } else {
            ret = "property is NOT satisfied\n";
        }
        
        return ret;
    }
    

    // Do not free the RG
    public void freeUselessAllocations() {
        for (SpecificationState state : states.values()) {
            state.freeUselessAllocations();
        }
    }

    private synchronized void addState(SpecificationState newState) {
        newState.id = stateID;
        //newState.computeHash(blockValues);
        states.put(newState.getHash(blockValues), newState);
        statesByID.put(newState.id, newState);
        stateID++;
    }

    private synchronized SpecificationState addStateIfNotExisting(SpecificationState newState) {
        SpecificationState similar = states.get(newState.getHash(blockValues));
        if (similar == null) {
        	if (!(stateLimitRG && stateID >= stateLimit)) {
        		addState(newState);
        	} else {
        		return null;
    	    }
        }
        return similar;
    }

    private synchronized SpecificationState findSimilarState(SpecificationState newState) {
        SpecificationState similar = states.get(newState.getHash(blockValues));
        return similar;
    }

    private Map<Integer, SpecificationState> getRG() {
        return states;
    }

    private boolean stateIsReachableFromState(SpecificationState start, SpecificationState arrival) {
        Set<Long> visited= new HashSet<Long>();
        if (start.distance > arrival.distance) {
            return false;
        }
        return stateIsReachableFromStateRec(start, arrival, visited);
    }
    
    private boolean stateIsReachableFromStateRec(SpecificationState start, SpecificationState arrival, Set<Long> visited) {
        if (start == arrival) {
            return true;
        } else if (start.getNextsSize() == 0 || start.elaborated == false){
            return false;
        } else if (visited.contains(start.id)) {
            return false;
        }
        
        visited.add(start.id);
        for (SpecificationLink i : start.nexts) {
            if (stateIsReachableFromStateRec(i.destinationState, arrival, visited)) {
                return true;
            }
        }
        return false;
    }
    
}
