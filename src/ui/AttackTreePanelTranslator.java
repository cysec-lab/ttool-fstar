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
 * Class AvatarTreePanelTranslator
 * Creation: 13/04/2015
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

import java.util.*;

import myutil.*;
import ui.atd.*;

import attacktrees.*;
//import translator.*;
import ui.window.*;
import avatartranslator.*;


public class AttackTreePanelTranslator {

    protected AttackTree at;
    protected AttackTreePanel atp;
    protected Vector checkingErrors, warnings;
    protected CorrespondanceTGElement listE; // usual list
    //protected CorrespondanceTGElement listB; // list for particular element -> first element of group of blocks
    protected LinkedList <TDiagramPanel> panels;


    public AttackTreePanelTranslator(AttackTreePanel _atp) {
        atp = _atp;
        reinit();
    }

    public void reinit() {
        checkingErrors = new Vector();
        warnings = new Vector();
        listE = new CorrespondanceTGElement();
        panels = new LinkedList <TDiagramPanel>();
    }

    public Vector getCheckingErrors() {
        return checkingErrors;
    }

    public Vector getWarnings() {
        return warnings;
    }

    public CorrespondanceTGElement getCorrespondanceTGElement() {
        return listE;
    }

    public AttackTree translateToAttackTreeDataStructure() {

        at = new AttackTree("AttackTree", atp);


        for(TDiagramPanel panel: atp.panels) {
            if (panel instanceof AttackTreeDiagramPanel) {
                translate((AttackTreeDiagramPanel)panel);
            }
        }


        TraceManager.addDev("AT=" + at.toString());
        return at;

    }

