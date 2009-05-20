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

#include <TMLTask.h>
#include <TMLCommand.h>
#include <CPU.h>

TMLTask::TMLTask(unsigned int iID, unsigned int iPriority, std::string iName, CPU* iCPU): _ID(iID), _name(iName), _priority(iPriority), _endLastTransaction(0), _currCommand(0), _firstCommand(0), _cpu(iCPU), _previousTransEndTime(0), _comment(0), _busyCycles(0), _CPUContentionDelay(0), _noCPUTransactions(0) {
	//_myid=++_id;
	_cpu->registerTask(this);
#ifdef ADD_COMMENTS
	_commentList.reserve(BLOCK_SIZE);
#endif
	_transactList.reserve(BLOCK_SIZE);
}

TMLTask::~TMLTask(){
#ifdef ADD_COMMENTS
	for(CommentList::iterator i=_commentList.begin(); i != _commentList.end(); ++i){
		delete *i;
	}
#endif
	if (_comment!=0) delete [] _comment;
}

unsigned int TMLTask::getPriority() const{
	return _priority;
}

TMLTime TMLTask::getEndLastTransaction() const{
	return _endLastTransaction;
}

TMLCommand* TMLTask::getCurrCommand() const{
	return _currCommand;
}

void TMLTask::setCurrCommand(TMLCommand* iCurrCommand){
	//std::cout << _name << "currentcommand: " << iCurrCommand->toString() << std::endl;
	_currCommand=iCurrCommand;
}

CPU* TMLTask::getCPU() const{
	return _cpu;
}

std::string TMLTask::toString() const{
	return _name;
}

std::string TMLTask::toShortString() const{
	std::ostringstream outp;
	outp << "ta" << _ID;
	return outp.str();
}

unsigned int TMLTask::getID() const{
	return _ID;
}

#ifdef ADD_COMMENTS
void TMLTask::addComment(Comment* iComment){
	_commentList.push_back(iComment);
}

std::string TMLTask::getNextComment(bool iInit, Comment*& oComment){
	if (iInit) _posCommentList=_commentList.begin();
	if (_posCommentList == _commentList.end()){
		//std::cout << "ret0\n";
		oComment=0;
		return std::string("no more comment");
	}
	//std::cout << "NON0\n";
	oComment=*_posCommentList;
	_posCommentList++;
	return ((oComment->_command==0)?_comment[oComment->_actionCode]:oComment->_command->getCommentString(oComment));
}
#endif

void TMLTask::addTransaction(TMLTransaction* iTrans){
	_transactList.push_back(iTrans);
	_endLastTransaction=iTrans->getEndTime();
	_busyCycles+=iTrans->getOperationLength();
	FOR_EACH_TRANSLISTENER (*i)->transExecuted(iTrans);
	if(iTrans->getChannel()==0){
		_noCPUTransactions++;
		_CPUContentionDelay+=iTrans->getStartTime()-iTrans->getRunnableTime();
	}
}

TMLTime TMLTask::getNextSignalChange(bool iInit, std::string& oSigChange, bool& oNoMoreTrans){
	std::ostringstream outp;
	if (iInit){
		//std::cout << "Init" << std::endl; 
		_posTrasactList=_transactList.begin();
		//std::cout << "Init2" << std::endl; 
		_previousTransEndTime=0;
		_vcdOutputState=END_TRANS;
	}
	if (_posTrasactList == _transactList.end()){
		if (iInit || _transactList.back()->getTerminatedFlag()){
			outp << VCD_PREFIX << vcdValConvert(TERMINATED) << " ta" << _ID;
		}else{
			outp << VCD_PREFIX << vcdValConvert(SUSPENDED) << " ta" << _ID;
		}
		oSigChange=outp.str();
		oNoMoreTrans=true;
		return _previousTransEndTime;
	}else{
		//std::cout << "VCD out trans: " << (*_posTrasactList)->toShortString() << std::endl;
		TMLTransaction* aCurrTrans=*_posTrasactList;
		oNoMoreTrans=false;
		switch (_vcdOutputState){
			case END_TRANS:
				if (aCurrTrans->getRunnableTime()==_previousTransEndTime){
					outp << VCD_PREFIX << vcdValConvert(RUNNABLE) << " ta" << _ID;
					_vcdOutputState=START_TRANS;
				}else{
					outp << VCD_PREFIX << vcdValConvert(SUSPENDED) << " ta" << _ID;
					if (aCurrTrans->getRunnableTime()==aCurrTrans->getStartTimeOperation()){
						_vcdOutputState=START_TRANS;
					}else{
						_vcdOutputState=BETWEEN_TRANS;
					}
				}
				oSigChange=outp.str();
				return _previousTransEndTime;
			break;
			case BETWEEN_TRANS:
				outp << VCD_PREFIX << vcdValConvert(RUNNABLE) << " ta" << _ID;
				oSigChange=outp.str();
				_vcdOutputState=START_TRANS;
				return aCurrTrans->getRunnableTime();
			break;
			case START_TRANS:
				outp << VCD_PREFIX << vcdValConvert(RUNNING) << " ta" << _ID;
				oSigChange=outp.str();
				do{
					_previousTransEndTime=(*_posTrasactList)->getEndTime();
					_posTrasactList++;
				}while (_posTrasactList != _transactList.end() && (*_posTrasactList)->getStartTimeOperation()==_previousTransEndTime);
				_vcdOutputState=END_TRANS;
				return aCurrTrans->getStartTimeOperation();
			break;
		}
	}
}

