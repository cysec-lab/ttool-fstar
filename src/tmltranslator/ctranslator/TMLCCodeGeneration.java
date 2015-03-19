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
   * Class TMLCCodeGeneration
   * Creation: 09/02/2014
   * @version 1.0 09/02/2014
   * @author Andrea ENRICI
   * @see
   */

package tmltranslator.ctranslator;;

import java.util.*;
import java.io.*;
import java.nio.*;
import javax.swing.*;
import javax.swing.event.*;
import myutil.*;

import tmltranslator.*;
import ui.tmlcompd.*;
import ui.ConfigurationTTool;

public class TMLCCodeGeneration	{

	public String title;

	private String CR = "\n";
	private String CR2 = "\n\n";
	private String TAB = "\t";
	private String TAB2 = "\t\t";
	private String TAB3 = "\t\t\t";
	private String TAB4 = "\t\t\t\t";
	private String SP = " ";
	private String SC = ";";
	private String COLON = ",";

	private TMLMapping tmap;
	private TMLModeling tmlm;
	private TMLArchitecture tmla;
	private String applicationName;
	private StringBuffer mainFileString = new StringBuffer();
	private StringBuffer headerString = new StringBuffer();
	private StringBuffer programString = new StringBuffer();
	private StringBuffer initFileString = new StringBuffer();
	private ArrayList<TMLTask> mappedTasks;
	private ArrayList<TMLElement> commElts;
	private ArrayList<Operation> operationsList = new ArrayList<Operation>();
	private int SDRoperationsCounter = 0;
	private int signalsCounter = 0;
	private ArrayList<Signal> signalsList = new ArrayList<Signal>();
	private ArrayList<TMLCPLib> mappedCPLibs;
	private ArrayList<TMLPort> postexList = new ArrayList<TMLPort>();
	private ArrayList<TMLPort> prexList = new ArrayList<TMLPort>();
	private ArrayList<Buffer> buffersList = new ArrayList<Buffer>();
	private ArrayList<DataTransfer> dataTransfersList = new ArrayList<DataTransfer>();

	private final String WRONG_ORIGIN_CHANNEL = "is not declared as an origin channel of the task";
	private final String WRONG_DESTINATION_CHANNEL = "is not declared as a destination channel of the task";
	private final String WRONG_ORIGIN_EVENT = "is not declared as an origin event of the task";
	private final String WRONG_DESTINATION_EVENT = "is not declared as a destination event of the task";
	private final String WRONG_ORIGIN_REQUEST = "is not declared as an origin request of the task";
	private final String SYNTAX_ERROR = "syntax error";
	private final String WRONG_VARIABLE_IDENTIFIER = "forbidden variable's name";
	private final String VARIABLE_ERROR = "variable is not used according to its type";
	private final String UNDECLARED_VARIABLE = "unknown variable";
	private final String WRONG_VARIABLE_TYPE = "incorrect variable type";
	private final String SYNTAX_ERROR_VARIABLE_EXPECTED = "syntax error (variable expected)";
	private final String TIME_UNIT_ERROR = "unknown time unit";

	private ArrayList<TMLCCodeGenerationError> errors;
	private ArrayList<TMLCCodeGenerationError> warnings;

	private String debugFileName = ConfigurationTTool.CcodeDirectory + "/debugFile.txt";
	PrintWriter outputStream;

	public JFrame frame; //Main Frame

	public TMLCCodeGeneration( String _title, String _applicationName, JFrame _frame )	{
		title = _title;
		applicationName = _applicationName;
		frame = _frame;
		init();
	}

	private void init()	{
		mappedTasks = new ArrayList<TMLTask>();
		commElts = new ArrayList<TMLElement>();
		errors = new ArrayList<TMLCCodeGenerationError>();
	}

	public void toTextFormat( TMLMapping _tmap )	{

		tmap = _tmap;
		tmap.linkTasks2TMLChannels();
		tmap.linkTasks2TMLEvents();
		tmlm = _tmap.getTMLModeling();
		tmla = _tmap.getTMLArchitecture();
		mappedCPLibs = _tmap.getMappedTMLCPLibs();

		ArrayList<TMLTask> mappedTasks = tmap.getMappedTasks();
		ArrayList<TMLElement> commElts = tmap.getMappedCommunicationElement();

		//Create the data structures for signals, buffers, operations and data transfers
		openDebugFile();
		makeSignalsList();	//make the signals associated to operations, based on the tasks of operations

		for( Signal sig: signalsList )	{
			TraceManager.addDev( sig.getName() );
			appendToDebugFile( sig.toString() + CR );
		}
		makeOperationsList( mappedTasks );	//make the list of operations based on the tasks in the app model
		for( Operation op: operationsList )	{
			TraceManager.addDev( op.toString() );
			appendToDebugFile( op.toString() + CR );
		}
		makeBuffersList();
		for( Buffer buff: buffersList )	{
			TraceManager.addDev( buff.toString() );
			appendToDebugFile( buff.toString() + CR );
		}
		makeDataTransfersList();
		for( DataTransfer dt: dataTransfersList )	{
		 		TraceManager.addDev( dt.toString() );
				appendToDebugFile( dt.toString() );
		}
		closeDebugFile();

		//Generate the C code
		generateMainFile();
		generateHeaderFile( mappedTasks );
		generateProgramFile();
		generateInitFile( mappedTasks );
	}

/**********************************************************************************
 * 																CREATION OF DATA STRUCTURE PART
 *********************************************************************************/
	//From the list of mapped tasks, built the list of operations. For SDR operations, only F_ tasks are considered.
	private void makeOperationsList( ArrayList<TMLTask> mappedTasks )	{

		ArrayList<TMLTask> SDRXtasks = new ArrayList<TMLTask>();
		ArrayList<TMLTask> SDRFtasks = new ArrayList<TMLTask>();
		Buffer inBuffer, outBuffer;
		Signal outSignal;
		ArrayList<Signal> inSignals;
		String[] s;

		for( TMLTask task: mappedTasks )	{
			TraceManager.addDev( "Mapped Operation of taks " + task.getName() + " is: " + task.getOperationMEC() );
			String taskName = task.getName().split( "__" )[1];
			s = taskName.split( "X_" );
			if( s.length > 1 )	{	//we are splitting an eXecution task
				SDRXtasks.add( task );
			}
			else	{	
				s = taskName.split( "F_" );
				if( s.length > 1 )	{	//we are splitting a Firing task
					SDRFtasks.add( task );
				}
			}
		}
		//Now couple the tasks to create SDRoperations
		for( TMLTask fTask: SDRFtasks )	{
			String fTaskName = fTask.getName().split( "__" )[1].split( "F_" )[1];
			for( TMLTask xTask: SDRXtasks )	{
				String xTaskName = xTask.getName().split( "__" )[1].split( "X_" )[1];
				if( xTaskName.equals( fTaskName ) )	{
					//Mind that signals are based on channels NOT on events!
					inSignals = getInSignals( xTask );	//is null for Source operation
					outSignal = getOutSignal( xTask);	//is null for Sink operation
					//Get the ports of channels and associated them to buffers
					if( xTask.getReadTMLChannels().size() > 0 )	{
						inBuffer = createInBuffer( xTask.getReadTMLChannels().get(0) );	//null for Source
					}
					else	{
						inBuffer = null;
					}
					if( xTask.getWriteTMLChannels().size() > 0 )	{
						outBuffer = createOutBuffer( xTask.getWriteTMLChannels().get(0) );	//null for Sink
					}
					else	{
						outBuffer = null;
					}
					operationsList.add( new Operation( fTask, xTask, tmap.getHwNodeOf( xTask ), tmap.getHwNodeOf( fTask ), inSignals, outSignal, inBuffer, outBuffer ) );
					SDRoperationsCounter++;
				}
			}
		}
	}

