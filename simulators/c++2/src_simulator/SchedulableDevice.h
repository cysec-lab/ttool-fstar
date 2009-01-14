/*Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Daniel Knorreck,
Ludovic Apvrille, Renaud Pacalet
 *
 * ludovic.apvrille AT telecom-paristech.fr
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
 */

#ifndef SchedulableDeviceH
#define SchedulableDeviceH

#include <definitions.h>
#include <Serializable.h>
class Master;

class TMLTransaction;

///Base class for devices which perform a scheduling
class SchedulableDevice: public Serializable{
public:
	SchedulableDevice():_endSchedule(0){}
	///Determines the next transaction to be executed
	virtual void schedule()=0;
	///Adds the transaction determined by the scheduling algorithm to the internal list of scheduled transactions
	virtual bool addTransaction()=0;
	///Returns a pointer to the transaction determined by the scheduling algorithm
    	/**
      	\return Pointer to transaction
    	*/
	virtual TMLTransaction* getNextTransaction()=0;
	///Writes a HTML representation of the schedule to an output file
	/**
      	\param myfile Reference to the ofstream object representing the output file
    	*/
	///Add a transaction waiting for execution to the internal list
	/**
      	\param iTrans Pointer to the transaction to add
	\param iSourceDevice Source device
    	*/
	virtual void registerTransaction(TMLTransaction* iTrans, Master* iSourceDevice)=0;
	///Writes a HTML representation of the schedule to an output file
	/**
      	\param myfile Reference to the ofstream object representing the output file
    	*/
	virtual void schedule2HTML(std::ofstream& myfile)=0;
	///Writes a plain text representation of the schedule to an output file
	/**
      	\param myfile Reference to the ofstream object representing the output file
    	*/
	virtual void schedule2TXT(std::ofstream& myfile)=0;
	virtual std::string toString()=0;
	virtual std::istream& readObject(std::istream &is){
		READ_STREAM(is,_endSchedule);
		return is;
	}
	virtual std::ostream& writeObject(std::ostream &os){
		WRITE_STREAM(os,_endSchedule);		
		return os;
	}
	static TMLTime getSimulatedTime(){return _simulatedTime;}
	///Destructor
	virtual ~SchedulableDevice(){}
protected:
	///Class variable holding the simulation time
	static TMLTime _simulatedTime;
	///End time of the last scheduled transaction
	TMLTime _endSchedule;
};

#endif
