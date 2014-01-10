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
 * Class Main
 * starts the main Windows and a project manager
 * Creation: 01/12/2003
 * @version 1.0 21/08/2004
 * @author Ludovic APVRILLE
 * @see
 */



import myutil.*;


public class Test  {
   
	
    public static void main(String[] args) {
    	
    	testBoolExpr();
    	
    }
    
    public static void testBoolExpr() {
    	int nbOfPb = 0;
    	
    	nbOfPb += evalBool("t or f", true, false);
    	
    	nbOfPb += evalBool("(t) or f", true, false);
    	
    	nbOfPb += evalBool("(t) or f)", true, true);
    	
    	nbOfPb += evalBool("((t) or f)", true, false);
    	
    	nbOfPb += evalBool("((t) and f)", false, false);
    	
    	nbOfPb += evalBool("t and f and t", false, false);
    	
    	
    	nbOfPb += evalBool("(2==2)==f", false, false);
    	nbOfPb += evalBool("(2==1)==f", true, false);
    	nbOfPb += evalBool("(2>1)==f", false, false);
    	
    	nbOfPb += evalBool("f==2==2", false, false);
    	
    	nbOfPb += evalBool("((3>5)or(4<6))and(1>2)", false, false);
    	nbOfPb += evalBool("((3>5)or(4<6))and(2>1)", true, false);
    	nbOfPb += evalBool("((3>5)or or (4<6))and(2>1)", true, true);
    	
    	nbOfPb += evalBool("f or t)", true, true);
    	
    	System.out.println("Nb of problems found:" + nbOfPb);
    	
    	System.exit(-1);
    }
    
    public static int evalBool(String s, boolean expectedValue, boolean expectedError) {
    	BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    	boolean val = bee.getResultOf(s);
    	boolean err = bee.hasError();
    	
    	if ((val != expectedValue) || (err != expectedError)) {
    	
    	System.out.println("\n-------------------->Result of " + s + " =" + bee.getResultOf(s));
    	if (bee.hasError()) {
    		System.out.println("Error = " + bee.getFullError());
    	}
    	System.out.println("\n\n");
    	return 1;
    	}
    	
    	return 0;
    	
    	
    }

} // Class Test