	//Create the inBuffer form the port of the read channel associated to the xTask
	private Buffer createInBuffer( TMLChannel readChannel )	{
	
		if( readChannel.isBasicChannel() )	{
			return new Buffer( readChannel.getDestinationPort() );
		}
		else if( readChannel.isAForkChannel() )	{
			return new Buffer( readChannel.getDestinationPorts().get(0) );
		}
		else if( readChannel.isAJoinChannel() )	{
			return new Buffer( readChannel.getDestinationPorts().get(0) );
		}
		return null;
	}

	//Create the inBuffer form the port of the write channel associated to the xTask
	private Buffer createOutBuffer( TMLChannel writeChannel )	{
	
		if( writeChannel.isBasicChannel() )	{
			return new Buffer( writeChannel.getOriginPort() );
		}
		else if( writeChannel.isAForkChannel() )	{
			return new Buffer( writeChannel.getOriginPorts().get(0) );
		}
		else if( writeChannel.isAJoinChannel() )	{
			return new Buffer( writeChannel.getOriginPorts().get(0) );
		}
		return null;
	}

	private ArrayList<Signal> getInSignals( TMLTask task )	{	//Find the signal associated to the write channel of task

		TMLPort originPort, destinationPortCh, originPortSigChannel, destinationPortSigChannel;
		TMLChannel sigChannel;
		ArrayList<Signal> sigsList = new ArrayList<Signal>();

		for( TMLChannel ch: task.getReadTMLChannels() )	{
			if( ch.isBasicChannel() )	{
				originPort = ch.getOriginPort();
				for( Signal sig: signalsList )	{
					sigChannel = sig.getTMLChannel();
					if( sigChannel.isBasicChannel() )	{
						if( sigChannel.getOriginPort().getName().equals( originPort.getName() ) )	{
							//return sig;
							sigsList.add( sig );
						}
					}
				}
			}
			else if( ch.isAForkChannel() )	{
				originPort = ch.getOriginPorts().get(0);
				for( Signal sig: signalsList )	{
					sigChannel = sig.getTMLChannel();
					if( sigChannel.isAForkChannel() )	{
						if( sigChannel.getOriginPorts().get(0).getName().equals( originPort.getName() ) )	{
							//return sig;
							sigsList.add( sig );
						}
					}
				}
			}
			else if( ch.isAJoinChannel() )	{	//take all signals that match the destination port of the join channel
				destinationPortCh = ch.getDestinationPorts().get(0);
				for( Signal sig: signalsList )	{
					sigChannel = sig.getTMLChannel();
					if( sigChannel.isAForkChannel() )	{
						destinationPortSigChannel = sigChannel.getDestinationPorts().get(0);
					}
					else	{	//there are no join channels associated to signals
						destinationPortSigChannel = sigChannel.getDestinationPort();
					}
					if( destinationPortCh.getName().equals( destinationPortSigChannel.getName() ) )	{
						sigsList.add( sig );
					}
				}
			}
		}
		return sigsList;
	}

	private Signal getOutSignal( TMLTask task )	{	//Find the signal associated to the write channel of task
		
		TMLPort originPort, destinationPort, originPortSigChannel;
		TMLChannel sigChannel;

		for( TMLChannel ch: task.getWriteTMLChannels() )	{
			if( ch.isBasicChannel() )	{
				originPort = ch.getOriginPort();
				for( Signal sig: signalsList )	{
					sigChannel = sig.getTMLChannel();
					if( sigChannel.isBasicChannel() )	{
						if( sigChannel.getOriginPort().getName().equals( originPort.getName() ) )	{
							return sig;
						}
					}
				}
			}
			else if( ch.isAForkChannel() )	{
				originPort = ch.getOriginPorts().get(0);
				for( Signal sig: signalsList )	{
					sigChannel = sig.getTMLChannel();
					if( sigChannel.isAForkChannel() )	{
						if( sigChannel.getOriginPorts().get(0).getName().equals( originPort.getName() ) )	{
							return sig;
						}
					}
				}
			}
			else if( ch.isAJoinChannel() )	{	//join channels are tricky because of the multiple source tasks and ports.
				ArrayList<TMLTask> tasksList = ch.getOriginTasks();	//get the index of the origin port
				int i;
				for( i = 0; i < tasksList.size(); i++ )	{
					if( tasksList.get(i).getName().equals( task.getName() ) )	{
						break;	//i is the index of the correct origin port
					}
				}
				TMLPort sourcePortCh = ch.getOriginPorts().get(i);
				for( Signal sig: signalsList )	{
					sigChannel = sig.getTMLChannel();
					if( sigChannel.isAForkChannel() )	{
						originPortSigChannel = sigChannel.getOriginPorts().get(0);
					}
					else	{	//there are no join channels associated to signals
						originPortSigChannel = sigChannel.getOriginPort();
					}
					if( sourcePortCh.getName().equals( originPortSigChannel.getName() ) )	{
						return sig;
					}
				}
			}
		}
		return null;
	}

	private void makeBuffersList()	{

		ArrayList<TMLPort> portsList = new ArrayList<TMLPort>();
		for( TMLChannel ch: tmlm.getChannels() )	{
			if( ch.isBasicChannel() )	{
				buffersList.add( new Buffer( ch.getOriginPort() ) );	
				buffersList.add( new Buffer( ch.getDestinationPort() ) );	
			}
			else if( ch.isAForkChannel() )	{
				buffersList.add( new Buffer( ch.getOriginPorts().get(0) ) );	
				for( TMLPort port: ch.getDestinationPorts() )	{
					buffersList.add( new Buffer( port ) );
				}
			}
			else if( ch.isAJoinChannel() )	{
				for( TMLPort port: ch.getOriginPorts() )	{
					buffersList.add( new Buffer( port ) );
				}
				buffersList.add( new Buffer( ch.getDestinationPorts().get(0) ) );	
			}
		}
		for( TMLCPLib cplib: mappedCPLibs )	{
			if( cplib.getArtifacts().size() == 1 )	{
				String mappedPortName = cplib.getArtifacts().get(0).getPortName();	//only one mapped port per CP
				//TraceManager.addDev( mappedPortName );
				for( Buffer buff: buffersList )	{
					if( buff.getName().split("BUFFER__")[1].equals( mappedPortName ) )	{
						//TraceManager.addDev( "Buffer " + buff.getName() + " matches mapped port " + mappedPortName );
						buff.addMappingArtifact( cplib.getArtifacts().get(0) );
					}
				}
			}
		}
	}

