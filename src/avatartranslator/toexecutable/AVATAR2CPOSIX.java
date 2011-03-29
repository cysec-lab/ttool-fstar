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
* Class AVATAR2CPOSIX
* Creation: 29/03/2011
* @version 1.1 29/03/2011
* @author Ludovic APVRILLE
* @see
*/

package avatartranslator.toexecutable;

import java.awt.*;
import java.util.*;

import myutil.*;
import avatartranslator.*;

public class AVATAR2CPOSIX {

	private final static String UNKNOWN = "UNKNOWN";

	
	private final static String BOOLEAN_DATA_HEADER = "(* Boolean return types *)\ndata true/0.\ndata false/0.\n";
	private final static String FUNC_DATA_HEADER = "(* Functions data *)\ndata " + UNKNOWN + "/0.\n";
	
	private final static String PK_HEADER = "(* Public key cryptography *)\nfun pk/1.\nfun encrypt/2.\nreduc decrypt(encrypt(x,pk(y)),y) = x.\n";
	private final static String SK_HEADER = "(* Symmetric key cryptography *)\nfun sencrypt/2.\nreduc sdecrypt(sencrypt(x,k),k) = x.\n";
	private final static String MAC_HEADER = "(* MAC *)\nfun MAC/2.\nreduc verifyMAC(m, k, MAC(m, k)) = true.\n";
	private final static String CONCAT_HEADER = "(* CONCAT *)\nfun concat/5.\nreduc get1(concat(m1, m2, m3, m4, m5))= m1.\nreduc get2(concat(m1, m2, m3, m4, m5))= m2.\nreduc get3(concat(m1, m2, m3, m4, m5))= m3.\nreduc get4(concat(m1, m2, m3, m4, m5))= m4.\nreduc get5(concat(m1, m2, m3, m4, m5))= m5.\n";
	
	private final static String MAIN_DEC = "int main(int argc, char *argv[]) {\n";
	
	private AvatarSpecification avspec;
	
	private Vector warnings;
	
	private MainFile mainFile;
	private Vector<TaskFile> taskFiles;
	

	public AVATAR2CPOSIX(AvatarSpecification _avspec) {
		avspec = _avspec;
	}
	
	
	public void saveInFiles(String path) throws FileException {
		if (mainFile != null) {
			FileUtils.saveFile(path + mainFile.getName() + ".h", mainFile.getHeaderCode());
			FileUtils.saveFile(path + mainFile.getName() + ".c", mainFile.getMainCode());
		}
	}
	
	
	public Vector getWarnings() {
		return warnings;
	}
	

	
	public void generateCPOSIX(boolean _debug) {
		mainFile = new MainFile("main");
	
	
	}
	
}