#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>

#include "request.h"
#include "myerrors.h"
#include "message.h"
#include "syncchannel.h"
#include "asyncchannel.h"
#include "mytimelib.h"
#include "request_manager.h"
#include "defs.h"
#include "debug.h"
#include "random.h"
#include "tracemanager.h" 
#include "mwmr.h" 
 

#define NB_PROC 2
#define WIDTH 4
#define DEPTH 16

void __user_init() {
}

#include "InterfaceDevice.h"
#include "SmartCard.h"
#include "TCPIP.h"
#include "TCPPacketManager.h"
#include "Application.h"
#include "SmartCardController.h"

/* Main mutex */
pthread_barrier_t barrier ;
pthread_attr_t *attr_t;
pthread_mutex_t __mainMutex;

#define CHANNEL0 __attribute__((section("section_channel0")))
#define LOCK0 __attribute__((section("section_lock0")))
#define CHANNEL1 __attribute__((section("section_channel1")))
#define LOCK1 __attribute__((section("section_lock1")))
#define CHANNEL2 __attribute__((section("section_channel2")))
#define LOCK2 __attribute__((section("section_lock2")))
#define CHANNEL3 __attribute__((section("section_channel3")))
#define LOCK3 __attribute__((section("section_lock3")))
#define CHANNEL4 __attribute__((section("section_channel4")))
#define LOCK4 __attribute__((section("section_lock4")))
#define CHANNEL5 __attribute__((section("section_channel5")))
#define LOCK5 __attribute__((section("section_lock5")))
#define CHANNEL6 __attribute__((section("section_channel6")))
#define LOCK6 __attribute__((section("section_lock6")))
#define base(arg) arg

typedef struct mwmr_s mwmr_t;

/* Synchronous channels */
syncchannel __TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket;
uint32_t const TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket_lock LOCK0;
struct mwmr_status_s TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket_status CHANNEL0;
uint8_t TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket_data[32] CHANNEL0;
struct mwmr_s TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket CHANNEL0;

syncchannel __TCPIP_emptyListOfPackets__TCPPacketManager_empty;
uint32_t const TCPIP_emptyListOfPackets__TCPPacketManager_empty_lock LOCK0;
struct mwmr_status_s TCPIP_emptyListOfPackets__TCPPacketManager_empty_status CHANNEL0;
uint8_t TCPIP_emptyListOfPackets__TCPPacketManager_empty_data[32] CHANNEL0;
struct mwmr_s TCPIP_emptyListOfPackets__TCPPacketManager_empty CHANNEL0;

syncchannel __TCPIP_addPacket__TCPPacketManager_addPacket;
uint32_t const TCPIP_addPacket__TCPPacketManager_addPacket_lock LOCK0;
struct mwmr_status_s TCPIP_addPacket__TCPPacketManager_addPacket_status CHANNEL0;
uint8_t TCPIP_addPacket__TCPPacketManager_addPacket_data[32] CHANNEL0;
struct mwmr_s TCPIP_addPacket__TCPPacketManager_addPacket CHANNEL0;

syncchannel __TCPIP_ackPacket__TCPPacketManager_ackPacket;
uint32_t const TCPIP_ackPacket__TCPPacketManager_ackPacket_lock LOCK0;
struct mwmr_status_s TCPIP_ackPacket__TCPPacketManager_ackPacket_status CHANNEL0;
uint8_t TCPIP_ackPacket__TCPPacketManager_ackPacket_data[32] CHANNEL0;
struct mwmr_s TCPIP_ackPacket__TCPPacketManager_ackPacket CHANNEL0;

syncchannel __Application_open__TCPIP_open;
uint32_t const Application_open__TCPIP_open_lock LOCK1;
struct mwmr_status_s Application_open__TCPIP_open_status CHANNEL1;
uint8_t Application_open__TCPIP_open_data[32] CHANNEL1;
struct mwmr_s Application_open__TCPIP_open CHANNEL1;

syncchannel __Application_close__TCPIP_close;
uint32_t const Application_close__TCPIP_close_lock LOCK1;
struct mwmr_status_s Application_close__TCPIP_close_status CHANNEL1;
uint8_t Application_close__TCPIP_close_data[32] CHANNEL1;
struct mwmr_s Application_close__TCPIP_close CHANNEL1;

syncchannel __Application_abort__TCPIP_abort;
uint32_t const Application_abort__TCPIP_abort_lock LOCK1;
struct mwmr_status_s Application_abort__TCPIP_abort_status CHANNEL1;
uint8_t Application_abort__TCPIP_abort_data[32] CHANNEL1;
struct mwmr_s Application_abort__TCPIP_abort CHANNEL1;

syncchannel __Application_sendTCP__TCPIP_send_TCP;
uint32_t const Application_sendTCP__TCPIP_send_TCP_lock LOCK1;
struct mwmr_status_s Application_sendTCP__TCPIP_send_TCP_status CHANNEL1;
uint8_t Application_sendTCP__TCPIP_send_TCP_data[32] CHANNEL1;
struct mwmr_s Application_sendTCP__TCPIP_send_TCP CHANNEL1;

syncchannel __SmartCardController_fromTtoP__TCPIP_fromTtoP;
uint32_t const SmartCardController_fromTtoP__TCPIP_fromTtoP_lock LOCK2;
struct mwmr_status_s SmartCardController_fromTtoP__TCPIP_fromTtoP_status CHANNEL2;
uint8_t SmartCardController_fromTtoP__TCPIP_fromTtoP_data[32] CHANNEL2;
struct mwmr_s SmartCardController_fromTtoP__TCPIP_fromTtoP CHANNEL2;

syncchannel __SmartCardController_fromPtoT__TCPIP_fromPtoT;
uint32_t const SmartCardController_fromPtoT__TCPIP_fromPtoT_lock LOCK2;
struct mwmr_status_s SmartCardController_fromPtoT__TCPIP_fromPtoT_status CHANNEL2;
uint8_t SmartCardController_fromPtoT__TCPIP_fromPtoT_data[32] CHANNEL2;
struct mwmr_s SmartCardController_fromPtoT__TCPIP_fromPtoT CHANNEL2;

syncchannel __SmartCardController_start_TCPIP__TCPIP_start;
uint32_t const SmartCardController_start_TCPIP__TCPIP_start_lock LOCK2;
struct mwmr_status_s SmartCardController_start_TCPIP__TCPIP_start_status CHANNEL2;
uint8_t SmartCardController_start_TCPIP__TCPIP_start_data[32] CHANNEL2;
struct mwmr_s SmartCardController_start_TCPIP__TCPIP_start CHANNEL2;

syncchannel __SmartCardController_reset__InterfaceDevice_reset;
uint32_t const SmartCardController_reset__InterfaceDevice_reset_lock LOCK3;
struct mwmr_status_s SmartCardController_reset__InterfaceDevice_reset_status CHANNEL3;
uint8_t SmartCardController_reset__InterfaceDevice_reset_data[32] CHANNEL3;
struct mwmr_s SmartCardController_reset__InterfaceDevice_reset CHANNEL3;

syncchannel __SmartCardController_pTS__InterfaceDevice_pTS;
uint32_t const SmartCardController_pTS__InterfaceDevice_pTS_lock LOCK3;
struct mwmr_status_s SmartCardController_pTS__InterfaceDevice_pTS_status CHANNEL3;
uint8_t SmartCardController_pTS__InterfaceDevice_pTS_data[32] CHANNEL3;
struct mwmr_s SmartCardController_pTS__InterfaceDevice_pTS CHANNEL3;