	private void makeDataTransfersList()	{

		ArrayList<Signal> inSignals;
		Signal outSignal;

		for( TMLCPLib cplib: mappedCPLibs )	{
			if( cplib.getArtifacts().size() == 1 )	{
				String portName = cplib.getArtifacts().get(0).getPortName();	//only one mapped port per CP
				String memoryName = cplib.getArtifacts().get(0).getMemoryName();	//only one mapped port per CP
				String taskName = cplib.getArtifacts().get(0).getTaskName();	//only one mapped port per CP
				Object o = cplib.getArtifacts().get(0).getReferenceObject();
				//TraceManager.addDev( "CPLIB " + cplib.getName() + "\n\tportName: " + portName + "\n\tmemoryName: " + memoryName + "\n\ttaskName:" + taskName );
				inSignals = getDTInSignals( portName );
				DataTransfer dt = new DataTransfer( cplib, inSignals, null );	//outSignals are added later
				dataTransfersList.add( dt );
			}
		}

		Signal newSig;
		ArrayList<Signal> newInSignalsList = new ArrayList<Signal>();

		for( DataTransfer dt: dataTransfersList )	{
			Operation op = getOperationWithSameInputSignals( dt.getInSignals() );	//IMPORTANT: create a DT output signal and modifies operations input signals
			if( op != null )	{
				for( Signal sig: op.getInSignals() )	{
					newSig = new Signal( sig.getTMLChannel() );
					newSig.setName( sig.getName() + "_CP" );
					dt.addOutSignal( newSig );
					newInSignalsList.add( newSig );	//to be substitued to the inSignals of op
					signalsList.add( newSig );
				}
				op.setInSignals( newInSignalsList );
				newInSignalsList = new ArrayList<Signal>();
			}
		}
		/*for( Signal sig: signalsList )	{
			TraceManager.addDev( sig.getName() );
		}*/
		TraceManager.addDev( "*****************************************" );
		for( Operation op: operationsList )	{
			TraceManager.addDev( op.toString() );
		}
	}

	private Operation getOperationWithSameInputSignals( ArrayList<Signal> inSignals )	{
		
		int counter = 0;
		for( Operation op: operationsList )	{
			for( Signal sig: op.getInSignals() )	{
				if( inSignals.contains( sig ) )	{
					counter++;
				}
			}
			if( counter == inSignals.size() )	{
				return op;
			}
			counter = 0;
		}
		return null;
	}

	//retrieve the signal whose channel has a destintation port equal to portName
	private ArrayList<Signal> getDTInSignals( String portName )	{
		
		TMLChannel channel;
		ArrayList<Signal> sigsList = new ArrayList<Signal>();

		for( Signal sig: signalsList )	{
			channel = sig.getTMLChannel();
			if( channel.isBasicChannel() )	{
				if( channel.getDestinationPort().getName().equals( portName ) )	{
					sigsList.add( sig );
				}
			}
			else	{	//fork or join channel with multiple destination ports	
				for( TMLPort port: channel.getDestinationPorts() )	{
					if( port.getName().equals( portName ) )	{
						sigsList.add(sig );
					}
				}
			}
		}
		return sigsList;
	}

	//Associate signals to operations and at the same time add signals to signalsList. Only works for SDR operations (so far)
	//If user makes a typo in the event name associated to a channel, then the signal is not created. Should raise an error!
	private void makeSignalsList()	{
		
		for( TMLChannel ch: tmlm.getChannels() )	{
			//for basic and for channels there is a one-to-one correspondence with signals. Do not consider events, for simplicity, so
			//far.
			if( ch.isBasicChannel() || ch.isAForkChannel() )	{
				signalsList.add( new Signal( ch ) );
			}
			else if( ch.isAJoinChannel() )	{	//basic signals must be created in order for the SDF scheduler to work
				for( TMLChannel tmlch: transformIntoBasicChannels( ch ) )	{
					signalsList.add( new Signal( tmlch ) );
				}
			}
			/*if( ch.isBasicChannel() )	{
				String associatedEvtName = ch.getOriginPort().getAssociatedEvent();
				for( TMLEvent evt: tmlm.getEvents() )	{
					String evtName = evt.getName().split("__")[1];
					if( evtName.equals( associatedEvtName ) )	{
						signalsList.add( new Signal( ch, evt ) );
						break;
					}
				}
			}
			else if( ch.isAForkChannel() )	{
				String associatedEvtName = ch.getOriginPort(0).getAssociatedEvent();
				for( TMLEvent evt: tmlm.getEvents() )	{
					String evtName = evt.getName().split("__")[1];
					if( evtName.equals( associatedEvtName ) )	{
						signalsList.add( new Signal( ch, evt ) );
						break;
					}
				}
			}
			else if( ch.isAJoinChannel() )	{	//Basic signals must be created in order for the SDF scheduler to work
				String associatedEvtName = ch.getDestinationPorts().get(0).getAssociatedEvent();
				for( TMLEvent evt: tmlm.getEvents() )	{
					String evtName = evt.getName().split("__")[3];
					if( evtName.equals( associatedEvtName ) )	{
						signalsList.add( new Signal( ch, evt ) );
						break;
					}
				}
			}*/
		}
		return;
	}

/**********************************************************************************
 * 																CODE GENERATION PART
 *********************************************************************************/

	private void generateMainFile()	{
		mainFileString.append( "#include \"" + applicationName + "\".h" + CR2 );
		mainFileString.append( "int main(void)\t{" + CR +
								TAB + "int status=0;" + CR +
								TAB + "char *src_out_dat;" + CR +
								TAB + "char *dma1_out_dat;" + CR +
								TAB + "int g_r_size = 10240;" + CR +
								TAB + "int g_Ns = 1024;" + CR +
								TAB + "int g_Fi = 593;" + CR +
								TAB + "int g_Li = 116;" + CR2 +
								TAB + "src_out_dat = (char*) calloc(g_r_size*4, 1);" + CR +
								TAB + "if( src_out_dat == NULL ) exit(1);" + CR +
								TAB2 + "dma1_out_dat = (char*) calloc(4, 1);" + CR +
								TAB + "if( dma1_out_dat == NULL ) exit(1);" + CR +
								TAB2 + "FILE *source = fopen(\"date_demo.dat\", \"r\");" + CR +
								TAB + "if( source != NULL ){ " + CR +
								TAB2 + "fread(src_out_dat, 1, g_r_size*4, source);" + CR +
								TAB2 + "fclose(source);" + CR +
 								TAB + "} else printf(\"ERROR input file does not exist!\\n\");" + CR +
								TAB + applicationName + "_init( (char*)src_out_dat, (char*)dma1_out_dat, g_r_size, g_Ns , g_Fi, g_Li );" + CR +
								TAB + "status = " + applicationName + "_exec();" + CR +
								TAB + "printf(\"score %d \", *(uint32_t*)dma1_out_dat );" + CR +
								TAB + "free(src_out_dat);" + CR +
								"}" );
	}