    public void translate(AttackTreeDiagramPanel atdp) {
        LinkedList<TGComponent> allComponents = (LinkedList<TGComponent>)(atdp.getAllComponentList());

        int nodeID = 0;
        TGComponent father;

        //Create attacks, nodes
        for(TGComponent comp: allComponents) {
            if (comp instanceof ATDAttack) {
                ATDAttack atdatt = (ATDAttack)comp;
                Attack att;
                String value = atdatt.getValue();
                father = atdatt.getFather();
                if ((father != null) && (father instanceof ATDBlock)) {
                    value = ((ATDBlock)father).getNodeName() + "__" + value;

                }
                att = new Attack(value, atdatt);
                att.setRoot(atdatt.isRootAttack());
                at.addAttack(att);
                listE.addCor(att, comp);
            }
            if (comp instanceof ATDConstraint) {
                ATDConstraint cons = (ATDConstraint)comp;
                nodeID ++;

                //OR
                if (cons.isOR()) {
                    ORNode ornode = new ORNode("OR__" + nodeID, cons);
                    at.addNode(ornode);
                    listE.addCor(ornode, comp);

                    //AND
                } else if (cons.isAND()) {
                    ANDNode andnode = new ANDNode("AND__" + nodeID, cons);
                    at.addNode(andnode);
                    listE.addCor(andnode, comp);

                    //XOR
                } else if (cons.isXOR()) {
                    XORNode xornode = new XORNode("XOR__" + nodeID, cons);
                    at.addNode(xornode);
                    listE.addCor(xornode, comp);

                    //SEQUENCE
                } else if (cons.isSequence()) {
                    SequenceNode seqnode = new SequenceNode("SEQUENCE__" + nodeID, cons);
                    at.addNode(seqnode);
                    listE.addCor(seqnode, comp);

                    //BEFORE
                } else if (cons.isBefore()) {
                    String eq = cons.getEquation();
                    int time;
                    try {
                        time = Integer.decode(eq).intValue();
                        BeforeNode befnode = new BeforeNode("BEFORE__" + nodeID, cons, time);
                        at.addNode(befnode);
                        listE.addCor(befnode, comp);
                    } catch (Exception e) {
                        CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Invalid time in before node");
                        ce.setTGComponent(comp);
                        ce.setTDiagramPanel(atdp);
                        addCheckingError(ce);
                    }

                    //AFTER
                } else if (cons.isAfter()) {
                    String eq = cons.getEquation();
                    int time;
                    try {
                        time = Integer.decode(eq).intValue();
                        AfterNode aftnode = new AfterNode("AFTER__" + nodeID, cons, time);
                        at.addNode(aftnode);
                        listE.addCor(aftnode, comp);
                    } catch (Exception e) {
                        CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Invalid time in after node");
                        ce.setTGComponent(comp);
                        ce.setTDiagramPanel(atdp);
                        addCheckingError(ce);
                    }

                }  else {
                    CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Invalid attack node");
                    ce.setTGComponent(comp);
                    ce.setTDiagramPanel(atdp);
                    addCheckingError(ce);
                }


            }


        }

        // Making connections between nodes&attacks
        TGComponent tgc1, tgc2;
        for(TGComponent comp: allComponents) {
            if (comp instanceof ATDAttackConnector) {
                ATDAttackConnector con = (ATDAttackConnector)(comp);
                tgc1 = atdp.getComponentToWhichBelongs(con.getTGConnectingPointP1());
                tgc2 = atdp.getComponentToWhichBelongs(con.getTGConnectingPointP2());
                if ( ((tgc1 instanceof ATDAttack) || (tgc1 instanceof ATDConstraint)) &&
                     ((tgc2 instanceof ATDAttack) || (tgc2 instanceof ATDConstraint)) ) {
                    try {
                        // We must transpose this into attack -> node or node -> attack

                        // Attack -> attack
                        if ((tgc1 instanceof ATDAttack) && (tgc2 instanceof ATDAttack)) {
                            // We link the two attacks with an "and" node
                            Attack at1 = (Attack)(listE.getObject(tgc1));
                            Attack at2 = (Attack)(listE.getObject(tgc2));
                            nodeID ++;
                            ANDNode andnode = new ANDNode("ANDBetweenAttacks__" + nodeID + "__" + at1.getName() + "__" + at2.getName(), tgc1);
                            at.addNode(andnode);
                            listE.addCor(andnode, comp);
                            at1.addDestinationNode(andnode);
                            at2.setOriginNode(andnode);
                            andnode.addInputAttack(at1, new Integer("0"));
                            andnode.setResultingAttack(at2);


                            // Attack -> node
                        } else if ((tgc1 instanceof ATDAttack) && (tgc2 instanceof ATDConstraint)) {
                            Attack at1 = (Attack)(listE.getObject(tgc1));
                            AttackNode node1 = (AttackNode)(listE.getObject(tgc2));
                            at1.addDestinationNode(node1);
                            String val = comp.getValue().trim();
                            if (val.length() == 0) {
                                val = "0";
                            }
                            node1.addInputAttack(at1, new Integer(val));

                            // Node -> attack
                        } else if ((tgc1 instanceof ATDConstraint) && (tgc2 instanceof ATDAttack)) {
                            Attack at1 = (Attack)(listE.getObject(tgc2));
                            AttackNode node1 = (AttackNode)(listE.getObject(tgc1));
                            at1.setOriginNode(node1);
                            if (node1.getResultingAttack() != null) {
                                // Already a resulting attack -> error
                                CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Too many resulting attacks");
                                ce.setTGComponent(tgc1);
                                ce.setTDiagramPanel(atdp);
                                addCheckingError(ce);
                            } else {
                                node1.setResultingAttack(at1);
                            }

                            // Node -> Node
                        } else if ((tgc1 instanceof ATDConstraint) && (tgc2 instanceof ATDConstraint)) {
                            AttackNode node1 = (AttackNode)(listE.getObject(tgc1));
                            AttackNode node2 = (AttackNode)(listE.getObject(tgc2));
                            // Make fake attack
                            Attack att = new Attack("Attack__from_" + node1.getName() + "_to_" + node2.getName(), tgc1);
                            att.setRoot(false);
                            at.addAttack(att);
                            listE.addCor(att, comp);

                            att.setOriginNode(node1);
                            att.addDestinationNode(node2);

                            if (node1.getResultingAttack() != null) {
                                // Already a resulting attack -> error
                                CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Too many resulting attacks");
                                ce.setTGComponent(tgc1);
                                ce.setTDiagramPanel(atdp);
                                addCheckingError(ce);
                            } else {
                                node1.setResultingAttack(att);
                            }

                            String val = comp.getValue().trim();
                            if (val.length() == 0) {
                                val = "0";
                            }
                            node2.addInputAttack(att, new Integer(val));
                        }

                    } catch (Exception e) {
                        CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Badly formed connector");
                        ce.setTGComponent(comp);
                        ce.setTDiagramPanel(atdp);
                        addCheckingError(ce);
                    }
                }
            }
        }

    }


    public AvatarSpecification generateAvatarSpec() {
        AvatarSpecification as = new AvatarSpecification("spec from attack trees", atp);
        // One block per attacknode to receive the attack
        // One block per attack -> syncho
        // One mast block with all channels declared at that level
        AvatarBlock mainBlock = new AvatarBlock("MainBlock", null);
        as.addBlock(mainBlock);

        // Declare all attacks
        declareAllAttacks(as, mainBlock);

        // Make block for attacks
        makeLeafAttackBlocks(as, mainBlock);

        // Make blocks for nodes
        makeAttackNodeBlocks(as, mainBlock);



        return as;
    }