std::ostream& TMLTask::writeObject(std::ostream& s){
	unsigned int aCurrCmd;
	WRITE_STREAM(s,_endLastTransaction);
	std::cout << "Write: TMLTask " << _name << " endLastTransaction: " << _endLastTransaction << std::endl;
	if (_currCommand==0){
		aCurrCmd=0;
		WRITE_STREAM(s,aCurrCmd);
		std::cout << "Write: TMLTask " << _name << " aCurrCmd: " << aCurrCmd << std::endl;
	}else{
		aCurrCmd=_currCommand->getID();
		//if (aCurrCmd>1000 && aCurrCmd!=((unsigned int)-1)){
		//	std::cout << "BIIIIIIIIIIIIIIIIIIIIIIIG number: " << aCurrCmd << std::endl;
		//}
		WRITE_STREAM(s,aCurrCmd);
		std::cout << "Write: TMLTask " << _name << " aCurrCmd: " << aCurrCmd << std::endl;
		_currCommand->writeObject(s);
	}
	return s;
}

std::istream& TMLTask::readObject(std::istream& s){
	unsigned int aCurrCmd;
	//_previousTransEndTime=0; _busyCycles=0; _CPUContentionDelay=0; _noCPUTransactions=0;
	READ_STREAM(s, _endLastTransaction);
	std::cout << "Read: TMLTask " << _name << " endLastTransaction: " << _endLastTransaction << std::endl;
	READ_STREAM(s, aCurrCmd);
	std::cout << "Read: TMLTask " << _name << " aCurrCmd: " << aCurrCmd << std::endl;
	if (aCurrCmd!=0){
		//std::cout << "cmd ID: " << aCurrCmd << std::endl;
		_currCommand=getCommandByID(aCurrCmd);
		//std::cout << "cmd adr: " << _currCommand << std::endl;
		//std::cout << "before read cmd " << std::endl;
		 _currCommand->readObject(s);
		//_currCommand->prepare();
	}
	//std::cout << "End Read Object TMLTask " << _name << std::endl;
	return s;
}

void TMLTask::streamBenchmarks(std::ostream& s) const{
	s << "*** Task " << _name << " ***\n"; 
	s << "Execution time: " << _busyCycles << std::endl;
	if (_noCPUTransactions!=0) s << "Average CPU contention delay: " << (static_cast<float>(_CPUContentionDelay)/static_cast<float>(_noCPUTransactions)) << std::endl;
}

void TMLTask::reset(){
	//std::cout << "task reset" << std::endl;
	_endLastTransaction=0;
	if (_currCommand!=0) _currCommand->reset();
	_currCommand=_firstCommand;
	if (_currCommand!=0) _currCommand->reset();
#ifdef ADD_COMMENTS
	_commentList.clear();
#endif
	_transactList.clear();
	_busyCycles=0;
	_CPUContentionDelay=0;
	_noCPUTransactions=0; 
}

ParamType* TMLTask::getVariableByName(std::string& iVarName){
	return _varLookUp[iVarName.c_str()];
	//return (_varLookUp.find(iVarName.c_str()))->second;
}

void TMLTask::addCommand(unsigned int iID, TMLCommand* iCmd){
	_commandHash[iID]=iCmd;
}

TMLCommand* TMLTask::getCommandByID(unsigned int iID){
	return _commandHash[iID];
}