	private void generateHeaderFile( ArrayList<TMLTask> mappedTasks )	{

		getPrexAndPostexChannels();
		headerString.append( generateCodeForLibraries() );
		headerString.append( generateCodeForPrototypes() );
		headerString.append( variablesInMainFile() );
		headerString.append( buffersAndInstructionsDeclaration( true ) );
		headerString.append( generateCodeForSignals() );
		headerString.append( generateCodeForVariables() );
	}

	private void getPrexAndPostexChannels()	{

		boolean foundPrex = false, foundPostex = false;
		TMLPort originPort, destinationPort;

		//Fill the the prex and postex lists
		for( TMLChannel ch: tmlm.getChannels() )	{
			if( ch.isBasicChannel() )	{
				originPort = ch.getOriginPort();
				if( originPort.isPrex() )	{
					prexList.add( originPort );
					foundPrex = true;
				}
			}
			if( ch.isAForkChannel() )	{
				originPort = ch.getOriginPorts().get(0);
				if( originPort.isPrex() )	{
					prexList.add( originPort );
					foundPrex = true;
				}
			}
		}
		for( TMLChannel ch: tmlm.getChannels() )	{
			if( ch.isBasicChannel() )	{
				destinationPort = ch.getDestinationPort();
				if( destinationPort.isPostex() )	{
					postexList.add( destinationPort );
					foundPostex = true;
				}
			}
			if( ch.isAJoinChannel() )	{
				destinationPort = ch.getDestinationPorts().get(0);
				if( destinationPort.isPostex() )	{
					postexList.add( destinationPort );
					foundPostex = true;
				}
			}
		}
		TraceManager.addDev( "foundPrex = " + foundPrex );
		if( !foundPrex )	{
			addError( "No suitable channel has been marked as prex", TMLCCodeGenerationError.ERROR_STRUCTURE );
		}
		if( !foundPostex )	{
			addError( "No suitable channel has been marked as postex", TMLCCodeGenerationError.ERROR_STRUCTURE );
		}
	}

	private String generateCodeForLibraries()	{
		String s = "#ifndef " + applicationName + "_H" + CR +
							"#define " + applicationName + "_H" + CR +
							"#include <stdio.h>" + CR +
							"#include <stdint.h>" + CR +
							"#include <embb/fep.h>" + CR +
							"#include <embb/intl.h>" + CR +
							"#include <embb/mapper.h>" + CR +
							"#include <embb/adaif.h>" + CR +
							"#include <embb/memory.h>" + CR + 
							"#include <embb/dma.h>" + CR2;
		return s;
	}

	private String generateCodeForPrototypes()	{
		String s = 	"/**** prototypes *****/" + CR +
								"extern int " + applicationName + "_exec(void);" + CR +
								"extern void " + applicationName + "_exec_init();" + CR +
								"extern bool exit_rule(void);" + CR +
								"extern void register_operations(void);" + CR +
								"extern void register_dataTransfers(void);" + CR +
								"extern void register_fire_rules(void);" + CR +
								"extern void signal_to_buffer_init();" + CR +
								"extern void init_operations_context(void);" + CR +
								"extern void init_CPs_context(void);" + CR +
								"extern void cleanup_operations_context(void);" + CR2;
		return s;
	}

	private String variablesInMainFile()	{
		String s = 	"extern int g_r_size;" + CR +
								"extern int g_Ns;" + CR +
								"extern int g_Fi" + CR +
								"extern int g_Li" + CR +
								"extern char *src_out_dat;" + CR +
								"extern char *dma1_out_dat;" + CR2;
		return s;
	}

	private String buffersAndInstructionsDeclaration( boolean declaration )	{

		TMLTask xTask, fTask;
		ArchUnitMEC taskMEC;
		StringBuffer buffersString = new StringBuffer( "/**** Buffers *****/" + CR );
		StringBuffer instructionsString = new StringBuffer( "/**** Operations Instructions *****/" + CR );

		for( Operation op: operationsList )	{
			if( op.getType() == Operation.SDR )	{
				xTask = op.getSDRTasks().get( Operation.X_TASK );
				fTask = op.getSDRTasks().get( Operation.F_TASK );
				taskMEC = tmap.getHwNodeOf( xTask ).getArchUnitMEC();
				if( taskMEC instanceof FepMEC )	{
					FEPBuffer fepBuff = new FEPBuffer( "buff_" + xTask.getName().split("__")[1], xTask );
					if( declaration )	{
						buffersString.append( "extern" + SP + fepBuff.getType() + SP + fepBuff.getName() + ";" + CR );
						instructionsString.append( "extern" + SP + FepMEC.Context + SP + fTask.getTaskName() + "_ctx;" + CR );
					}
					else	{
						buffersString.append( fepBuff.getType() + SP + fepBuff.getName() + ";" + CR );
						instructionsString.append( FepMEC.Context + SP + fTask.getTaskName() + "_ctx;" + CR );
					}
					//buffersList.add( fepBuff );
				}
				else if( taskMEC instanceof MapperMEC )	{
					MAPPERBuffer mappBuff = new MAPPERBuffer( "buff_" + xTask.getName().split("__")[1], xTask );
					if( declaration )	{
						buffersString.append( "extern" + SP + mappBuff.getType() + SP + mappBuff.getName() + ";" + CR );
						instructionsString.append( "extern" + SP + MapperMEC.Context + SP + fTask.getTaskName() + "_ctx;" + CR );
					}
					else	{
						buffersString.append( mappBuff.getType() + SP + mappBuff.getName() + ";" + CR );
						instructionsString.append( MapperMEC.Context + SP + fTask.getTaskName() + "_ctx;" + CR );
					}
					//buffersList.add( mappBuff );
				}
				else if( taskMEC instanceof InterleaverMEC )	{
					MMBuffer mmBuff = new MMBuffer( "buff_" + xTask.getName().split("__")[1], xTask );
					if( declaration )	{
						buffersString.append( "extern" + SP + mmBuff.getType() + SP + mmBuff.getName() + ";" + CR );
						instructionsString.append( "extern" + SP + InterleaverMEC.Context + SP + fTask.getTaskName() + "_ctx;" + CR );
					}
					else	{
						buffersString.append( mmBuff.getType() + SP + mmBuff.getName() + ";" + CR );
						instructionsString.append( InterleaverMEC.Context + SP + fTask.getTaskName() + "_ctx;" + CR );
					}
					//buffersList.add( mmBuff );
				}
				else if( taskMEC instanceof AdaifMEC )	{
					MMBuffer mmBuff = new MMBuffer( "buff_" + xTask.getName().split("__")[1], xTask );
					if( declaration )	{
						buffersString.append( "extern" + SP + mmBuff.getType() + SP + mmBuff.getName() + ";" + CR );
						instructionsString.append( "extern" + SP + AdaifMEC.Context + SP + fTask.getTaskName() + "_ctx;" + CR );
					}
					else	{
						buffersString.append( mmBuff.getType() + SP + mmBuff.getName() + ";" + CR );
						instructionsString.append( AdaifMEC.Context + SP + fTask.getTaskName() + "_ctx;" + CR );
					}
					//buffersList.add( mmBuff );
				}
			}
		}
		instructionsString.append( CR2 + "/**** Data Transfers Instructions ****/" + CR );
		for( DataTransfer dt: dataTransfersList )	{
			TMLCPLib tmlcplib = dt.getTMLCPLib();
			CPMEC mec = tmlcplib.getCPMEC();
			if( mec instanceof CpuMemoryCopyMEC )	{
				if( declaration )	{
					instructionsString.append( "extern" + SP + CpuMemoryCopyMEC.Context + SP + tmlcplib.getName() + "_ctx;" + CR );
				}
				else	{	instructionsString.append( CpuMemoryCopyMEC.Context + SP + tmlcplib.getName() + "_ctx;" + CR );	}
			}
			if( mec instanceof SingleDmaMEC )	{
				if( declaration )	{
					instructionsString.append( "extern" + SP + SingleDmaMEC.Context + SP + tmlcplib.getName() + "_ctx;" + CR );
				}
				else	{
					instructionsString.append( SingleDmaMEC.Context + SP + tmlcplib.getName() + "_ctx;" + CR );
				}
			}
			if( mec instanceof DoubleDmaMEC )	{
				if( declaration )	{
					instructionsString.append( "extern" + SP + DoubleDmaMEC.Context + SP + tmlcplib.getName() + "_ctx;" + CR );
				}
				else	{
					instructionsString.append( DoubleDmaMEC.Context + SP + tmlcplib.getName() + "_ctx;" + CR );
				}
			}
		}
		return buffersString.toString() + CR + instructionsString.toString();
	}

