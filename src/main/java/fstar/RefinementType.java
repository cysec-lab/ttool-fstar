
package fstar;

import rationals.converters.ToString;

/**
 * Class Refinement
 * 
 * @version 2.0 20/05/2010
 * @author Taku Kitamura
 */

public class RefinementType {
    String refinementTypeString;

    public RefinementType(String s) {
        refinementTypeString = s;
    }

    public String toString() {
        return refinementTypeString;
    }
}
