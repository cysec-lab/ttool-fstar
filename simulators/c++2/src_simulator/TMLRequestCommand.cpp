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

#include <TMLRequestCommand.h>
#include <TMLEventBChannel.h>
#include <TMLTask.h>
#include <TMLTransaction.h>
#include <Bus.h>

TMLRequestCommand::TMLRequestCommand(TMLTask* iTask,TMLEventBChannel* iChannel,Parameter<ParamType>* iParam):TMLCommand(iTask,WAIT_SEND_VLEN,iParam),_channel(iChannel){
}

void TMLRequestCommand::execute(){
	_channel->write(_currTransaction);
	//std::cout << "Dependent Task: " << _channel->getBlockedReadTask()->toString() << std::endl;
	_progress+=_currTransaction->getVirtualLength();
	//std::cout << "setEndLastTrans Virtual length " << std::endl;
	_task->setEndLastTransaction(_currTransaction->getEndTime());
	//_currTransaction=0;
#if defined BUS_ENABLED && defined EVENTS_MAPPED_ON_BUS
	//std::cout << "Bus OPS " << std::endl;
	Bus* bus=_channel->getBus();
	if (bus!=0) bus->addTransaction();
#endif
	//std::cout << "prepare " << std::endl;
	if (!prepare()) _currTransaction->setTerminatedFlag();
	if (_progress==0) _currTransaction=0;
}

bool TMLRequestCommand::prepareNextTransaction(){
	//std::cout << "prepare bext transaction testWrite prg:" << _progress << " to execute:" << (*_pLength)-_progress << std::endl;
	//_currTransaction=new TMLTransaction(this,_progress,(*_pLength)-_progress,_task->getEndLastTransaction());
	_currTransaction=new TMLTransaction(this,_progress,_length-_progress,_task->getEndLastTransaction());
	_channel->testWrite(_currTransaction);
	return true;
}

TMLTask* TMLRequestCommand::getDependentTask() const{
	//std::cout << "getDepTask " << std::endl;
	return _channel->getBlockedReadTask();
}

TMLChannel* TMLRequestCommand::getChannel() const{
	//std::cout << "getChannel " << std::endl;
	return (TMLChannel*)_channel;
}

std::string TMLRequestCommand::toString(){
	std::ostringstream outp;
	outp << "Request in " << TMLCommand::toString() << " " << _channel->toString();
	return outp.str();
}

std::string TMLRequestCommand::toShortString(){
	std::ostringstream outp;
	outp << _task->toString() << ": Request " << _channel->toShortString();
	return outp.str();
}

std::string TMLRequestCommand::getCommandStr(){
	return "sendReq";
}