	private String generateCodeForSignals()	{

		StringBuffer s = new StringBuffer( CR2 + "/********* SIGNAL TYPE ***************/" + CR + Signal.DECLARATION + CR2 + "enum sigs_enu	{" + CR );
		for( Signal sig: signalsList )	{
			s.append( sig.getName() + CR );
		}
		s.append( "NUM_SIGS };" + CR2 + "enum ops_enu	{" + CR );

		for( Operation op: operationsList )	{
			s.append( op.getName() + ",\n" );
		}
		s.append( "NUM_OPS };" + CR2 );
		return s.toString();
	}

	private String generateCodeForVariables()	{
		StringBuffer s = new StringBuffer( "/**** variables *****/" + CR + "extern SIG_TYPE sig[];" + CR2 );
		s.append( FEPBuffer.DECLARATION + CR2 );
		s.append( MAPPERBuffer.DECLARATION + CR2 );
		s.append( MMBuffer.DECLARATION + CR2 + "#endif" );
		return s.toString();
	}

	private void generateProgramFile()	{

		/*JOptionPane.showMessageDialog( frame,
																	 "The TURTLE Analysis contains several errors",
																	 "Syntax analysis failed",
																	 JOptionPane.INFORMATION_MESSAGE );*/
		Scheduler scheduler = new Scheduler( Scheduler.JAIR );
		programString.append(
							"#include " + "\"" + applicationName + ".h\"" + CR2 +
							"int (*operation[NUM_OPS])();" + CR +
							"bool (*fire_rule[NUM_OPS])();" + CR +
							"SIG_TYPE sig[NUM_SIGS]={{0}};" + CR2 +
							"/******** " + applicationName + "_exec function *********/" + CR +
							"int " + applicationName + "_exec(void)	{" + CR + TAB +
							"bool valid_signal = false;" + CR + TAB +
							"bool blocked = true;" + CR + TAB +
							"int status = 0;" + CR + TAB +
							"register_operations();" + CR + TAB +
							"register_dataTransfers();" + CR + TAB +
							"register_fire_rules();" + CR + TAB +
							"signal_to_buffer_init();" + CR + TAB +
							"init_operations_context();" + CR + TAB +
							"init_CPs_context();" + CR2 + TAB +
							"/********* INIT PREX OPs signals ********/" + CR +
							generateCodeToInitPrexOperations() + CR + TAB +
							"/********* OPERATIONS scheduling ***************/" + CR + TAB +
							scheduler.getCode() + CR + TAB +
							"cleanup_operations_context();" + CR + "}" + CR2 );
		generateCodeForOperations();
		generateCodeForCommunicationPatterns();
		generateCodeToRegisterOperations();
		generateCodeToRegisterDataTransfers();
		generateCodeForFireRules();
		generateCodeToRegisterFireRules();
		generateCodeForExitRule();
	}

	private String generateCodeToInitPrexOperations()	{
		
		StringBuffer s = new StringBuffer();
		for( TMLPort port: prexList )	{
			s.append( TAB + "sig[ " + port.getName() +" ].f = true;" + CR );
		}
		return s.toString();
	}


	private void generateCodeForOperations()	{ //generate the code for the execution operations
		
		//for each operations add the exec code + the info for all the signals and stuff
		String exec_code = "";

		for( Operation op: operationsList )	{
			if( op.getType() == Operation.SDR )	{
				programString.append( generateSDROperation( op, op.getSDRTasks().get( Operation.X_TASK ), op.getSDRTasks().get( Operation.F_TASK ) ) );
			}
		}
	}

