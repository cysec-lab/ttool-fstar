/*Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Niu Siyuan,
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

#ifndef FPGA_H
#define FPGA_H


#include <definitions.h>
#include <SchedulableDevice.h>
#include <SchedulableCommDevice.h>
#include <TraceableDevice.h>
#include <BusMaster.h>
#include <WorkloadSource.h>
#include <TMLTask.h>
#include <TMLCommand.h>

class TMLTask;
class TMLTransaction;
class Bus;

enum vcdFPGAVisState
    {
	END_IDLE_FPGA,
	END_PENALTY_FPGA,
	END_TASK_FPGA
};


///Simulates the bahavior of a FPGA shared by several master devices
class FPGA: public SchedulableDevice, public TraceableDevice {
public:
///Constructor
	/**
	\param iID ID of the device
	\param iName Name of the device
	\param iReconfigTime reconfiguration time
	\param iChangeIdleModeCycles Cycles needed to switch into indle mode
	\param iCyclesBeforeIdle Pointer to the max consecutive cycles before idle in cycle
	\param iCyclesPerExeci Cycles needed to execute one EXECI unit
	\param iCyclesPerExecc Cycles needed to execute one EXECC unit
	*/
	
        FPGA(ID iID, std::string iName, TMLTime iReconfigTime, unsigned int iChangeIdleModeCycles, unsigned int iCyclesBeforeIdle,unsigned int iCyclesPerExeci, unsigned int iCyclesPerExecc);
	///Destructor
	virtual ~FPGA();
	///Determines the next FPGA transaction to be executed
	virtual void schedule();
	///Adds the transaction determined by the scheduling algorithm to the internal list of 		scheduled transactions
	virtual bool addTransaction(TMLTransaction* iTransToBeAdded);
	void truncateAndAddNextTransAt(TMLTime iTime);
	///Returns a pointer to the transaction determined by the scheduling algorithm
    	/**
      	\return Pointer to transaction
    	*/
	TMLTransaction* getNextTransaction();
	void getNextSignalChange(bool iInit, SignalChangeData* oSigData);
	///Returns a short string representation of the transaction
	/**
	\return Short string representation
	*/
	std::string toShortString() const;
	inline std::string toString() const {return _name;}
	///Writes a plain text representation of the schedule to an output file
	/**
      	\param myfile Reference to the ofstream object representing the output file
    	*/
	void schedule2TXT(std::ofstream& myfile) const;

	/**
      	\param glob refers to the output stream
    	*/
	int allTrans2XML(std::ostringstream& glob, int maxNbOfTrans) const;
	void latencies2XML(std::ostringstream& glob, unsigned int id1, unsigned int id2);
	//virtual TMLTime truncateNextTransAt(TMLTime iTime)=0;
	///Adds a new bus master to the internal list
	/**
	\param iMaster Pointer to bus master 
	*/
	virtual void streamBenchmarks(std::ostream& s) const;
	inline void streamStateXML(std::ostream& s) const {streamBenchmarks(s);}
	virtual void addBusMaster(BusMaster* iMaster){
		_busMasterList.push_back(iMaster);
	}
	///Stores a new task in the internal task list
	/**
      	\param iTask Pointer to the task to add
    	*/
	virtual void registerTask(TMLTask* iTask){
		_taskList.push_back(iTask);
	}
	inline void setTransNumber(unsigned int num) { _transNumber=num;}
	inline unsigned int getTransNumber() { return _transNumber;}
	double averageLoad (TMLTask* currTask) const;
	void drawPieChart(std::ofstream& myfile) const;
	void showPieChart(std::ofstream& myfile) const;
	void schedule2HTML(std::ofstream& myfile) const;
	inline const TaskList& getTaskList() const{return _taskList;}
	inline void setHtmlCurrTask(TMLTask *t) { _htmlCurrTask=t;}
protected:
	///List of all tasks running on the FPGA
	TaskList _taskList;
	TMLTask* _htmlCurrTask;
	/**
	\param iTime Indicates at what time the transaction should be truncated
	*/
	TMLTime truncateNextTransAt(TMLTime iTime);
	///Calculates the start time and the length of the next transaction
	/**
	\param iTimeSlice FPGA Time slice granted by the scheduler
	*/
	void calcStartTimeLength();

	TMLTime _reconfigTime;


	///Determines the correct bus master of this CPU connected to the same bus as bus master iDummy
	/**
	\param iDummy Dummy Bus Master
	*/
	BusMaster* getMasterForBus(BusMaster* iDummy);
	///Pointer to the bus which will be accessed by the next transaction
	BusMaster* _masterNextTransaction;
	///Pointer to the last transaction which has been executed
	TMLTransaction* _lastTransaction;
	///List of bus masters
	BusMasterList _busMasterList;

 		
	///Cycles needed to switch to idle mode
	unsigned int _changeIdleModeCycles;
	///Idle cycles which elapse before entering idle mode
	unsigned int _cyclesBeforeIdle;
 
	///Cycles needed to execute one execi unit
	unsigned int _cyclesPerExeci;
	unsigned int _cyclesPerExecc;
	///Time needed to execute one execi unit
	float _timePerExeci;
 
	///Idle time which elapses before entering idle mode
	TMLTime _timeBeforeIdle;
	///Time needed to switch into idle mode
	TMLTime _changeIdleModeTime;
	unsigned int _transNumber;
 		
	///State variable for the VCD output
	vcdFPGAVisState _vcdOutputState;
};

#endif






