    private void declareAllAttacks(AvatarSpecification _as, AvatarBlock _main) {
        AvatarRelation ar = new AvatarRelation("MainRelation", _main, _main, null);
        ar.setAsynchronous(false);
        ar.setPrivate(true);
        ar.setBroadcast(false);

        _as.addRelation(ar);
        for(Attack attack: at.getAttacks()) {
            avatartranslator.AvatarSignal makeAttack = new avatartranslator.AvatarSignal("make__" + attack.getName(), AvatarSignal.OUT, (Object)(listE.getTG(attack)));
            _main.addSignal(makeAttack);
            avatartranslator.AvatarSignal stopMakeAttack = new avatartranslator.AvatarSignal("makeStop__" + attack.getName(), AvatarSignal.IN, listE.getTG(attack));
            _main.addSignal(stopMakeAttack);
            avatartranslator.AvatarSignal acceptAttack = new avatartranslator.AvatarSignal("accept__" + attack.getName(), AvatarSignal.IN, listE.getTG(attack));
            _main.addSignal(acceptAttack);
            avatartranslator.AvatarSignal stopAcceptAttack = new avatartranslator.AvatarSignal("acceptStop__" + attack.getName(), AvatarSignal.OUT, listE.getTG(attack));
            _main.addSignal(stopAcceptAttack);
            ar.addSignals(makeAttack, acceptAttack);
            ar.addSignals(stopMakeAttack, stopAcceptAttack);
        }
    }

    private void makeLeafAttackBlocks(AvatarSpecification _as, AvatarBlock _main) {
        for(Attack attack: at.getAttacks()) {
            if (attack.isLeaf()) {
                // Make the block
                AvatarBlock ab = new AvatarBlock(attack.getName(), listE.getTG(attack));
                _as.addBlock(ab);
                ab.setFather(_main);

                avatartranslator.AvatarSignal sigAttack = _main.getAvatarSignalWithName("make__" + attack.getName());
                avatartranslator.AvatarSignal stopAttack = _main.getAvatarSignalWithName("makeStop__" + attack.getName());

                if ((sigAttack != null) && (stopAttack != null)) {
                    makeAttackBlockSMD(ab, sigAttack, stopAttack, listE.getTG(attack));
                }

            } else if (attack.isFinal()) {
                // Make the block
                AvatarBlock ab = new AvatarBlock(attack.getName(), listE.getTG(attack));
                _as.addBlock(ab);
                ab.setFather(_main);

                avatartranslator.AvatarSignal sigAttack = _main.getAvatarSignalWithName("accept__" + attack.getName());
                avatartranslator.AvatarSignal stopAttack = _main.getAvatarSignalWithName("acceptStop__" + attack.getName());

                makeAttackBlockSMD(ab, sigAttack, stopAttack, listE.getTG(attack));

            }
        }
    }

    private void makeAttackBlockSMD(AvatarBlock _ab, avatartranslator.AvatarSignal _sigAttack, avatartranslator.AvatarSignal _sigStop, Object _ref) {
	Object _ref1 = _ref;
	_ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();

        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false);
	AvatarState performedState = new AvatarState("main", _ref1, true);
        AvatarState mainStop = new AvatarState("stop", _ref, false);
        AvatarActionOnSignal getMake = new AvatarActionOnSignal("GettingAttack", _sigAttack, _ref1);
        AvatarActionOnSignal getStop = new AvatarActionOnSignal("GettingStop", _sigStop, _ref);

        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
	asm.addElement(performedState);
        asm.addElement(getMake);
        asm.addElement(getStop);


        AvatarTransition at = new AvatarTransition("at1", _ref);
        asm.addElement(at);
        start.addNext(at);
        at.addNext(mainState);

        at = new AvatarTransition("at2", _ref);
        asm.addElement(at);
        mainState.addNext(at);
        at.addNext(getMake);

        at = new AvatarTransition("at3", _ref);
        asm.addElement(at);
        getMake.addNext(at);
        at.addNext(performedState);

	at = new AvatarTransition("backToMain", _ref);
        asm.addElement(at);
        performedState.addNext(at);
        at.addNext(mainState);

        at = new AvatarTransition("at4", _ref);
        asm.addElement(at);
        mainState.addNext(at);
        at.addNext(getStop);