	private String generateSDROperation( Operation op, TMLTask xTask, TMLTask fTask )	{
		
		//For SDR operations the xTask is used to retrieve the mapped unit
		String signalOutName = "";
		String signalInName = "";
		String ctxName = op.getContextName();
		StringBuffer code = new StringBuffer();

		OperationMEC xTaskOperation = xTask.getOperationMEC();

		if( op.getOutSignal() != null )	{
			signalOutName = op.getOutSignal().getName();
		}
		for( Signal sig: op.getInSignals() )	{
			signalInName += sig.getName();
		}

		code.append( "int op_" + op.getName() + "()\t{" + CR + getTaskAttributes( fTask ) + CR );

		//Mark input signals as false
		for( Signal sig: op.getInSignals() )	{
			code.append( TAB + "sig[" + sig.getName() + "].f = false;" + CR );
		}
		
		if( xTaskOperation instanceof CwpMEC )	{
			CwpMEC cwp = new CwpMEC( ctxName, signalInName, signalOutName, "" );
			code.append( cwp.getExecCode() );
		}
		else if( xTaskOperation instanceof CwmMEC )	{
			CwmMEC cwm = new CwmMEC( ctxName, signalInName, signalOutName, "" );
			code.append( cwm.getExecCode() );
		}
		else if( xTaskOperation instanceof CwaMEC )	{
			CwaMEC cwa = new CwaMEC( ctxName, signalInName, "", signalOutName, "" );
			code.append( cwa.getExecCode() );
		}
		else if( xTaskOperation instanceof CwlMEC )	{
			CwlMEC cwl = new CwlMEC( ctxName, signalInName, signalOutName, "" );
			code.append( cwl.getExecCode() );
		}
		else if( xTaskOperation instanceof SumMEC )	{
			SumMEC sum = new SumMEC( ctxName, signalInName, signalOutName, "" );
			code.append( sum.getExecCode() );
		}
		else if( xTaskOperation instanceof FftMEC )	{
			FftMEC fft = new FftMEC( ctxName, signalInName, signalOutName, "" );
			code.append( fft.getExecCode() );
		}
		else if( xTaskOperation instanceof MappOperationMEC )	{
			MappOperationMEC mapp = new MappOperationMEC( ctxName, signalInName, signalOutName, "" );
			code.append( mapp.getExecCode() );
		}
		else if( xTaskOperation instanceof IntlOperationMEC )	{
			IntlOperationMEC intl = new IntlOperationMEC( ctxName, signalInName, signalOutName, "" );
			code.append( intl.getExecCode() );
		}
		else if( xTaskOperation instanceof AdaifOperationMEC )	{
			AdaifOperationMEC adaif = new AdaifOperationMEC( ctxName, signalInName, signalOutName, "" );
			code.append( adaif.getExecCode() );
		}

		if( op.getOutSignal() != null )	{
			code.append( TAB + "sig[" + op.getOutSignal().getName() + "].f = true;" + CR );
		}
		else	{	//the postex
			if( postexList.size() > 0 )	{
				code.append( TAB + "sig[" + postexList.get(0).getName() + "].f = true;" + CR );
			}
			else	{
				code.append( CR );
			}
		}
		code.append( TAB + "return status;" + CR + "}" + CR2 );

		return code.toString();
	}

	private String getTaskAttributes( TMLTask task )	{

		StringBuffer attributesList = new StringBuffer();
		String type, value;

		String[] attributes = task.getAttributeString().split("/");
		for( int i = 0; i < attributes.length; i++ )	{
			if( attributes[i].length() > 1 )	{
				String s = attributes[i].split("\\.")[1];
				String name = s.split(":")[0];
				if( !name.contains( "__req" ) )	{	//filter out request parameters
					type = s.split(":")[1].split("=")[0];
					value = s.split(":")[1].split("=")[1];
					if( value.equals(" " ) )	{
						attributesList.append( TAB + type + " " + name + ";" + CR );
					}
					else	{
						attributesList.append( TAB + type + " " + name + " = " + value.substring( 0, value.length() - 1 ) + ";" + CR );
					}
				}
			}
		}
		return attributesList.toString().substring( 0, attributesList.length() - 1 );	//remove last CR
	}

	private String getOutSignalName( TMLTask task )	{
		
		String s = "";
		if( task.getWriteChannels().size() > 0 )	{
			TMLWriteChannel ch = task.getWriteChannels().get(0);
			String signalName = ch.toString().split("__")[1];
			if( signalsList.contains( signalName + "_CPin" ) )	{
				TraceManager.addDev( "Task " + task.getName() + ", OUT signal: " + signalName );
				s = signalName;
			}
			else	{
				s = signalName;
			}
			return s;
		}
		return "";
	}

	private String getInSignalName( TMLTask task )	{
		
		String s = "";
		if( task.getReadChannels().size() > 0 )	{
			TMLReadChannel ch = task.getReadChannels().get(0);
			String signalName = ch.toString().split("__")[1];
			if( signalsList.contains( signalName + "_CPin" ) )	{
				TraceManager.addDev( "Task " + task.getName() + ", IN signal: " + signalName );
				s = signalName;
			}
			else	{
				s = signalName;
			}
			return s;
		}
		return "";
	}

	private void generateCodeForCommunicationPatterns()	{
		
		String s;
		TMLCPLib cplib;

		for( DataTransfer dt: dataTransfersList )	{
			cplib = dt.getTMLCPLib();
			if( cplib.getArtifacts().size() == 1 )	{
				TraceManager.addDev( "----------" + cplib.getArtifacts().get(0).getPortName() );
			}
			programString.append( "int op_" + cplib.getName() + "()\t{" + CR );
			
			for( Signal sig: dt.getInSignals() )	{
				programString.append( TAB + "sig[ " + sig.getName() + " ].f = false;" + CR );
			}

			s = cplib.getMappedUnits().get(0);
			if( s.split("\\.")[0].equals( "Memory_Copy" ) )	{
				CpuMemoryCopyMEC myMEC = new CpuMemoryCopyMEC();
				programString.append( myMEC.getExecCode() );
			}
			if( s.split("\\.")[0].contains( "DMA" ) )	{
				SingleDmaMEC myMEC = new SingleDmaMEC();
				programString.append( myMEC.getExecCode() );
			}

			for( Signal sig: dt.getOutSignals() )	{
				programString.append( TAB + "sig[ " + sig.getName() + " ].f = true;" + CR );
			}
			programString.append( TAB + "return status;" + CR + "}" + CR2 );
		}
	}

	private void generateCodeToRegisterOperations()	{

		programString.append( "void register_operations( void )\t{" + CR );
		for( Operation op: operationsList )	{
			programString.append( TAB + "operation[" + op.getName() + "] = " + "op_" + op.getName() + ";" + CR );
		}
		programString.append( "}" + CR2 );
	}

	private void generateCodeToRegisterDataTransfers()	{

		programString.append( "void register_dataTransfers( void )\t{" + CR );
		for( DataTransfer dt: dataTransfersList )	{
			programString.append( TAB + "operation[" + dt.getName() + "] = " + "op_" + dt.getName() + ";" + CR );
		}
		programString.append( "}" + CR2 );
	}

	private void generateCodeForFireRules()	{

		programString.append( "/**** OPERATIONS FIRE RULES ****/" + CR );
		for( Operation op: operationsList )	{
			programString.append( "bool fr_" + op.getName() + "( void )\t{" + CR );
			programString.append( TAB + "return (" + op.getFireRuleCondition() + ");" + CR );
			programString.append( "}" + CR2 );
		}
		programString.append( CR );
		programString.append( "/**** DATA TRANSFERS FIRE RULES ****/" + CR );
		for( DataTransfer dt: dataTransfersList )	{
			programString.append( "bool fr_" + dt.getName() + "( void )\t{" + CR );
			programString.append( TAB + "return (" + SP + dt.getFireRuleCondition() + SP + ");" + CR );
			programString.append( "}" + CR2 );
		}
		programString.append( CR );
	}