syncchannel __SmartCardController_dataReady__InterfaceDevice_data_Ready;
uint32_t const SmartCardController_dataReady__InterfaceDevice_data_Ready_lock LOCK3;
struct mwmr_status_s SmartCardController_dataReady__InterfaceDevice_data_Ready_status CHANNEL3;
uint8_t SmartCardController_dataReady__InterfaceDevice_data_Ready_data[32] CHANNEL3;
struct mwmr_s SmartCardController_dataReady__InterfaceDevice_data_Ready CHANNEL3;

syncchannel __SmartCardController_activation__InterfaceDevice_activation;
uint32_t const SmartCardController_activation__InterfaceDevice_activation_lock LOCK3;
struct mwmr_status_s SmartCardController_activation__InterfaceDevice_activation_status CHANNEL3;
uint8_t SmartCardController_activation__InterfaceDevice_activation_data[32] CHANNEL3;
struct mwmr_s SmartCardController_activation__InterfaceDevice_activation CHANNEL3;

syncchannel __SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC;
uint32_t const SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC_lock LOCK3;
struct mwmr_status_s SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC_status CHANNEL3;
uint8_t SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC_data[32] CHANNEL3;
struct mwmr_s SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC CHANNEL3;

syncchannel __SmartCardController_answerToReset__InterfaceDevice_answerToReset;
uint32_t const SmartCardController_answerToReset__InterfaceDevice_answerToReset_lock LOCK3;
struct mwmr_status_s SmartCardController_answerToReset__InterfaceDevice_answerToReset_status CHANNEL3;
uint8_t SmartCardController_answerToReset__InterfaceDevice_answerToReset_data[32] CHANNEL3;
struct mwmr_s SmartCardController_answerToReset__InterfaceDevice_answerToReset CHANNEL3;

syncchannel __SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm;
uint32_t const SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm_lock LOCK3;
struct mwmr_status_s SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm_status CHANNEL3;
uint8_t SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm_data[32] CHANNEL3;
struct mwmr_s SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm CHANNEL3;

syncchannel __SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD;
uint32_t const SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD_lock LOCK3;
struct mwmr_status_s SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD_status CHANNEL3;
uint8_t SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD_data[32] CHANNEL3;
struct mwmr_s SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD CHANNEL3;

syncchannel __SmartCardController_data_Ready_SC__InterfaceDevice_dataReady;
uint32_t const SmartCardController_data_Ready_SC__InterfaceDevice_dataReady_lock LOCK3;
struct mwmr_status_s SmartCardController_data_Ready_SC__InterfaceDevice_dataReady_status CHANNEL3;
uint8_t SmartCardController_data_Ready_SC__InterfaceDevice_dataReady_data[32] CHANNEL3;
struct mwmr_s SmartCardController_data_Ready_SC__InterfaceDevice_dataReady CHANNEL3;

syncchannel __SmartCardController_start_Application__Application_startApplication;
uint32_t const SmartCardController_start_Application__Application_startApplication_lock LOCK4;
struct mwmr_status_s SmartCardController_start_Application__Application_startApplication_status CHANNEL4;
uint8_t SmartCardController_start_Application__Application_startApplication_data[32] CHANNEL4;
struct mwmr_s SmartCardController_start_Application__Application_startApplication CHANNEL4;

syncchannel __TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set;
uint32_t const TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set_lock LOCK5;
struct mwmr_status_s TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set_status CHANNEL5;
uint8_t TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set_data[32] CHANNEL5;
struct mwmr_s TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set CHANNEL5;

syncchannel __TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset;
uint32_t const TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset_lock LOCK5;
struct mwmr_status_s TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset_status CHANNEL5;
uint8_t TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset_data[32] CHANNEL5;
struct mwmr_s TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset CHANNEL5;

syncchannel __TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire;
uint32_t const TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire_lock LOCK5;
struct mwmr_status_s TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire_status CHANNEL5;
uint8_t TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire_data[32] CHANNEL5;
struct mwmr_s TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire CHANNEL5;

syncchannel __TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set;
uint32_t const TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set_lock LOCK6;
struct mwmr_status_s TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set_status CHANNEL6;
uint8_t TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set_data[32] CHANNEL6;
struct mwmr_s TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set CHANNEL6;

syncchannel __TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset;
uint32_t const TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset_lock LOCK6;
struct mwmr_status_s TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset_status CHANNEL6;
uint8_t TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset_data[32] CHANNEL6;
struct mwmr_s TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset CHANNEL6;

syncchannel __TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire;
uint32_t const TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire_lock LOCK6;
struct mwmr_status_s TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire_status CHANNEL6;
uint8_t TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire_data[32] CHANNEL6;
struct mwmr_s TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire CHANNEL6;

/* Asynchronous channels */
asyncchannel __TCPIP_receiveTCP__Application_receiveTCP;
uint32_t const TCPIP_receiveTCP__Application_receiveTCP_lock LOCK0;
struct mwmr_status_s TCPIP_receiveTCP__Application_receiveTCP_status CHANNEL0;
uint8_t TCPIP_receiveTCP__Application_receiveTCP_data[32] CHANNEL0;
struct mwmr_s TCPIP_receiveTCP__Application_receiveTCP CHANNEL0;

asyncchannel __TCPPacketManager_storePacket__TCPPacketManager_retrieve;
uint32_t const TCPPacketManager_storePacket__TCPPacketManager_retrieve_lock LOCK1;
struct mwmr_status_s TCPPacketManager_storePacket__TCPPacketManager_retrieve_status CHANNEL1;
uint8_t TCPPacketManager_storePacket__TCPPacketManager_retrieve_data[32] CHANNEL1;
struct mwmr_s TCPPacketManager_storePacket__TCPPacketManager_retrieve CHANNEL1;


