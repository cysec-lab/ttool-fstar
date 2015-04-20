/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici

   ludovic.apvrille AT enst.fr
   andrea.enrici AT enstr.fr

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
   * Class SingleDmaMEC, Model Extension Construct (MEC) class for a single DMA data transfer
   * Creation: 05/02/2014
   * @version 1.0 05/02/2014
   * @author Andrea ENRICI
   * @see
   */

package tmltranslator.ctranslator;;

import java.util.*;

public class SingleDmaMEC extends CPMEC	{

	public static final String Context = "EMBB_CONTEXT";
	public static final String Ctx_cleanup = "dma_ctx_cleanup";

	public static final int MaxParameters = 3;
	public static final int destinationAddressIndex = 0;
	public static final int sourceAddressIndex = 1;
	public static final int counterIndex = 2;

	public static final String destinationAddress = "destinationAddress";
	public static final String sourceAddress = "sourceAddress";
	public static final String counter = "counter";

	public SingleDmaMEC( String ctxName )	{

		node_type = "SingleDmaMEC";
		inst_type = "VOID";
		inst_decl = "EMBB_DMA_CONTEXT";
		buff_type = "MM_BUFFER_TYPE";
		buff_init = "VOID";
	}

	public SingleDmaMEC( String ctxName, ArchUnitMEC archMEC )	{

		node_type = "SingleDmaMEC";
		inst_type = "VOID";
		inst_decl = "EMBB_DMA_CONTEXT";
		buff_type = "MM_BUFFER_TYPE";
		buff_init = "VOID";
		exec_code = TAB + "embb_dma_start(&" + ctxName + ", (uintptr_t) /*USER TO DO: SRC_ADDRESS*/, (uintptr_t) /*USER TO DO: DST_ADDRESS*/, /*USER TO DO: NUM_SAMPLES */ );" + CR;	
		init_code = TAB + archMEC.getCtxInitCode() + "(&" + ctxName + ", " + "(uintptr_t) " + archMEC.getLocalMemoryPointer() + " );" + CR;
		cleanup_code = TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName +");";
	}
	
	public SingleDmaMEC( String ctxName, String destinationAddress, String sourceAddress, String size )	{

		node_type = "SingleDmaMEC";
		inst_type = "VOID";
		inst_decl = "EMBB_DMA_CONTEXT";
		buff_type = "MM_BUFFER_TYPE";
		buff_init = "VOID";
		exec_code = TAB + "embb_dma_start(&" + ctxName + ", (uintptr_t) " + sourceAddress + ", (uintptr_t) " + destinationAddress + ", (size_t) " + size + " );" + CR;	
		init_code = TAB + "embb_dma_ctx_init(&" + ctxName + ", /*USER TO DO: DMA_DEVICE*/, /*USER TO DO: DST_DEV*/, NULL );" + CR;
		cleanup_code = TAB + "embb_dma_ctx_cleanup(&" + ctxName +");";
	}

	public String getInitCode()	{
		return init_code;
	}

}	//End of class