	private void generateCodeToRegisterFireRules()	{

		programString.append( "void register_fire_rules( void )\t{" + CR );
		for( Operation op: operationsList )	{
			programString.append( TAB + "fire_rule[" + op.getName() + "] = " + "fr_" + op.getName() + ";" + CR );
		}
		for( DataTransfer dt: dataTransfersList )	{
			programString.append( TAB + "fire_rule[" + dt.getName() + "] = " + "fr_" + dt.getName() + ";" + CR );
		}
		programString.append( "}" + CR2 );
	}

	private void generateCodeForExitRule()	{
		
		StringBuffer s = new StringBuffer();
		for( TMLPort port: postexList )	{
			s.append( "( sig[ " + port.getName() +" ].f == true ) &&" );
		}
		if( s.length() > 3 )	{
			programString.append( "bool exit_rule(void)\t{" + CR + TAB + "return " + s.toString().substring( 0, s.length() - 3 ) + SC + CR + "}" );
		}
		else	{
			programString.append( "bool exit_rule(void)\t{" + CR + TAB + "return " + s + SC + CR + "}" );
		}
	}

	private void generateInitFile( ArrayList<TMLTask> mappedTasks )	{
		
		String init_code = "";
		String XOD = "";
		initFileString.append( "#include \"" + applicationName + "_exec.h\"" + CR2 );
		initFileString.append( "/**** variables ****/" + CR2 +
									"int g_r_size = 10240;" + CR +
									"int g_Ns = 1024;" + CR +
									"int g_Fi = 593;" + CR +
									"int g_Li = 116;" + CR +
									"char *src_out_dat;" + CR +
									"char *dma1_out_dat;" + CR2 );		
		initFileString.append( buffersAndInstructionsDeclaration( false ) + CR2 );
		initFileString.append( CR + initializeApplication() + CR2 );
		initFileString.append( initializeSignals() + CR );
		initFileString.append( "/**** init code ****/" + CR );

		//Only for SDR operations
		for( Operation op: operationsList )	{
			if( op.getType() == Operation.SDR )	{
				TMLTask xTask = op.getSDRTasks().get( Operation.X_TASK );
				TMLTask fTask = op.getSDRTasks().get( Operation.X_TASK );
				OperationMEC xTaskOperation = xTask.getOperationMEC();
				OperationMEC fTaskOperation = fTask.getOperationMEC();
				XOD = xTask.getName().split("__")[1];
				if( xTaskOperation instanceof CwpMEC )	{
					CwpMEC cwp = new CwpMEC( XOD, xTask.getID0(), xTask.getOD0(), "" );
					init_code = cwp.getInitCode();
				}
				else if( xTaskOperation instanceof CwmMEC )	{
					CwmMEC cwm = new CwmMEC( XOD, xTask.getID0(), xTask.getOD0(), "" );
					init_code = cwm.getInitCode();
				}
				else if( xTaskOperation instanceof CwaMEC )	{
					CwaMEC cwa = new CwaMEC( XOD, xTask.getID0(), "", xTask.getOD0(), "" );
					init_code = cwa.getInitCode();
				}
				else if( xTaskOperation instanceof CwlMEC )	{
					CwlMEC cwl = new CwlMEC( XOD, xTask.getID0(), xTask.getOD0(), "" );
					init_code = cwl.getInitCode();
				}
				else if( xTaskOperation instanceof SumMEC )	{
					SumMEC sum = new SumMEC( XOD, xTask.getID0(), xTask.getOD0(), "" );
					init_code = sum.getInitCode();
				}
				else if( xTaskOperation instanceof FftMEC )	{
					FftMEC fft = new FftMEC( XOD, xTask.getID0(), xTask.getOD0(), "" );
					init_code = fft.getInitCode();
				}
				else if( xTaskOperation instanceof IntlOperationMEC )	{
					IntlOperationMEC intl = new IntlOperationMEC( XOD, xTask.getID0(), xTask.getOD0(), "" );
					initFileString.append( intl.getInitCode() + CR );
				}
				else if( xTaskOperation instanceof MappOperationMEC )	{
					MappOperationMEC mapp = new MappOperationMEC( XOD, xTask.getID0(), xTask.getOD0(), "" );
					initFileString.append( mapp.getInitCode() + CR );
				}
				else if( xTaskOperation instanceof AdaifOperationMEC )	{
					AdaifOperationMEC adaif = new AdaifOperationMEC( XOD, xTask.getID0(), xTask.getOD0(), "" );
					initFileString.append( adaif.getInitCode() + CR );
				}
			initFileString.append( init_code + CR );
			init_code = "";
			}
		}

		generateInitRoutinesForCPs();

		initFileString.append( "/**** init contexts ****/" + CR + "void init_operations_context(void)\t{" + CR );
		for( Operation op: operationsList )	{
			if( op.getType() == Operation.SDR )	{
				TMLTask xTask = op.getSDRTasks().get( Operation.X_TASK );
				//TMLTask fTask = op.getSDRTasks().get( Operation.X_TASK );
				//OperationMEC xTaskOperation = xTask.getOperationMEC();
				ArchUnitMEC xTaskArchMEC = tmap.getHwNodeOf( xTask ).getArchUnitMEC();
				if( xTaskArchMEC instanceof FepMEC )	{
					initFileString.append( TAB + "init_" + xTask.getTaskName() + "();" + CR );
				}
				if( xTaskArchMEC instanceof MapperMEC )	{
					initFileString.append( TAB + "init_" + xTask.getTaskName() + "();" + CR );
				}
				if( xTaskArchMEC instanceof InterleaverMEC )	{
					initFileString.append( TAB + "init_" + xTask.getTaskName() + "();" + CR );
				}
				if( xTaskArchMEC instanceof AdaifMEC )	{
					initFileString.append( TAB + "init_" + xTask.getTaskName() + "();" + CR );
				}
			}
		}
		initFileString.append( "}" + CR2 );

		//Init Communication Patterns. Only DMA transfers need init code
		initFileString.append( "/**** init CPs ****/" + CR + "void init_CPs_context(void)\t{" + CR );
		for( TMLCPLib cplib: mappedCPLibs )	{
			String s = cplib.getMappedUnits().get(0);
			if( s.split("\\.")[0].contains( "DMA" ) )	{
				initFileString.append( TAB + "init_" + cplib.getName() + "();" + CR );
			}
		}
		initFileString.append( "}" + CR2 );

		//Clean-up context routines
		initFileString.append( "/**** cleanup contexts ****/" + CR );
		initFileString.append( "void cleanup_operations_context( void )\t{" + CR );
		for( Operation op: operationsList )	{
			if( op.getType() == Operation.SDR )	{
				TMLTask xTask = op.getSDRTasks().get( Operation.X_TASK );
				//TMLTask fTask = op.getSDRTasks().get( Operation.X_TASK );
				//OperationMEC xTaskOperation = xTask.getOperationMEC();
				ArchUnitMEC xTaskArchMEC = tmap.getHwNodeOf( xTask ).getArchUnitMEC();
				if( xTaskArchMEC instanceof FepMEC )	{
					initFileString.append( TAB + FepMEC.Ctx_cleanup + "( &" + xTask.getTaskName() + "_ctx );" + CR );
				}
				if( xTaskArchMEC instanceof MapperMEC )	{
					initFileString.append( TAB + MapperMEC.Ctx_cleanup + "( &" + xTask.getTaskName() + "_ctx );" + CR );
				}
				if( xTaskArchMEC instanceof InterleaverMEC )	{
					initFileString.append( TAB + InterleaverMEC.Ctx_cleanup + "( &" + xTask.getTaskName() + "_ctx );" + CR );
				}
				if( xTaskArchMEC instanceof AdaifMEC )	{
					initFileString.append( TAB + AdaifMEC.Ctx_cleanup + "( &" + xTask.getTaskName() + "_ctx );" + CR );
				}
			}
		}
		initFileString.append( "}" + CR2 );
		initFileString.append( "void cleanup_CPs_context( void )\t{" + CR );
		for( DataTransfer dt: dataTransfersList )	{
			TMLCPLib tmlcplib = dt.getTMLCPLib();
			CPMEC cpMEC = tmlcplib.getCPMEC();
			TraceManager.addDev( "cpMEC from tmlcplib " + cpMEC );
			if( cpMEC instanceof CpuMemoryCopyMEC )	{
				initFileString.append( TAB + CpuMemoryCopyMEC.Ctx_cleanup + "( &" + tmlcplib.getName() + "_ctx );" + CR );
			}
			if( cpMEC instanceof SingleDmaMEC )	{
				initFileString.append( TAB + SingleDmaMEC.Ctx_cleanup + "( &" + tmlcplib.getName() + "_ctx );" + CR );
			}
			if( cpMEC instanceof DoubleDmaMEC )	{
				initFileString.append( TAB + DoubleDmaMEC.Ctx_cleanup + "( &" + tmlcplib.getName() + "_ctx );" + CR );
			}
		}
		initFileString.append( "}" + CR );
	}