int main(int argc, char *argv[]) {
  
  
  void *ptr;
  pthread_barrier_init(&barrier,NULL, NB_PROC);
  pthread_attr_t *attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_init(attr_t);
  pthread_mutex_init(&__mainMutex, NULL);
  
  /* Synchronous channels */
  TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket_status.rptr = 0;
  TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket_status.wptr = 0;
  TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket_status.usage = 0;
  TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket_status.lock = 0;
  
  TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket.width = 1;
  TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket.depth = 1;
  TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket.gdepth = 1;
  TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket.buffer = TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket_data;
  TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket.status = &TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket_status;
  
  __TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket.inname ="timeoutPacket";
  __TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket.outname ="timeoutPacket";
  __TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket.mwmr_fifo = &TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket;
  TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket.status =&TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket_status;
  TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket.status->lock=0;
  TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket.status->rptr=0;
  TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket.status->usage=0;
  TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket.status->wptr =0;
  TCPIP_emptyListOfPackets__TCPPacketManager_empty_status.rptr = 0;
  TCPIP_emptyListOfPackets__TCPPacketManager_empty_status.wptr = 0;
  TCPIP_emptyListOfPackets__TCPPacketManager_empty_status.usage = 0;
  TCPIP_emptyListOfPackets__TCPPacketManager_empty_status.lock = 0;
  
  TCPIP_emptyListOfPackets__TCPPacketManager_empty.width = 1;
  TCPIP_emptyListOfPackets__TCPPacketManager_empty.depth = 1;
  TCPIP_emptyListOfPackets__TCPPacketManager_empty.gdepth = 1;
  TCPIP_emptyListOfPackets__TCPPacketManager_empty.buffer = TCPIP_emptyListOfPackets__TCPPacketManager_empty_data;
  TCPIP_emptyListOfPackets__TCPPacketManager_empty.status = &TCPIP_emptyListOfPackets__TCPPacketManager_empty_status;
  
  __TCPIP_emptyListOfPackets__TCPPacketManager_empty.inname ="empty";
  __TCPIP_emptyListOfPackets__TCPPacketManager_empty.outname ="emptyListOfPackets";
  __TCPIP_emptyListOfPackets__TCPPacketManager_empty.mwmr_fifo = &TCPIP_emptyListOfPackets__TCPPacketManager_empty;
  TCPIP_emptyListOfPackets__TCPPacketManager_empty.status =&TCPIP_emptyListOfPackets__TCPPacketManager_empty_status;
  TCPIP_emptyListOfPackets__TCPPacketManager_empty.status->lock=0;
  TCPIP_emptyListOfPackets__TCPPacketManager_empty.status->rptr=0;
  TCPIP_emptyListOfPackets__TCPPacketManager_empty.status->usage=0;
  TCPIP_emptyListOfPackets__TCPPacketManager_empty.status->wptr =0;
  TCPIP_addPacket__TCPPacketManager_addPacket_status.rptr = 0;
  TCPIP_addPacket__TCPPacketManager_addPacket_status.wptr = 0;
  TCPIP_addPacket__TCPPacketManager_addPacket_status.usage = 0;
  TCPIP_addPacket__TCPPacketManager_addPacket_status.lock = 0;
  
  TCPIP_addPacket__TCPPacketManager_addPacket.width = 1;
  TCPIP_addPacket__TCPPacketManager_addPacket.depth = 1;
  TCPIP_addPacket__TCPPacketManager_addPacket.gdepth = 1;
  TCPIP_addPacket__TCPPacketManager_addPacket.buffer = TCPIP_addPacket__TCPPacketManager_addPacket_data;
  TCPIP_addPacket__TCPPacketManager_addPacket.status = &TCPIP_addPacket__TCPPacketManager_addPacket_status;
  
  __TCPIP_addPacket__TCPPacketManager_addPacket.inname ="addPacket";
  __TCPIP_addPacket__TCPPacketManager_addPacket.outname ="addPacket";
  __TCPIP_addPacket__TCPPacketManager_addPacket.mwmr_fifo = &TCPIP_addPacket__TCPPacketManager_addPacket;
  TCPIP_addPacket__TCPPacketManager_addPacket.status =&TCPIP_addPacket__TCPPacketManager_addPacket_status;
  TCPIP_addPacket__TCPPacketManager_addPacket.status->lock=0;
  TCPIP_addPacket__TCPPacketManager_addPacket.status->rptr=0;
  TCPIP_addPacket__TCPPacketManager_addPacket.status->usage=0;
  TCPIP_addPacket__TCPPacketManager_addPacket.status->wptr =0;
  TCPIP_ackPacket__TCPPacketManager_ackPacket_status.rptr = 0;
  TCPIP_ackPacket__TCPPacketManager_ackPacket_status.wptr = 0;
  TCPIP_ackPacket__TCPPacketManager_ackPacket_status.usage = 0;
  TCPIP_ackPacket__TCPPacketManager_ackPacket_status.lock = 0;
  
  TCPIP_ackPacket__TCPPacketManager_ackPacket.width = 1;
  TCPIP_ackPacket__TCPPacketManager_ackPacket.depth = 1;
  TCPIP_ackPacket__TCPPacketManager_ackPacket.gdepth = 1;
  TCPIP_ackPacket__TCPPacketManager_ackPacket.buffer = TCPIP_ackPacket__TCPPacketManager_ackPacket_data;
  TCPIP_ackPacket__TCPPacketManager_ackPacket.status = &TCPIP_ackPacket__TCPPacketManager_ackPacket_status;
  
  __TCPIP_ackPacket__TCPPacketManager_ackPacket.inname ="ackPacket";
  __TCPIP_ackPacket__TCPPacketManager_ackPacket.outname ="ackPacket";
  __TCPIP_ackPacket__TCPPacketManager_ackPacket.mwmr_fifo = &TCPIP_ackPacket__TCPPacketManager_ackPacket;
  TCPIP_ackPacket__TCPPacketManager_ackPacket.status =&TCPIP_ackPacket__TCPPacketManager_ackPacket_status;
  TCPIP_ackPacket__TCPPacketManager_ackPacket.status->lock=0;
  TCPIP_ackPacket__TCPPacketManager_ackPacket.status->rptr=0;
  TCPIP_ackPacket__TCPPacketManager_ackPacket.status->usage=0;
  TCPIP_ackPacket__TCPPacketManager_ackPacket.status->wptr =0;
  Application_open__TCPIP_open_status.rptr = 0;
  Application_open__TCPIP_open_status.wptr = 0;
  Application_open__TCPIP_open_status.usage = 0;
  Application_open__TCPIP_open_status.lock = 0;
  
  Application_open__TCPIP_open.width = 1;
  Application_open__TCPIP_open.depth = 1;
  Application_open__TCPIP_open.gdepth = 1;
  Application_open__TCPIP_open.buffer = Application_open__TCPIP_open_data;
  Application_open__TCPIP_open.status = &Application_open__TCPIP_open_status;
  
  __Application_open__TCPIP_open.inname ="open";
  __Application_open__TCPIP_open.outname ="open";
  __Application_open__TCPIP_open.mwmr_fifo = &Application_open__TCPIP_open;
  Application_open__TCPIP_open.status =&Application_open__TCPIP_open_status;
  Application_open__TCPIP_open.status->lock=0;
  Application_open__TCPIP_open.status->rptr=0;
  Application_open__TCPIP_open.status->usage=0;
  Application_open__TCPIP_open.status->wptr =0;
  Application_close__TCPIP_close_status.rptr = 0;
  Application_close__TCPIP_close_status.wptr = 0;
  Application_close__TCPIP_close_status.usage = 0;
  Application_close__TCPIP_close_status.lock = 0;
  
  Application_close__TCPIP_close.width = 1;
  Application_close__TCPIP_close.depth = 1;
  Application_close__TCPIP_close.gdepth = 1;
  Application_close__TCPIP_close.buffer = Application_close__TCPIP_close_data;
  Application_close__TCPIP_close.status = &Application_close__TCPIP_close_status;
  
  __Application_close__TCPIP_close.inname ="close";
  __Application_close__TCPIP_close.outname ="close";
  __Application_close__TCPIP_close.mwmr_fifo = &Application_close__TCPIP_close;
  Application_close__TCPIP_close.status =&Application_close__TCPIP_close_status;
  Application_close__TCPIP_close.status->lock=0;
  Application_close__TCPIP_close.status->rptr=0;
  Application_close__TCPIP_close.status->usage=0;
  Application_close__TCPIP_close.status->wptr =0;
  Application_abort__TCPIP_abort_status.rptr = 0;
  Application_abort__TCPIP_abort_status.wptr = 0;
  Application_abort__TCPIP_abort_status.usage = 0;
  Application_abort__TCPIP_abort_status.lock = 0;
  
  Application_abort__TCPIP_abort.width = 1;
  Application_abort__TCPIP_abort.depth = 1;
  Application_abort__TCPIP_abort.gdepth = 1;
  Application_abort__TCPIP_abort.buffer = Application_abort__TCPIP_abort_data;
  Application_abort__TCPIP_abort.status = &Application_abort__TCPIP_abort_status;
  
  __Application_abort__TCPIP_abort.inname ="abort";
  __Application_abort__TCPIP_abort.outname ="abort";
  __Application_abort__TCPIP_abort.mwmr_fifo = &Application_abort__TCPIP_abort;
  Application_abort__TCPIP_abort.status =&Application_abort__TCPIP_abort_status;
  Application_abort__TCPIP_abort.status->lock=0;
  Application_abort__TCPIP_abort.status->rptr=0;
  Application_abort__TCPIP_abort.status->usage=0;
  Application_abort__TCPIP_abort.status->wptr =0;
  Application_sendTCP__TCPIP_send_TCP_status.rptr = 0;
  Application_sendTCP__TCPIP_send_TCP_status.wptr = 0;
  Application_sendTCP__TCPIP_send_TCP_status.usage = 0;
  Application_sendTCP__TCPIP_send_TCP_status.lock = 0;
  
  Application_sendTCP__TCPIP_send_TCP.width = 1;
  Application_sendTCP__TCPIP_send_TCP.depth = 1;
  Application_sendTCP__TCPIP_send_TCP.gdepth = 1;
  Application_sendTCP__TCPIP_send_TCP.buffer = Application_sendTCP__TCPIP_send_TCP_data;
  Application_sendTCP__TCPIP_send_TCP.status = &Application_sendTCP__TCPIP_send_TCP_status;
  
  __Application_sendTCP__TCPIP_send_TCP.inname ="send_TCP";
  __Application_sendTCP__TCPIP_send_TCP.outname ="sendTCP";
  __Application_sendTCP__TCPIP_send_TCP.mwmr_fifo = &Application_sendTCP__TCPIP_send_TCP;
  Application_sendTCP__TCPIP_send_TCP.status =&Application_sendTCP__TCPIP_send_TCP_status;
  Application_sendTCP__TCPIP_send_TCP.status->lock=0;
  Application_sendTCP__TCPIP_send_TCP.status->rptr=0;
  Application_sendTCP__TCPIP_send_TCP.status->usage=0;
  Application_sendTCP__TCPIP_send_TCP.status->wptr =0;
  SmartCardController_fromTtoP__TCPIP_fromTtoP_status.rptr = 0;
  SmartCardController_fromTtoP__TCPIP_fromTtoP_status.wptr = 0;
  SmartCardController_fromTtoP__TCPIP_fromTtoP_status.usage = 0;
  SmartCardController_fromTtoP__TCPIP_fromTtoP_status.lock = 0;
  
  SmartCardController_fromTtoP__TCPIP_fromTtoP.width = 1;
  SmartCardController_fromTtoP__TCPIP_fromTtoP.depth = 1;
  SmartCardController_fromTtoP__TCPIP_fromTtoP.gdepth = 1;
  SmartCardController_fromTtoP__TCPIP_fromTtoP.buffer = SmartCardController_fromTtoP__TCPIP_fromTtoP_data;
  SmartCardController_fromTtoP__TCPIP_fromTtoP.status = &SmartCardController_fromTtoP__TCPIP_fromTtoP_status;
  
  __SmartCardController_fromTtoP__TCPIP_fromTtoP.inname ="fromTtoP";
  __SmartCardController_fromTtoP__TCPIP_fromTtoP.outname ="fromTtoP";
  __SmartCardController_fromTtoP__TCPIP_fromTtoP.mwmr_fifo = &SmartCardController_fromTtoP__TCPIP_fromTtoP;
  SmartCardController_fromTtoP__TCPIP_fromTtoP.status =&SmartCardController_fromTtoP__TCPIP_fromTtoP_status;
  SmartCardController_fromTtoP__TCPIP_fromTtoP.status->lock=0;
  SmartCardController_fromTtoP__TCPIP_fromTtoP.status->rptr=0;
  SmartCardController_fromTtoP__TCPIP_fromTtoP.status->usage=0;
  SmartCardController_fromTtoP__TCPIP_fromTtoP.status->wptr =0;
  SmartCardController_fromPtoT__TCPIP_fromPtoT_status.rptr = 0;
  SmartCardController_fromPtoT__TCPIP_fromPtoT_status.wptr = 0;
  SmartCardController_fromPtoT__TCPIP_fromPtoT_status.usage = 0;
  SmartCardController_fromPtoT__TCPIP_fromPtoT_status.lock = 0;
  
  SmartCardController_fromPtoT__TCPIP_fromPtoT.width = 1;
  SmartCardController_fromPtoT__TCPIP_fromPtoT.depth = 1;
  SmartCardController_fromPtoT__TCPIP_fromPtoT.gdepth = 1;
  SmartCardController_fromPtoT__TCPIP_fromPtoT.buffer = SmartCardController_fromPtoT__TCPIP_fromPtoT_data;
  SmartCardController_fromPtoT__TCPIP_fromPtoT.status = &SmartCardController_fromPtoT__TCPIP_fromPtoT_status;
  
  __SmartCardController_fromPtoT__TCPIP_fromPtoT.inname ="fromPtoT";
  __SmartCardController_fromPtoT__TCPIP_fromPtoT.outname ="fromPtoT";
  __SmartCardController_fromPtoT__TCPIP_fromPtoT.mwmr_fifo = &SmartCardController_fromPtoT__TCPIP_fromPtoT;
  SmartCardController_fromPtoT__TCPIP_fromPtoT.status =&SmartCardController_fromPtoT__TCPIP_fromPtoT_status;
  SmartCardController_fromPtoT__TCPIP_fromPtoT.status->lock=0;
  SmartCardController_fromPtoT__TCPIP_fromPtoT.status->rptr=0;
  SmartCardController_fromPtoT__TCPIP_fromPtoT.status->usage=0;
  SmartCardController_fromPtoT__TCPIP_fromPtoT.status->wptr =0;
  SmartCardController_start_TCPIP__TCPIP_start_status.rptr = 0;
  SmartCardController_start_TCPIP__TCPIP_start_status.wptr = 0;
  SmartCardController_start_TCPIP__TCPIP_start_status.usage = 0;
  SmartCardController_start_TCPIP__TCPIP_start_status.lock = 0;
  
  SmartCardController_start_TCPIP__TCPIP_start.width = 1;
  SmartCardController_start_TCPIP__TCPIP_start.depth = 1;
  SmartCardController_start_TCPIP__TCPIP_start.gdepth = 1;
  SmartCardController_start_TCPIP__TCPIP_start.buffer = SmartCardController_start_TCPIP__TCPIP_start_data;
  SmartCardController_start_TCPIP__TCPIP_start.status = &SmartCardController_start_TCPIP__TCPIP_start_status;
  
  __SmartCardController_start_TCPIP__TCPIP_start.inname ="start";
  __SmartCardController_start_TCPIP__TCPIP_start.outname ="start_TCPIP";
  __SmartCardController_start_TCPIP__TCPIP_start.mwmr_fifo = &SmartCardController_start_TCPIP__TCPIP_start;
  SmartCardController_start_TCPIP__TCPIP_start.status =&SmartCardController_start_TCPIP__TCPIP_start_status;
  SmartCardController_start_TCPIP__TCPIP_start.status->lock=0;
  SmartCardController_start_TCPIP__TCPIP_start.status->rptr=0;
  SmartCardController_start_TCPIP__TCPIP_start.status->usage=0;
  SmartCardController_start_TCPIP__TCPIP_start.status->wptr =0;
  SmartCardController_reset__InterfaceDevice_reset_status.rptr = 0;
  SmartCardController_reset__InterfaceDevice_reset_status.wptr = 0;
  SmartCardController_reset__InterfaceDevice_reset_status.usage = 0;
  SmartCardController_reset__InterfaceDevice_reset_status.lock = 0;
  
  SmartCardController_reset__InterfaceDevice_reset.width = 1;
  SmartCardController_reset__InterfaceDevice_reset.depth = 1;
  SmartCardController_reset__InterfaceDevice_reset.gdepth = 1;
  SmartCardController_reset__InterfaceDevice_reset.buffer = SmartCardController_reset__InterfaceDevice_reset_data;
  SmartCardController_reset__InterfaceDevice_reset.status = &SmartCardController_reset__InterfaceDevice_reset_status;
  
  __SmartCardController_reset__InterfaceDevice_reset.inname ="reset";
  __SmartCardController_reset__InterfaceDevice_reset.outname ="reset";
  __SmartCardController_reset__InterfaceDevice_reset.mwmr_fifo = &SmartCardController_reset__InterfaceDevice_reset;
  SmartCardController_reset__InterfaceDevice_reset.status =&SmartCardController_reset__InterfaceDevice_reset_status;
  SmartCardController_reset__InterfaceDevice_reset.status->lock=0;
  SmartCardController_reset__InterfaceDevice_reset.status->rptr=0;
  SmartCardController_reset__InterfaceDevice_reset.status->usage=0;
  SmartCardController_reset__InterfaceDevice_reset.status->wptr =0;
  SmartCardController_pTS__InterfaceDevice_pTS_status.rptr = 0;
  SmartCardController_pTS__InterfaceDevice_pTS_status.wptr = 0;
  SmartCardController_pTS__InterfaceDevice_pTS_status.usage = 0;
  SmartCardController_pTS__InterfaceDevice_pTS_status.lock = 0;
  
  SmartCardController_pTS__InterfaceDevice_pTS.width = 1;
  SmartCardController_pTS__InterfaceDevice_pTS.depth = 1;
  SmartCardController_pTS__InterfaceDevice_pTS.gdepth = 1;
  SmartCardController_pTS__InterfaceDevice_pTS.buffer = SmartCardController_pTS__InterfaceDevice_pTS_data;
  SmartCardController_pTS__InterfaceDevice_pTS.status = &SmartCardController_pTS__InterfaceDevice_pTS_status;
  
  __SmartCardController_pTS__InterfaceDevice_pTS.inname ="pTS";
  __SmartCardController_pTS__InterfaceDevice_pTS.outname ="pTS";
  __SmartCardController_pTS__InterfaceDevice_pTS.mwmr_fifo = &SmartCardController_pTS__InterfaceDevice_pTS;
  SmartCardController_pTS__InterfaceDevice_pTS.status =&SmartCardController_pTS__InterfaceDevice_pTS_status;
  SmartCardController_pTS__InterfaceDevice_pTS.status->lock=0;
  SmartCardController_pTS__InterfaceDevice_pTS.status->rptr=0;
  SmartCardController_pTS__InterfaceDevice_pTS.status->usage=0;
  SmartCardController_pTS__InterfaceDevice_pTS.status->wptr =0;
  SmartCardController_dataReady__InterfaceDevice_data_Ready_status.rptr = 0;
  SmartCardController_dataReady__InterfaceDevice_data_Ready_status.wptr = 0;
  SmartCardController_dataReady__InterfaceDevice_data_Ready_status.usage = 0;
  SmartCardController_dataReady__InterfaceDevice_data_Ready_status.lock = 0;
  
  SmartCardController_dataReady__InterfaceDevice_data_Ready.width = 1;
  SmartCardController_dataReady__InterfaceDevice_data_Ready.depth = 1;
  SmartCardController_dataReady__InterfaceDevice_data_Ready.gdepth = 1;
  SmartCardController_dataReady__InterfaceDevice_data_Ready.buffer = SmartCardController_dataReady__InterfaceDevice_data_Ready_data;
  SmartCardController_dataReady__InterfaceDevice_data_Ready.status = &SmartCardController_dataReady__InterfaceDevice_data_Ready_status;
  
  __SmartCardController_dataReady__InterfaceDevice_data_Ready.inname ="dataReady";
  __SmartCardController_dataReady__InterfaceDevice_data_Ready.outname ="data_Ready";
  __SmartCardController_dataReady__InterfaceDevice_data_Ready.mwmr_fifo = &SmartCardController_dataReady__InterfaceDevice_data_Ready;
  SmartCardController_dataReady__InterfaceDevice_data_Ready.status =&SmartCardController_dataReady__InterfaceDevice_data_Ready_status;
  SmartCardController_dataReady__InterfaceDevice_data_Ready.status->lock=0;
  SmartCardController_dataReady__InterfaceDevice_data_Ready.status->rptr=0;
  SmartCardController_dataReady__InterfaceDevice_data_Ready.status->usage=0;
  SmartCardController_dataReady__InterfaceDevice_data_Ready.status->wptr =0;
  SmartCardController_activation__InterfaceDevice_activation_status.rptr = 0;
  SmartCardController_activation__InterfaceDevice_activation_status.wptr = 0;
  SmartCardController_activation__InterfaceDevice_activation_status.usage = 0;
  SmartCardController_activation__InterfaceDevice_activation_status.lock = 0;
  
  SmartCardController_activation__InterfaceDevice_activation.width = 1;
  SmartCardController_activation__InterfaceDevice_activation.depth = 1;
  SmartCardController_activation__InterfaceDevice_activation.gdepth = 1;
  SmartCardController_activation__InterfaceDevice_activation.buffer = SmartCardController_activation__InterfaceDevice_activation_data;
  SmartCardController_activation__InterfaceDevice_activation.status = &SmartCardController_activation__InterfaceDevice_activation_status;
  
  __SmartCardController_activation__InterfaceDevice_activation.inname ="activation";
  __SmartCardController_activation__InterfaceDevice_activation.outname ="activation";
  __SmartCardController_activation__InterfaceDevice_activation.mwmr_fifo = &SmartCardController_activation__InterfaceDevice_activation;
  SmartCardController_activation__InterfaceDevice_activation.status =&SmartCardController_activation__InterfaceDevice_activation_status;
  SmartCardController_activation__InterfaceDevice_activation.status->lock=0;
  SmartCardController_activation__InterfaceDevice_activation.status->rptr=0;
  SmartCardController_activation__InterfaceDevice_activation.status->usage=0;
  SmartCardController_activation__InterfaceDevice_activation.status->wptr =0;
  SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC_status.rptr = 0;
  SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC_status.wptr = 0;
  SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC_status.usage = 0;
  SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC_status.lock = 0;
  
  SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC.width = 1;
  SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC.depth = 1;
  SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC.gdepth = 1;
  SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC.buffer = SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC_data;
  SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC.status = &SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC_status;
  
  __SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC.inname ="fromDtoSC";
  __SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC.outname ="fromDtoSC";
  __SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC.mwmr_fifo = &SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC;
  SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC.status =&SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC_status;
  SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC.status->lock=0;
  SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC.status->rptr=0;
  SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC.status->usage=0;
  SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC.status->wptr =0;
  SmartCardController_answerToReset__InterfaceDevice_answerToReset_status.rptr = 0;
  SmartCardController_answerToReset__InterfaceDevice_answerToReset_status.wptr = 0;
  SmartCardController_answerToReset__InterfaceDevice_answerToReset_status.usage = 0;
  SmartCardController_answerToReset__InterfaceDevice_answerToReset_status.lock = 0;
  
  SmartCardController_answerToReset__InterfaceDevice_answerToReset.width = 1;
  SmartCardController_answerToReset__InterfaceDevice_answerToReset.depth = 1;
  SmartCardController_answerToReset__InterfaceDevice_answerToReset.gdepth = 1;
  SmartCardController_answerToReset__InterfaceDevice_answerToReset.buffer = SmartCardController_answerToReset__InterfaceDevice_answerToReset_data;
  SmartCardController_answerToReset__InterfaceDevice_answerToReset.status = &SmartCardController_answerToReset__InterfaceDevice_answerToReset_status;
  
  __SmartCardController_answerToReset__InterfaceDevice_answerToReset.inname ="answerToReset";
  __SmartCardController_answerToReset__InterfaceDevice_answerToReset.outname ="answerToReset";
  __SmartCardController_answerToReset__InterfaceDevice_answerToReset.mwmr_fifo = &SmartCardController_answerToReset__InterfaceDevice_answerToReset;
  SmartCardController_answerToReset__InterfaceDevice_answerToReset.status =&SmartCardController_answerToReset__InterfaceDevice_answerToReset_status;
  SmartCardController_answerToReset__InterfaceDevice_answerToReset.status->lock=0;
  SmartCardController_answerToReset__InterfaceDevice_answerToReset.status->rptr=0;
  SmartCardController_answerToReset__InterfaceDevice_answerToReset.status->usage=0;
  SmartCardController_answerToReset__InterfaceDevice_answerToReset.status->wptr =0;
  SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm_status.rptr = 0;
  SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm_status.wptr = 0;
  SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm_status.usage = 0;
  SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm_status.lock = 0;
  
  SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm.width = 1;
  SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm.depth = 1;
  SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm.gdepth = 1;
  SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm.buffer = SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm_data;
  SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm.status = &SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm_status;
  
  __SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm.inname ="pTSConfirm";
  __SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm.outname ="pTSCConfirm";
  __SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm.mwmr_fifo = &SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm;
  SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm.status =&SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm_status;
  SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm.status->lock=0;
  SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm.status->rptr=0;
  SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm.status->usage=0;
  SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm.status->wptr =0;
  SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD_status.rptr = 0;
  SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD_status.wptr = 0;
  SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD_status.usage = 0;
  SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD_status.lock = 0;
  
  SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD.width = 1;
  SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD.depth = 1;
  SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD.gdepth = 1;
  SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD.buffer = SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD_data;
  SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD.status = &SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD_status;
  
  __SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD.inname ="fromSCtoD";
  __SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD.outname ="fromSCtoD";
  __SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD.mwmr_fifo = &SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD;
  SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD.status =&SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD_status;
  SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD.status->lock=0;
  SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD.status->rptr=0;
  SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD.status->usage=0;
  SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD.status->wptr =0;
  SmartCardController_data_Ready_SC__InterfaceDevice_dataReady_status.rptr = 0;
  SmartCardController_data_Ready_SC__InterfaceDevice_dataReady_status.wptr = 0;
  SmartCardController_data_Ready_SC__InterfaceDevice_dataReady_status.usage = 0;
  SmartCardController_data_Ready_SC__InterfaceDevice_dataReady_status.lock = 0;
  
  SmartCardController_data_Ready_SC__InterfaceDevice_dataReady.width = 1;
  SmartCardController_data_Ready_SC__InterfaceDevice_dataReady.depth = 1;
  SmartCardController_data_Ready_SC__InterfaceDevice_dataReady.gdepth = 1;
  SmartCardController_data_Ready_SC__InterfaceDevice_dataReady.buffer = SmartCardController_data_Ready_SC__InterfaceDevice_dataReady_data;
  SmartCardController_data_Ready_SC__InterfaceDevice_dataReady.status = &SmartCardController_data_Ready_SC__InterfaceDevice_dataReady_status;
  
  __SmartCardController_data_Ready_SC__InterfaceDevice_dataReady.inname ="dataReady";
  __SmartCardController_data_Ready_SC__InterfaceDevice_dataReady.outname ="data_Ready_SC";
  __SmartCardController_data_Ready_SC__InterfaceDevice_dataReady.mwmr_fifo = &SmartCardController_data_Ready_SC__InterfaceDevice_dataReady;
  SmartCardController_data_Ready_SC__InterfaceDevice_dataReady.status =&SmartCardController_data_Ready_SC__InterfaceDevice_dataReady_status;
  SmartCardController_data_Ready_SC__InterfaceDevice_dataReady.status->lock=0;
  SmartCardController_data_Ready_SC__InterfaceDevice_dataReady.status->rptr=0;
  SmartCardController_data_Ready_SC__InterfaceDevice_dataReady.status->usage=0;
  SmartCardController_data_Ready_SC__InterfaceDevice_dataReady.status->wptr =0;
  SmartCardController_start_Application__Application_startApplication_status.rptr = 0;
  SmartCardController_start_Application__Application_startApplication_status.wptr = 0;
  SmartCardController_start_Application__Application_startApplication_status.usage = 0;
  SmartCardController_start_Application__Application_startApplication_status.lock = 0;
  
  SmartCardController_start_Application__Application_startApplication.width = 1;
  SmartCardController_start_Application__Application_startApplication.depth = 1;
  SmartCardController_start_Application__Application_startApplication.gdepth = 1;
  SmartCardController_start_Application__Application_startApplication.buffer = SmartCardController_start_Application__Application_startApplication_data;
  SmartCardController_start_Application__Application_startApplication.status = &SmartCardController_start_Application__Application_startApplication_status;
  
  __SmartCardController_start_Application__Application_startApplication.inname ="startApplication";
  __SmartCardController_start_Application__Application_startApplication.outname ="start_Application";
  __SmartCardController_start_Application__Application_startApplication.mwmr_fifo = &SmartCardController_start_Application__Application_startApplication;
  SmartCardController_start_Application__Application_startApplication.status =&SmartCardController_start_Application__Application_startApplication_status;
  SmartCardController_start_Application__Application_startApplication.status->lock=0;
  SmartCardController_start_Application__Application_startApplication.status->rptr=0;
  SmartCardController_start_Application__Application_startApplication.status->usage=0;
  SmartCardController_start_Application__Application_startApplication.status->wptr =0;
  TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set_status.rptr = 0;
  TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set_status.wptr = 0;
  TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set_status.usage = 0;
  TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set_status.lock = 0;
  
  TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set.width = 1;
  TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set.depth = 1;
  TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set.gdepth = 1;
  TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set.buffer = TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set_data;
  TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set.status = &TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set_status;
  
  __TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set.inname ="set";
  __TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set.outname ="set__mainTimer";
  __TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set.mwmr_fifo = &TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set;
  TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set.status =&TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set_status;
  TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set.status->lock=0;
  TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set.status->rptr=0;
  TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set.status->usage=0;
  TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set.status->wptr =0;
  TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset_status.rptr = 0;
  TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset_status.wptr = 0;
  TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset_status.usage = 0;
  TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset_status.lock = 0;
  
  TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset.width = 1;
  TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset.depth = 1;
  TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset.gdepth = 1;
  TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset.buffer = TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset_data;
  TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset.status = &TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset_status;
  
  __TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset.inname ="reset";
  __TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset.outname ="reset__mainTimer";
  __TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset.mwmr_fifo = &TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset;
  TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset.status =&TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset_status;
  TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset.status->lock=0;
  TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset.status->rptr=0;
  TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset.status->usage=0;
  TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset.status->wptr =0;
  TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire_status.rptr = 0;
  TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire_status.wptr = 0;
  TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire_status.usage = 0;
  TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire_status.lock = 0;
  
  TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire.width = 1;
  TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire.depth = 1;
  TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire.gdepth = 1;
  TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire.buffer = TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire_data;
  TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire.status = &TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire_status;
  
  __TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire.inname ="expire__mainTimer";
  __TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire.outname ="expire";
  __TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire.mwmr_fifo = &TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire;
  TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire.status =&TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire_status;
  TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire.status->lock=0;
  TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire.status->rptr=0;
  TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire.status->usage=0;
  TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire.status->wptr =0;
  TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set_status.rptr = 0;
  TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set_status.wptr = 0;
  TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set_status.usage = 0;
  TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set_status.lock = 0;
  
  TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set.width = 1;
  TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set.depth = 1;
  TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set.gdepth = 1;
  TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set.buffer = TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set_data;
  TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set.status = &TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set_status;
  
  __TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set.inname ="set";
  __TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set.outname ="set__timerP";
  __TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set.mwmr_fifo = &TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set;
  TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set.status =&TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set_status;
  TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set.status->lock=0;
  TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set.status->rptr=0;
  TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set.status->usage=0;
  TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set.status->wptr =0;
  TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset_status.rptr = 0;
  TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset_status.wptr = 0;
  TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset_status.usage = 0;
  TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset_status.lock = 0;
  
  TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset.width = 1;
  TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset.depth = 1;
  TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset.gdepth = 1;
  TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset.buffer = TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset_data;
  TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset.status = &TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset_status;
  
  __TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset.inname ="reset";
  __TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset.outname ="reset__timerP";
  __TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset.mwmr_fifo = &TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset;
  TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset.status =&TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset_status;
  TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset.status->lock=0;
  TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset.status->rptr=0;
  TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset.status->usage=0;
  TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset.status->wptr =0;
  TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire_status.rptr = 0;
  TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire_status.wptr = 0;
  TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire_status.usage = 0;
  TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire_status.lock = 0;
  
  TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire.width = 1;
  TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire.depth = 1;
  TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire.gdepth = 1;
  TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire.buffer = TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire_data;
  TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire.status = &TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire_status;
  
  __TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire.inname ="expire__timerP";
  __TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire.outname ="expire";
  __TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire.mwmr_fifo = &TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire;
  TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire.status =&TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire_status;
  TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire.status->lock=0;
  TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire.status->rptr=0;
  TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire.status->usage=0;
  TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire.status->wptr =0;
  /* Asynchronous channels */
  TCPIP_receiveTCP__Application_receiveTCP_status.rptr = 0;
  TCPIP_receiveTCP__Application_receiveTCP_status.wptr = 0;
  TCPIP_receiveTCP__Application_receiveTCP_status.usage = 0;
  TCPIP_receiveTCP__Application_receiveTCP_status.lock = 0;
  
  TCPIP_receiveTCP__Application_receiveTCP.width = 1;
  TCPIP_receiveTCP__Application_receiveTCP.depth = 51;
  TCPIP_receiveTCP__Application_receiveTCP.gdepth = TCPIP_receiveTCP__Application_receiveTCP.depth;
  TCPIP_receiveTCP__Application_receiveTCP.buffer = TCPIP_receiveTCP__Application_receiveTCP_data;
  TCPIP_receiveTCP__Application_receiveTCP.status = &TCPIP_receiveTCP__Application_receiveTCP_status;
  __TCPIP_receiveTCP__Application_receiveTCP.inname ="receiveTCP";
  __TCPIP_receiveTCP__Application_receiveTCP.outname ="receiveTCP";
  __TCPIP_receiveTCP__Application_receiveTCP.isBlocking = 0;
  __TCPIP_receiveTCP__Application_receiveTCP.maxNbOfMessages = 5;
  __TCPIP_receiveTCP__Application_receiveTCP.mwmr_fifo = &TCPIP_receiveTCP__Application_receiveTCP;
  TCPIP_receiveTCP__Application_receiveTCP.status =&TCPIP_receiveTCP__Application_receiveTCP_status;
  TCPIP_receiveTCP__Application_receiveTCP.status->lock=0;
  TCPIP_receiveTCP__Application_receiveTCP.status->rptr=0;
  TCPIP_receiveTCP__Application_receiveTCP.status->usage=0;
  TCPIP_receiveTCP__Application_receiveTCP.status->wptr=0;
  TCPPacketManager_storePacket__TCPPacketManager_retrieve_status.rptr = 0;
  TCPPacketManager_storePacket__TCPPacketManager_retrieve_status.wptr = 0;
  TCPPacketManager_storePacket__TCPPacketManager_retrieve_status.usage = 0;
  TCPPacketManager_storePacket__TCPPacketManager_retrieve_status.lock = 0;
  
  TCPPacketManager_storePacket__TCPPacketManager_retrieve.width = 1;
  TCPPacketManager_storePacket__TCPPacketManager_retrieve.depth = 51;
  TCPPacketManager_storePacket__TCPPacketManager_retrieve.gdepth = TCPPacketManager_storePacket__TCPPacketManager_retrieve.depth;
  TCPPacketManager_storePacket__TCPPacketManager_retrieve.buffer = TCPPacketManager_storePacket__TCPPacketManager_retrieve_data;
  TCPPacketManager_storePacket__TCPPacketManager_retrieve.status = &TCPPacketManager_storePacket__TCPPacketManager_retrieve_status;
  __TCPPacketManager_storePacket__TCPPacketManager_retrieve.inname ="retrieve";
  __TCPPacketManager_storePacket__TCPPacketManager_retrieve.outname ="storePacket";
  __TCPPacketManager_storePacket__TCPPacketManager_retrieve.isBlocking = 0;
  __TCPPacketManager_storePacket__TCPPacketManager_retrieve.maxNbOfMessages = 5;
  __TCPPacketManager_storePacket__TCPPacketManager_retrieve.mwmr_fifo = &TCPPacketManager_storePacket__TCPPacketManager_retrieve;
  TCPPacketManager_storePacket__TCPPacketManager_retrieve.status =&TCPPacketManager_storePacket__TCPPacketManager_retrieve_status;
  TCPPacketManager_storePacket__TCPPacketManager_retrieve.status->lock=0;
  TCPPacketManager_storePacket__TCPPacketManager_retrieve.status->rptr=0;
  TCPPacketManager_storePacket__TCPPacketManager_retrieve.status->usage=0;
  TCPPacketManager_storePacket__TCPPacketManager_retrieve.status->wptr=0;
  
  /* Threads of tasks */
  pthread_t thread__InterfaceDevice;
  pthread_t thread__SmartCard;
  pthread_t thread__TCPIP;
  pthread_t thread__TCPPacketManager;
  pthread_t thread__Application;
  pthread_t thread__SmartCardController;
  /* Activating tracing  */
  if (argc>1){
    activeTracingInFile(argv[1]);
  } else {
    activeTracingInConsole();
  }
  /* Activating randomness */
  initRandom();
  /* Initializing the main mutex */
if (pthread_mutex_init(&__mainMutex, NULL) < 0) { exit(-1);}
  
  /* User initialization */
  __user_init();
  
  
  struct mwmr_s *channels_array_InterfaceDevice[9];
  channels_array_InterfaceDevice[0]=&SmartCardController_reset__InterfaceDevice_reset;
  channels_array_InterfaceDevice[1]=&SmartCardController_pTS__InterfaceDevice_pTS;
  channels_array_InterfaceDevice[2]=&SmartCardController_dataReady__InterfaceDevice_data_Ready;
  channels_array_InterfaceDevice[3]=&SmartCardController_activation__InterfaceDevice_activation;
  channels_array_InterfaceDevice[4]=&SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC;
  channels_array_InterfaceDevice[5]=&SmartCardController_answerToReset__InterfaceDevice_answerToReset;
  channels_array_InterfaceDevice[6]=&SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm;
  channels_array_InterfaceDevice[7]=&SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD;
  channels_array_InterfaceDevice[8]=&SmartCardController_data_Ready_SC__InterfaceDevice_dataReady;
  
  ptr =malloc(sizeof(pthread_t));
  thread__InterfaceDevice= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 1);  
  
  
  pthread_create(&thread__InterfaceDevice, attr_t, mainFunc__InterfaceDevice, (void *)channels_array_InterfaceDevice);
  
  struct mwmr_s *channels_array_SmartCard;
  ptr =malloc(sizeof(pthread_t));
  thread__SmartCard= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__SmartCard, attr_t, mainFunc__SmartCard, (void *)channels_array_SmartCard);
  
  struct mwmr_s *channels_array_TCPIP[15];
  channels_array_TCPIP[0]=&TCPIP_receiveTCP__Application_receiveTCP;
  channels_array_TCPIP[1]=&TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket;
  channels_array_TCPIP[2]=&TCPIP_emptyListOfPackets__TCPPacketManager_empty;
  channels_array_TCPIP[3]=&TCPIP_addPacket__TCPPacketManager_addPacket;
  channels_array_TCPIP[4]=&TCPIP_ackPacket__TCPPacketManager_ackPacket;
  channels_array_TCPIP[5]=&Application_open__TCPIP_open;
  channels_array_TCPIP[6]=&Application_close__TCPIP_close;
  channels_array_TCPIP[7]=&Application_abort__TCPIP_abort;
  channels_array_TCPIP[8]=&Application_sendTCP__TCPIP_send_TCP;
  channels_array_TCPIP[9]=&SmartCardController_fromTtoP__TCPIP_fromTtoP;
  channels_array_TCPIP[10]=&SmartCardController_fromPtoT__TCPIP_fromPtoT;
  channels_array_TCPIP[11]=&SmartCardController_start_TCPIP__TCPIP_start;
  channels_array_TCPIP[12]=&TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set;
  channels_array_TCPIP[13]=&TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset;
  channels_array_TCPIP[14]=&TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire;
  
  ptr =malloc(sizeof(pthread_t));
  thread__TCPIP= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__TCPIP, attr_t, mainFunc__TCPIP, (void *)channels_array_TCPIP);
  
  struct mwmr_s *channels_array_TCPPacketManager[8];
  channels_array_TCPPacketManager[0]=&TCPPacketManager_storePacket__TCPPacketManager_retrieve;
  channels_array_TCPPacketManager[1]=&TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket;
  channels_array_TCPPacketManager[2]=&TCPIP_emptyListOfPackets__TCPPacketManager_empty;
  channels_array_TCPPacketManager[3]=&TCPIP_addPacket__TCPPacketManager_addPacket;
  channels_array_TCPPacketManager[4]=&TCPIP_ackPacket__TCPPacketManager_ackPacket;
  channels_array_TCPPacketManager[5]=&TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set;
  channels_array_TCPPacketManager[6]=&TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset;
  channels_array_TCPPacketManager[7]=&TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire;
  
  ptr =malloc(sizeof(pthread_t));
  thread__TCPPacketManager= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__TCPPacketManager, attr_t, mainFunc__TCPPacketManager, (void *)channels_array_TCPPacketManager);
  
  struct mwmr_s *channels_array_Application[6];
  channels_array_Application[0]=&TCPIP_receiveTCP__Application_receiveTCP;
  channels_array_Application[1]=&Application_open__TCPIP_open;
  channels_array_Application[2]=&Application_close__TCPIP_close;
  channels_array_Application[3]=&Application_abort__TCPIP_abort;
  channels_array_Application[4]=&Application_sendTCP__TCPIP_send_TCP;
  channels_array_Application[5]=&SmartCardController_start_Application__Application_startApplication;
  
  ptr =malloc(sizeof(pthread_t));
  thread__Application= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__Application, attr_t, mainFunc__Application, (void *)channels_array_Application);
  
  struct mwmr_s *channels_array_SmartCardController[13];
  channels_array_SmartCardController[0]=&SmartCardController_fromTtoP__TCPIP_fromTtoP;
  channels_array_SmartCardController[1]=&SmartCardController_fromPtoT__TCPIP_fromPtoT;
  channels_array_SmartCardController[2]=&SmartCardController_start_TCPIP__TCPIP_start;
  channels_array_SmartCardController[3]=&SmartCardController_reset__InterfaceDevice_reset;
  channels_array_SmartCardController[4]=&SmartCardController_pTS__InterfaceDevice_pTS;
  channels_array_SmartCardController[5]=&SmartCardController_dataReady__InterfaceDevice_data_Ready;
  channels_array_SmartCardController[6]=&SmartCardController_activation__InterfaceDevice_activation;
  channels_array_SmartCardController[7]=&SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC;
  channels_array_SmartCardController[8]=&SmartCardController_answerToReset__InterfaceDevice_answerToReset;
  channels_array_SmartCardController[9]=&SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm;
  channels_array_SmartCardController[10]=&SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD;
  channels_array_SmartCardController[11]=&SmartCardController_data_Ready_SC__InterfaceDevice_dataReady;
  channels_array_SmartCardController[12]=&SmartCardController_start_Application__Application_startApplication;
  
  ptr =malloc(sizeof(pthread_t));
  thread__SmartCardController= (pthread_t)ptr;
  attr_t = malloc(sizeof(pthread_attr_t));
  pthread_attr_affinity(attr_t, 0);  
  
  
  pthread_create(&thread__SmartCardController, attr_t, mainFunc__SmartCardController, (void *)channels_array_SmartCardController);
  
  
  
  pthread_join(thread__InterfaceDevice, NULL);
  pthread_join(thread__SmartCard, NULL);
  pthread_join(thread__TCPIP, NULL);
  pthread_join(thread__TCPPacketManager, NULL);
  pthread_join(thread__Application, NULL);
  pthread_join(thread__SmartCardController, NULL);
  
  
  return 0;
  
}