        at = new AvatarTransition("at5", _ref);
        asm.addElement(at);
        getStop.addNext(at);
        at.addNext(mainStop);

    }


    private void makeAttackNodeBlocks(AvatarSpecification _as, AvatarBlock _main) {
        Attack att;

        for(AttackNode node: at.getAttackNodes()) {
            if (node.isWellFormed()) {
                // Make the block
                AvatarBlock ab = new AvatarBlock(node.getName(), listE.getTG(node));
                _as.addBlock(ab);
                ab.setFather(_main);

                if (node instanceof ANDNode) {
                    makeANDNode(_as, _main, ab, (ANDNode)node, listE.getTG(node));
                } else if (node instanceof ORNode) {
                    makeORNode(_as, _main, ab, (ORNode)node, listE.getTG(node));
                } else if (node instanceof XORNode) {
                    makeXORNode(_as, _main, ab, (XORNode)node, listE.getTG(node));
                } else if (node instanceof SequenceNode) {
                    makeSequenceNode(_as, _main, ab, (SequenceNode)node, listE.getTG(node));
                } else if (node instanceof AfterNode) {
                    makeAfterNode(_as, _main, ab, (AfterNode)node, listE.getTG(node));
                } else if (node instanceof BeforeNode) {
                    makeBeforeNode(_as, _main, ab, (BeforeNode)node, listE.getTG(node));
                }
            }
        }
    }


    private void makeANDNode(AvatarSpecification _as, AvatarBlock _main, AvatarBlock _ab, ANDNode _node, Object _ref) {
	Object _ref1 = _ref;
	_ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();

        // Basic machine
        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false);
        AvatarState endState = new AvatarState("end", _ref, false);
        AvatarState overallState = new AvatarState("overall", _ref, false);
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
        asm.addElement(endState);
        asm.addElement(overallState);
        AvatarTransition atF = new AvatarTransition("at1", _ref);
        asm.addElement(atF);
        start.addNext(atF);
        atF.addNext(mainState);
        String finalGuard = "";
        for(Attack att: _node.getInputAttacks()) {
            AvatarAttribute aa = new AvatarAttribute(att.getName() + "__performed", AvatarType.BOOLEAN, _ref);
            if (finalGuard.length() ==0) {
                finalGuard += "(" + att.getName() + "__performed == true)";
            } else {
                finalGuard += " && (" + att.getName() + "__performed == true)";
            }
            _ab.addAttribute(aa);
            atF.addAction(att.getName() + "__performed = false");

            avatartranslator.AvatarSignal sigAtt = _main.getAvatarSignalWithName("accept__" + att.getName());
            AvatarActionOnSignal acceptAttack = new AvatarActionOnSignal("AcceptAttack", sigAtt, _ref1);
            asm.addElement(acceptAttack);
            AvatarTransition at = new AvatarTransition("at_toInputAttack", _ref);
            asm.addElement(at);
            mainState.addNext(at);
            at.addNext(acceptAttack);
            at.setGuard("["+att.getName() + "__performed == false]");
            at = new AvatarTransition("at_fromInputAttack", _ref);
            at.addAction(att.getName() + "__performed = true");
            asm.addElement(at);
            acceptAttack.addNext(at);
            at.addNext(mainState);


        }

        // Adding resulting attack
        AvatarTransition at = new AvatarTransition("at_toEnd", _ref);
        asm.addElement(at);
        mainState.addNext(at);
        at.addNext(endState);
        at.setGuard("[" + finalGuard + "]");

        Attack resulting = _node.getResultingAttack();
        avatartranslator.AvatarSignal sigAttack = _main.getAvatarSignalWithName("make__" + resulting.getName());
        AvatarActionOnSignal resultingAttack = new AvatarActionOnSignal("ResultingAttack", sigAttack, _ref1);
        asm.addElement(resultingAttack);
        at = new AvatarTransition("at_toResultingAttack", _ref);
        asm.addElement(at);
        endState.addNext(at);
        at.addNext(resultingAttack);
        at = new AvatarTransition("at_Overall", _ref);
        asm.addElement(at);
        resultingAttack.addNext(at);
        at.addNext(overallState);

    }


    private void makeORNode(AvatarSpecification _as, AvatarBlock _main, AvatarBlock _ab, ORNode _node, Object _ref) {
	Object _ref1 = _ref;
	_ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();

        // Basic machine
        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false);
        AvatarState endState = new AvatarState("end", _ref, false);
        AvatarState overallState = new AvatarState("overall", _ref, false);
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
        asm.addElement(endState);
        asm.addElement(overallState);

        AvatarTransition atF = new AvatarTransition("at1", _ref);
        asm.addElement(atF);
        start.addNext(atF);
        atF.addNext(mainState);
        String finalGuard = "";
        for(Attack att: _node.getInputAttacks()) {
            AvatarAttribute aa = new AvatarAttribute(att.getName() + "__performed", AvatarType.BOOLEAN, _ref);
            if (finalGuard.length() ==0) {
                finalGuard += "(" + att.getName() + "__performed == true)";
            } else {
                finalGuard += " || (" + att.getName() + "__performed == true)";
            }
            _ab.addAttribute(aa);
            atF.addAction(att.getName() + "__performed = false");

            // From Main
            avatartranslator.AvatarSignal sigAtt = _main.getAvatarSignalWithName("accept__" + att.getName());
            AvatarActionOnSignal acceptAttack = new AvatarActionOnSignal("AcceptAttack", sigAtt, _ref1);
            asm.addElement(acceptAttack);
            AvatarTransition at = new AvatarTransition("at_toInputAttack", _ref);
            asm.addElement(at);
            mainState.addNext(at);
            at.addNext(acceptAttack);
            at.setGuard("["+att.getName() + "__performed == false]");
            at = new AvatarTransition("at_fromInputAttack", _ref);
            at.addAction(att.getName() + "__performed = true");
            asm.addElement(at);
            acceptAttack.addNext(at);
            at.addNext(mainState);

            // Link from End
            acceptAttack = new AvatarActionOnSignal("AcceptAttack", sigAtt, _ref1);
            asm.addElement(acceptAttack);
            at = new AvatarTransition("at_toInputAttack", _ref);
            asm.addElement(at);
            endState.addNext(at);
            at.addNext(acceptAttack);
            at.setGuard("["+att.getName() + "__performed == false]");
            at = new AvatarTransition("at_fromInputAttack", _ref);
            at.addAction(att.getName() + "__performed = true");
            asm.addElement(at);
            acceptAttack.addNext(at);
            at.addNext(endState);

            // Link from Overall
            acceptAttack = new AvatarActionOnSignal("AcceptAttack", sigAtt, _ref1);
            asm.addElement(acceptAttack);
            at = new AvatarTransition("at_toInputAttack", _ref);
            asm.addElement(at);
            overallState.addNext(at);
            at.addNext(acceptAttack);
            at.setGuard("["+att.getName() + "__performed == false]");
            at = new AvatarTransition("at_fromInputAttack", _ref);
            at.addAction(att.getName() + "__performed = true");
            asm.addElement(at);
            acceptAttack.addNext(at);
            at.addNext(overallState);


        }


	// Adding resulting attack
        AvatarTransition at = new AvatarTransition("at_toEnd", _ref);
        asm.addElement(at);
        mainState.addNext(at);
        at.addNext(endState);
        at.setGuard("[" + finalGuard + "]");

        Attack resulting = _node.getResultingAttack();
        avatartranslator.AvatarSignal sigAttack = _main.getAvatarSignalWithName("make__" + resulting.getName());
        AvatarActionOnSignal resultingAttack = new AvatarActionOnSignal("ResultingAttack", sigAttack, _ref1);
        asm.addElement(resultingAttack);
        at = new AvatarTransition("at_toResultingAttack", _ref);
        asm.addElement(at);
        endState.addNext(at);
        at.addNext(resultingAttack);
        at = new AvatarTransition("at_Overall", _ref);
        asm.addElement(at);
        resultingAttack.addNext(at);
        at.addNext(overallState);

    }


    private void makeXORNode(AvatarSpecification _as, AvatarBlock _main, AvatarBlock _ab, XORNode _node, Object _ref) {
	Object _ref1 = _ref;
	_ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();

        // Basic machine
        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false);
	AvatarState stoppingAll = new AvatarState("stoppingAll", _ref, false);
        AvatarState endState = new AvatarState("end", _ref, false);
        AvatarState overallState = new AvatarState("overall", _ref, false);
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
        asm.addElement(endState);
        asm.addElement(overallState);
	asm.addElement(stoppingAll);

	
        AvatarTransition atF = new AvatarTransition("at1", _ref);
        asm.addElement(atF);
        start.addNext(atF);
        atF.addNext(mainState);
        String finalGuard = "oneDone == true";
	String toEndGuard = "";
	AvatarAttribute oneDone = new AvatarAttribute("oneDone", AvatarType.BOOLEAN, _ref);
	_ab.addAttribute(oneDone);
	atF.addAction("oneDone = false");
        for(Attack att: _node.getInputAttacks()) {
            AvatarAttribute aa = new AvatarAttribute(att.getName() + "__performed", AvatarType.BOOLEAN, _ref);
            _ab.addAttribute(aa);
            atF.addAction(att.getName() + "__performed = false");
	    if (toEndGuard.length() ==0) {
                toEndGuard += "(" + att.getName() + "__performed == true)";
            } else {
                toEndGuard += " && (" + att.getName() + "__performed == true)";
            }
            // From Main
            avatartranslator.AvatarSignal sigAtt = _main.getAvatarSignalWithName("accept__" + att.getName());
            AvatarActionOnSignal acceptAttack = new AvatarActionOnSignal("AcceptAttack", sigAtt, _ref1);
            asm.addElement(acceptAttack);
            AvatarTransition at = new AvatarTransition("at_toInputAttack", _ref);
            asm.addElement(at);
            mainState.addNext(at);
            at.addNext(acceptAttack);
            at.setGuard("[("+att.getName() + "__performed == false) && (oneDone == false)]");
            at = new AvatarTransition("at_fromInputAttack", _ref);
            at.addAction(att.getName() + "__performed = true");
	    at.addAction("oneDone = true");
            asm.addElement(at);
            acceptAttack.addNext(at);
            at.addNext(mainState);

            // Link from stoppingAll
	    sigAtt = _main.getAvatarSignalWithName("acceptStop__" + att.getName());
            acceptAttack = new AvatarActionOnSignal("StopAttack", sigAtt, _ref);
            asm.addElement(acceptAttack);
            at = new AvatarTransition("at_toInputAttack", _ref);
            asm.addElement(at);
            stoppingAll.addNext(at);
            at.addNext(acceptAttack);
            at.setGuard("["+att.getName() + "__performed == false]");
            at = new AvatarTransition("at_fromInputAttack", _ref);
            at.addAction(att.getName() + "__performed = true");
            asm.addElement(at);
            acceptAttack.addNext(at);
            at.addNext(stoppingAll);

         }

	// Adding link to stopping all
	AvatarTransition at = new AvatarTransition("at_toStoppingAll", _ref);
        asm.addElement(at);
        mainState.addNext(at);
        at.addNext(stoppingAll);
        at.setGuard("[" + finalGuard + "]");


        // Adding resulting attack
        at = new AvatarTransition("at_toEnd", _ref);
        asm.addElement(at);
        stoppingAll.addNext(at);
        at.addNext(endState);
        at.setGuard("[" + toEndGuard + "]");

        Attack resulting = _node.getResultingAttack();
        avatartranslator.AvatarSignal sigAttack = _main.getAvatarSignalWithName("make__" + resulting.getName());
        AvatarActionOnSignal resultingAttack = new AvatarActionOnSignal("ResultingAttack", sigAttack, _ref1);
        asm.addElement(resultingAttack);
        at = new AvatarTransition("at_toResultingAttack", _ref);
        asm.addElement(at);
        endState.addNext(at);
        at.addNext(resultingAttack);
        at = new AvatarTransition("at_Overall", _ref);
        asm.addElement(at);
        resultingAttack.addNext(at);
        at.addNext(overallState);

    }



    private void makeSequenceNode(AvatarSpecification _as, AvatarBlock _main, AvatarBlock _ab, SequenceNode _node, Object _ref) {
	Object _ref1 = _ref;
	_ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();
        _node.orderAttacks();

        // Basic machine
        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false);
        AvatarState endState = new AvatarState("end", _ref, false);
        AvatarState overallState = new AvatarState("overall", _ref, false);
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
        asm.addElement(endState);
        asm.addElement(overallState);


        AvatarTransition at = new AvatarTransition("at", _ref);
        asm.addElement(at);
        start.addNext(at);
        at.addNext(mainState);

        AvatarState previousState = mainState;

        // Chaining accept attacks
        for(Attack att: _node.getInputAttacks()) {
            AvatarState state = new AvatarState("state__" + att.getName(), _ref);
            asm.addElement(state);
            avatartranslator.AvatarSignal sigAtt = _main.getAvatarSignalWithName("accept__" + att.getName());
            AvatarActionOnSignal acceptAttack = new AvatarActionOnSignal("AcceptAttack", sigAtt, _ref1);
            asm.addElement(acceptAttack);

            at = new AvatarTransition("at", _ref);
            asm.addElement(at);
            previousState.addNext(at);
            at.addNext(acceptAttack);
            at = new AvatarTransition("at", _ref);
            asm.addElement(at);
            acceptAttack.addNext(at);
            at.addNext(state);
            previousState = state;
        }

        at = new AvatarTransition("at", _ref);
        asm.addElement(at);
        previousState.addNext(at);
        at.addNext(endState);


        // Performing resulting attack
        Attack resulting = _node.getResultingAttack();
        avatartranslator.AvatarSignal sigAttack = _main.getAvatarSignalWithName("make__" + resulting.getName());
        AvatarActionOnSignal resultingAttack = new AvatarActionOnSignal("ResultingAttack", sigAttack, _ref1);
        asm.addElement(resultingAttack);
        at = new AvatarTransition("at_toResultingAttack", _ref);
        asm.addElement(at);
        endState.addNext(at);
        at.addNext(resultingAttack);
        at = new AvatarTransition("at_Overall", _ref);
        asm.addElement(at);
        resultingAttack.addNext(at);
        at.addNext(overallState);
    }

    private void makeAfterNode(AvatarSpecification _as, AvatarBlock _main, AvatarBlock _ab, AfterNode _node, Object _ref) {
	Object _ref1 = _ref;
	_ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();
        _node.orderAttacks();

        // Basic machine
        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false);
        AvatarState endState = new AvatarState("end", _ref, false);
        AvatarState overallState = new AvatarState("overall", _ref, false);
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
        asm.addElement(endState);
        asm.addElement(overallState);


        AvatarTransition at = new AvatarTransition("at", _ref);
        asm.addElement(at);
        start.addNext(at);
        at.addNext(mainState);

        AvatarState previousState = mainState;

        // Chaining accept attacks
        int cpt = 0;
        for(Attack att: _node.getInputAttacks()) {
            AvatarState state = new AvatarState("state__" + att.getName(), _ref);
            asm.addElement(state);
            avatartranslator.AvatarSignal sigAtt = _main.getAvatarSignalWithName("accept__" + att.getName());
            AvatarActionOnSignal acceptAttack = new AvatarActionOnSignal("AcceptAttack", sigAtt, _ref1);
            asm.addElement(acceptAttack);

            at = new AvatarTransition("at", _ref);
            asm.addElement(at);
            previousState.addNext(at);
            at.addNext(acceptAttack);
            if (cpt > 0) {
                at.setDelays("" + _node.getTime(), "" + _node.getTime());
            }
            at = new AvatarTransition("at", _ref);
            asm.addElement(at);
            acceptAttack.addNext(at);
            at.addNext(state);
            previousState = state;
            cpt ++;
        }

        at = new AvatarTransition("at", _ref);
        asm.addElement(at);
        previousState.addNext(at);
        at.addNext(endState);


        // Performing resulting attack
        Attack resulting = _node.getResultingAttack();
        avatartranslator.AvatarSignal sigAttack = _main.getAvatarSignalWithName("make__" + resulting.getName());
        AvatarActionOnSignal resultingAttack = new AvatarActionOnSignal("ResultingAttack", sigAttack, _ref1);
        asm.addElement(resultingAttack);
        at = new AvatarTransition("at_toResultingAttack", _ref);
        asm.addElement(at);
        endState.addNext(at);
        at.addNext(resultingAttack);
        at = new AvatarTransition("at_Overall", _ref);
        asm.addElement(at);
        resultingAttack.addNext(at);
        at.addNext(overallState);
    }

    private void makeBeforeNode(AvatarSpecification _as, AvatarBlock _main, AvatarBlock _ab, BeforeNode _node, Object _ref) {
	Object _ref1 = _ref;
	_ref = null;
        AvatarStateMachine asm = _ab.getStateMachine();
        _node.orderAttacks();

        // Basic machine
        AvatarStartState start = new AvatarStartState("start", _ref);
        AvatarState mainState = new AvatarState("main", _ref, false);
        AvatarState endState = new AvatarState("end", _ref, false);
        AvatarState overallState = new AvatarState("overall", _ref, false);
        AvatarState timeout = new AvatarState("timeout", _ref, false);
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(mainState);
        asm.addElement(endState);
        asm.addElement(overallState);
        asm.addElement(timeout);


        AvatarTransition at = new AvatarTransition("at", _ref);
        asm.addElement(at);
        start.addNext(at);
        at.addNext(mainState);

        AvatarState previousState = mainState;

        // Chaining accept attacks
        int cpt = 0;
        for(Attack att: _node.getInputAttacks()) {
            AvatarState state = new AvatarState("state__" + att.getName(), _ref);
            asm.addElement(state);
            avatartranslator.AvatarSignal sigAtt = _main.getAvatarSignalWithName("accept__" + att.getName());
            AvatarActionOnSignal acceptAttack = new AvatarActionOnSignal("AcceptAttack", sigAtt, _ref1);
            asm.addElement(acceptAttack);

            at = new AvatarTransition("at", _ref);
            asm.addElement(at);
            previousState.addNext(at);
            at.addNext(acceptAttack);
            if (cpt > 0) {
                at = new AvatarTransition("at_totimeout", _ref);
                asm.addElement(at);
                previousState.addNext(at);
                at.addNext(timeout);
                at.setDelays("" + _node.getTime(), "" + _node.getTime());
            }
            at = new AvatarTransition("at", _ref);
            asm.addElement(at);
            acceptAttack.addNext(at);
            at.addNext(state);
            previousState = state;
            cpt ++;
        }

        at = new AvatarTransition("at", _ref);
        asm.addElement(at);
        previousState.addNext(at);
        at.addNext(endState);


        // Performing resulting attack
        Attack resulting = _node.getResultingAttack();
        avatartranslator.AvatarSignal sigAttack = _main.getAvatarSignalWithName("make__" + resulting.getName());
        AvatarActionOnSignal resultingAttack = new AvatarActionOnSignal("ResultingAttack", sigAttack, _ref1);
        asm.addElement(resultingAttack);
        at = new AvatarTransition("at_toResultingAttack", _ref);
        asm.addElement(at);
        endState.addNext(at);
        at.addNext(resultingAttack);
        at = new AvatarTransition("at_Overall", _ref);
        asm.addElement(at);
        resultingAttack.addNext(at);
        at.addNext(overallState);
    }




    /*public void createBlocks(AvatarSpecification _as, LinkedList<AvatarBDBlock> _blocks) {
      AvatarBlock ab;
      Vector v;
      TAttribute a;
      int i;
      AvatarAttribute aa;
      ui.AvatarMethod uiam;
      ui.AvatarSignal uias;
      avatartranslator.AvatarMethod atam;
      avatartranslator.AvatarSignal atas;
      TGComponent tgc1, tgc2;
      Vector types;

      for(AvatarBDBlock block: _blocks) {
      ab = new AvatarBlock(block.getBlockName(), block);
      _as.addBlock(ab);
      listE.addCor(ab, block);
      block.setAVATARID(ab.getID());

      // Create attributes
      v = block.getAttributeList();
      for(i=0; i<v.size(); i++) {
      a = (TAttribute)(v.elementAt(i));
      if (a.getType() == TAttribute.INTEGER){
      addRegularAttribute(ab, a, "");
      } else if (a.getType() == TAttribute.NATURAL){
      addRegularAttribute(ab, a, "");
      } else if (a.getType() == TAttribute.BOOLEAN) {
      addRegularAttribute(ab, a, "");
      } else if (a.getType() == TAttribute.TIMER) {
      addRegularAttribute(ab, a, "");
      } else {
      // other
      //TraceManager.addDev(" -> Other type found: " + a.getTypeOther());
      types = adp.getAvatarBDPanel().getAttributesOfDataType(a.getTypeOther());
      if (types == null) {
      CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + a.getTypeOther() + " used in " + ab.getName());
      ce.setAvatarBlock(ab);
      ce.setTDiagramPanel(adp.getAvatarBDPanel());
      addCheckingError(ce);
      return;
      } else {
      if (types.size() ==0) {
      CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Data type definition must contain at least one attribute:  " + ab.getName());
      ce.setAvatarBlock(ab);
      ce.setTDiagramPanel(adp.getAvatarBDPanel());
      addCheckingError(ce);
      } else {
      for(int j=0; j<types.size(); j++) {
      addRegularAttribute(ab, (TAttribute)(types.elementAt(j)), a.getId() + "__");
      }
      }
      }

      }
      }

      // Create methods
      v = block.getMethodList();
      for(i=0; i<v.size(); i++) {
      uiam = (AvatarMethod)(v.get(i));
      atam = new avatartranslator.AvatarMethod(uiam.getId(), uiam);
      atam.setImplementationProvided(uiam.isImplementationProvided());
      ab.addMethod(atam);
      makeParameters(ab, atam, uiam);
      makeReturnParameters(ab, block, atam, uiam);
      }
      // Create signals
      v = block.getSignalList();
      for(i=0; i<v.size(); i++) {
      uias = (AvatarSignal)(v.get(i));

      if (uias.getInOut() == uias.IN) {
      atas = new avatartranslator.AvatarSignal(uias.getId(), avatartranslator.AvatarSignal.IN, uias);
      } else {
      atas = new avatartranslator.AvatarSignal(uias.getId(), avatartranslator.AvatarSignal.OUT, uias);
      }
      ab.addSignal(atas);
      makeParameters(ab, atas, uias);
      }

      // Put global code
      ab.addGlobalCode(block.getGlobalCode());

      }

      // Make block hierarchy
      for(AvatarBlock block: _as.getListOfBlocks()) {
      tgc1 = listE.getTG(block);
      if ((tgc1 != null) && (tgc1.getFather() != null)) {
      tgc2 = tgc1.getFather();
      ab = listE.getAvatarBlock(tgc2);
      if (ab != null) {
      block.setFather(ab);
      }
      }
      }
      }*/


    /*}

    //TraceManager.addDev("Size of vector:" + v.size());
    for(i=0; i<v.size(); i++) {
    aa = _ab.getAvatarAttributeWithName((String)(v.get(i)));
    if (aa == null) {
    CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + _name + " in signal expression: " + _idOperator);
    ce.setAvatarBlock(_ab);
    ce.setTDiagramPanel(_tdp);
    ce.setTGComponent(_tgc);
    addCheckingError(ce);
    return ;
    } else {
    //TraceManager.addDev("-> Adding attr in action on signal in block " + _ab.getName() + ":" + _name + "__" + tatmp.getId());
    _aaos.addValue((String)(v.get(i)));
    }
    }


    }*/


    private void addCheckingError(CheckingError ce) {
        if (checkingErrors == null) {
            checkingErrors = new Vector();
        }
        checkingErrors.addElement(ce);
    }

    private void addWarning(CheckingError ce) {
        if (warnings == null) {
            warnings = new Vector();
        }
        warnings.addElement(ce);
    }



}