	private void generateInitRoutinesForCPs()	{

		for( DataTransfer dt: dataTransfersList )	{
			TMLCPLib tmlcplib = dt.getTMLCPLib();
			CPMEC cpMEC = tmlcplib.getCPMEC();
			if( cpMEC instanceof CpuMemoryCopyMEC )	{
				initFileString.append( "void init_" + tmlcplib.getName() + "()\t{" + CR );
				CpuMemoryCopyMEC mec = new CpuMemoryCopyMEC();
				initFileString.append( TAB + mec.getInitCode() + "}" + CR2 );
			}
			if( cpMEC instanceof SingleDmaMEC )	{
				initFileString.append( "void init_" + tmlcplib.getName() + "()\t{" + CR );
				SingleDmaMEC mec = new SingleDmaMEC();
				initFileString.append( TAB + mec.getInitCode() + "}" + CR2 );
			}
			if( cpMEC instanceof DoubleDmaMEC )	{
				initFileString.append( "void init_" + tmlcplib.getName() + "()\t{" + CR );
				DoubleDmaMEC mec = new DoubleDmaMEC();
				initFileString.append( TAB + mec.getInitCode() + "}" + CR2 );
			}
		}
	}

	private String initializeApplication()	{

		String s = "void " + applicationName + "_exec_init()\t{" + CR;

		for( Buffer buff: buffersList )	{
			s += TAB + buff.getName() + SP + "=" + SP + "{/* USER TO DO*/, /* USER TO DO*/, /* USER TO DO*/};" + CR;
		}

		s += "}" + CR2;
		return s;
	}

	private String initializeSignals()	{
		String s = "void signal_to_buffer_init()\t{" + CR;
		for( int i = 0; i < signalsCounter; i++ )	{
			s += TAB + "sig[" + signalsList.get(i) + "].f = false;" + CR;
			s += TAB + "sig[" + signalsList.get(i) + "].roff = /*USER TO DO*/;" + CR;
			s += TAB + "sig[" + signalsList.get(i) + "].woff = /*USER TO DO*/;" + CR;
			s += TAB + "sig[" + signalsList.get(i) + "].pBuff = /*USER TO DO*/;" + CR2;
		}
		return s + "}" + SC + CR;
	}

	//for code generation scheduling, transform a join channel into a set of basic channels
	public ArrayList<TMLChannel> transformIntoBasicChannels( TMLChannel originalCh )	{

		String chName, appName, dstPortName;
		int numSrcPorts;
		TMLChannel channel;
		ArrayList<TMLChannel> channelsList = new ArrayList<TMLChannel>();

		if( originalCh.isAJoinChannel() )	{
			String[] s = originalCh.getName().split("__");
			numSrcPorts = s.length-2;
			appName = s[0];
			dstPortName = s[ s.length-1 ];
			for( int i = 0; i < numSrcPorts; i++ )	{
				chName = appName + "__" + s[i+1] + "__" + appName + "__" + dstPortName;
				channel = new TMLChannel( chName, null );
				channel.setPorts( originalCh.getOriginPorts().get(i), originalCh.getDestinationPorts().get(0) );
				channel.setTasks( originalCh.getOriginTasks().get(i), originalCh.getDestinationTasks().get(0) );
				channel.setType( originalCh.getType() );
				channel.setSize( originalCh.getSize() );
				channel.setMax( originalCh.getMax() );
				channelsList.add( channel );
			}
		}
		return channelsList;
	}
	
	private static String prepareString(String s) {
		return s.replaceAll("\\s", "");
	}
	
	public static String modifyString(String s) {
		return prepareString(s);
	}

	public String toString()	{
		return headerString.toString() + programString.toString();
	}

	public void saveFile( String path, String filename ) throws FileException {
		
		TraceManager.addUser( "Saving C files in " + path + filename );
		FileUtils.saveFile( path + "main.c", mainFileString.toString() );
		FileUtils.saveFile( path + filename + ".h", headerString.toString() );
		FileUtils.saveFile( path + filename + ".c", programString.toString() );
		FileUtils.saveFile( path + filename + "_init.c", initFileString.toString() );
	}

	private void openDebugFile()	{
		File fileObject = new File( debugFileName );
		fileObject.delete();
		outputStream = null;
		try	{
			outputStream = new PrintWriter( new FileOutputStream( debugFileName, true ) );
		}
		catch( FileNotFoundException e )	{
			System.out.println( "Error opening file " + debugFileName );
		}
	}

	private void appendToDebugFile( String s )	{
		outputStream.println( s );
	}

	private void closeDebugFile()	{
		outputStream.close();
	}

  public void addError( String message, int type )	{
		TMLCCodeGenerationError error = new TMLCCodeGenerationError( type );
		error.message = message;
		errors.add( error );
	}

  public ArrayList<TMLCCodeGenerationError> getErrors() {
		return errors;
	}

	public boolean hasErrors()	{
		if( errors.size() > 0 )	{
			return true;
		}
		return false;
	}

}	//End of class
