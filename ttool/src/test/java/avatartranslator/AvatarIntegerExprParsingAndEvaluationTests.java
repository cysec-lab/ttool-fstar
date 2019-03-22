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
 * Class AvatarGuardTests
 * Creation: 20/05/2015
 * @version 1.1 01/07/2015
 * @author Ludovic APVRILLE, Letitia LI
 * @see
 */

package avatartranslator;

import myutil.Conversion;
import myutil.IntExpressionEvaluator;
import org.junit.Test;
import org.junit.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import java.util.LinkedList;
import java.util.HashMap;
import java.util.Vector;


import ui.TAttribute;
import avatartranslator.*;

public class AvatarIntegerExprParsingAndEvaluationTests {

    AvatarGuard res;
    AvatarBlock block;

    public AvatarIntegerExprParsingAndEvaluationTests () {
        //  super ("AvatarGuards", false);
    }
    @Before
    public void test () {
        AvatarSpecification as = new AvatarSpecification("avatarspecification", null);

        block = new AvatarBlock("myblock", as, null);
        as.addBlock(block);
        AvatarAttribute x = new AvatarAttribute("x", AvatarType.INTEGER, block, null);
        block.addAttribute(x);
        AvatarAttribute y = new AvatarAttribute("y", AvatarType.INTEGER, block, null);
        block.addAttribute(y);
        AvatarAttribute z = new AvatarAttribute("z", AvatarType.INTEGER, block, null);
        block.addAttribute(z);

        x.setInitialValue("10");
        y.setInitialValue("5");
        z.setInitialValue("2");

    }


    private void testExpr(String command, double expectedResult, boolean expectedBool) {


        AvatarTransition at = new AvatarTransition(block, "at", null);
        at.addAction(command);

        IntExpressionEvaluator iee = new IntExpressionEvaluator();

        String expr = at.getAction(0).toString();
        int index = expr.indexOf('=');

        assertTrue(index > -1);

        expr = expr.substring(index+1, expr.length()).trim();

        for(AvatarAttribute aa: block.getAttributes()) {
            expr = Conversion.putVariableValueInString(AvatarSpecification.ops, expr, aa.getName(), aa.getInitialValue());
        }

        double result = iee.getResultOf(expr);

        assertTrue((result == expectedResult) == expectedBool);
    }

    @Test
    public void testIntExpr(){
        testExpr("x = x + y", 15.0, true);

        testExpr("x = x + y", 16.0, false);

        testExpr("x = x + x*(y+z)", 80.0, true);

        testExpr("x = x + x*(y+z)/z", 45, true);

        testExpr("x = x + x*(y+z)/(x + z - x)", 45, true);

        testExpr("x = x + x*(y+z)*(x - z)", 570, true);

        //testExpr("x = (x + y)*z", 30, true);

        //testExpr("x = (x + y)*z + (x+z)/z", 36, true);


        /*res= AvatarGuard.createFromString(A, "else");
        assertTrue(res instanceof AvatarGuardElse);*/
    }



    public static void main(String[] args){
        AvatarIntegerExprParsingAndEvaluationTests apt = new AvatarIntegerExprParsingAndEvaluationTests ();
        //apt.runTest ();
    }
}