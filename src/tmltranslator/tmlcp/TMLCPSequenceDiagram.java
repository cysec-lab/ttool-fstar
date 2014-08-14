/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici

ludovic.apvrille AT telecom-paristech.fr
andrea.enrici AT telecom-paristech.fr

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
* Class TMLCPSequenceDiagram. 
* Creation: 18/02/2014
* @version 1.1 18/05/2014
* @author Ludovic APVRILLE, Andrea ENRICI
* @see
*/

package tmltranslator.tmlcp;;

import java.util.*;

import tmltranslator.*;
import myutil.*;

public class TMLCPSequenceDiagram  extends TMLElement {

	private ArrayList<TMLSDInstance> instances; 
	private ArrayList<TMLSDInstance> mappingInstances;
	private ArrayList<TMLAttribute> globalVariables;
	private ArrayList<TMLSDMessage> messages; 
	//used to sort messages and actions according to their order in the graphical window, to produce the TMLTxt code
	private ArrayList<TMLSDItem> items; 	
	private ArrayList<TMLSDAction> actions;
	
	private int hashCode;
	private boolean hashCodeComputed = false;
	
    
	public TMLCPSequenceDiagram( String _name, Object _referenceObject )	{
		super( _name, _referenceObject );
		init();
	}

	private void init() {
		globalVariables = new ArrayList<TMLAttribute>();
		instances = new ArrayList<TMLSDInstance>();
		messages = new ArrayList<TMLSDMessage>();
		actions = new ArrayList<TMLSDAction>();
		items = new ArrayList<TMLSDItem>();
	}
    
 	/*public void addVariable( TMLAttribute _attr ) throws MultipleVariableDeclarationException	{

		if( !checkVariableUniqueness( _attr.getName() ) )	{
			String errorMessage = "TMLCOMPILER ERROR: variable " + _attr.getName() + " in diagram " + this.name + " has mutliple declarations";
			throw new MultipleVariableDeclarationException( errorMessage );
		}
		else	{
      globalVariables.add(_attr);
    }
	}*/

	public ArrayList<TMLSDItem> getItems()	{
		return items;
	}

	public void addItem( TMLSDItem _item )	{
		items.add( _item );
	}

	public void addVariable( TMLAttribute _attr )	{
		globalVariables.add( _attr );
	}

	public void addAttribute( TMLAttribute _attribute )	{	//used by the graphical 2 TMLTxt compiler
      globalVariables.add( _attribute );
	}
	
	public ArrayList<TMLAttribute> getAttributes() {
		return globalVariables;
	}

	public void addAttribute( TMLSDAction _action ) {
		actions.add( _action );
	}

	public ArrayList<TMLSDAction> getActions() {
		return actions;
	}

	public void addAction( TMLSDAction _action ) {
		actions.add( _action );
		addItem( new TMLSDItem( _action.getAction(), _action.getYCoord() ) );
	}
	
	//commenting the throw exception because bot needed by the graphical 2 TMLTxt compiler yet
	/*public void addInstance( TMLSDInstance _elt ) throws MultipleInstanceDeclarationException {
		
		if( declaredInstance( _elt ) )	{
			String errorMessage = "TMLCP COMPILER ERROR: instance " + _elt.getName() + " in diagram " + this.name + " declared multiple times";
			throw new MultipleInstanceDeclarationException( errorMessage );
		}
		else	{
	    instances.add( _elt );
		}
 	}*/

	public void addInstance( TMLSDInstance _inst )	{
		instances.add( _inst );
	}

	public void addMappingInstance( TMLSDInstance _elt ) {
    mappingInstances.add( _elt );
 	}
   
	public ArrayList<TMLSDInstance> getInstances()	{
		return instances;
	}
	
	public ArrayList<TMLSDInstance> getMappingInstances()	{
		return mappingInstances;
	}
	
	public void addMessage( TMLSDMessage _msg ) {
  	messages.add( _msg );
		addItem( new TMLSDItem( _msg.getName(), _msg.getYCoord() ) );
  }
    
	public void insertInitialValue( String _name, String value ) throws UninitializedVariableException	{
			
		int i = 0;
		String str;
		TMLAttribute tempAttr;
		TMLType tempType, _attrType;
		TMLAttribute _attr = new TMLAttribute( _name, new TMLType(1) );

		for( i = 0; i < globalVariables.size(); i++ )	{
			tempAttr = globalVariables.get(i);
			str = tempAttr.getName();
			if( str.equals( _attr.getName() ) )	{
				tempType = tempAttr.getType();
				_attrType = _attr.getType();
				if( tempType.getType() == _attrType.getType() )	{
					_attr.initialValue = value;
					globalVariables.set( i, _attr );
					return;
				}
			}
		}
		String errorMessage = "TMLCOMPILER ERROR: variable " + _name + " in diagram " + this.name + " is not initialized";
		throw new UninitializedVariableException( errorMessage );
	}

	public boolean containsInstance( String _name )	{
		
		int i, instCounter = 0;
		TMLSDInstance inst;

		for( i = 0; i < instances.size(); i++ )	{
			inst = instances.get(i);
			if( _name.equals( inst.getName() ) )	{
				instCounter++;
			}
		}
		return ( instCounter != 0 );
	}

	public int isVariableInitialized( String _name )	{

		int i, countNotDecl = 0;
		TMLAttribute attr;

		for( i = 0; i < globalVariables.size(); i++ )	{
			attr = globalVariables.get(i);
			if( _name.equals( attr.getName() ) )	{
				countNotDecl++;
				if( attr.getInitialValue() == null )	{
					return 0;
				}
			}
		}
		if( countNotDecl > 1 )	{
			return 1;	//declared multiple times
		}
		else	{
			if( countNotDecl == 0 )	{
				return 2;	//not declared
			}
			else	{
				return 3;		//everything is okay
			}
		}
	}

	private boolean declaredInstance( TMLSDInstance _inst )	{
		
		int i;
		String instName;
		ArrayList<TMLSDInstance> list;
		TMLSDInstance inst;

		list = getInstances();
		if( list.size() == 0 )	{
			return false;
		}
		else	{
			for( i = 0 ; i < list.size(); i++ )	{
				inst = list.get(i);
				instName = inst.getName();
				if( instName.equals( _inst.getName() ) )	{
					return true;
				}
			}
			return false;
		}
	}

	private boolean checkVariableUniqueness( String _name )	{
		
		int i;
		ArrayList<TMLAttribute> list;
		TMLAttribute attr;

		list = getAttributes();
		for( i = 0 ; i < list.size(); i++ )	{
			attr = list.get(i);
			if( _name.equals( attr.getName() ) )	{
				return false;
			}
		}
		return true;
	}

	public ArrayList<TMLSDMessage> getMessages()	{
		return messages;
	}

	public TMLSDInstance retrieveInstance( String _name )	{
			
			ArrayList<TMLSDInstance> instList;
			TMLSDInstance inst;
			int i;

			for( i = 0; i < instances.size(); i++ )	{
				inst = instances.get(i);
				if( _name.equals( inst.getName() ) )	{
					instances.remove(i);
					return inst;
				}
			}
			return new TMLSDInstance( "error", null, "NO_TYPE" );
	}

}	//End of class
