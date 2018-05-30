#include "TCPPacketManager.h"


// Header code defined in the model

// End of header code defined in the model

static uint32_t _empty;
static uint32_t _addPacket;
static uint32_t _ackPacket;
static uint32_t _timeoutPacket;
static uint32_t _retrieve;
static uint32_t _storePacket;
static uint32_t _set__timerP;
static uint32_t _reset__timerP;
static uint32_t _expire__timerP;

#define STATE__START__STATE 0
#define STATE__choice__0 1
#define STATE__ackPacketLoop 2
#define STATE__choice__1 3
#define STATE__PacketExpirationLoop 4
#define STATE__Main 5
#define STATE__RemovingPacketsLoop 6
#define STATE__STOP__STATE 7

void *mainFunc__TCPPacketManager(struct mwmr_s *channels_TCPPacketManager[]){
  
  struct mwmr_s *TCPPacketManager_storePacket__TCPPacketManager_retrieve= channels_TCPPacketManager[0];
  struct mwmr_s *TCPIP_ackPacket__TCPPacketManager_ackPacket= channels_TCPPacketManager[1];
  struct mwmr_s *TCPIP_addPacket__TCPPacketManager_addPacket= channels_TCPPacketManager[2];
  struct mwmr_s *TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket= channels_TCPPacketManager[3];
  struct mwmr_s *TCPIP_emptyListOfPackets__TCPPacketManager_empty= channels_TCPPacketManager[4];
  struct mwmr_s *TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire= channels_TCPPacketManager[5];
  int nbOfPackets = 0;
  int packet__srcdest = 0;
  int packet__seqNum = 0;
  int packet__ackNum = 0;
  int packet__control = 0;
  int packet__management = 0;
  int packet__checksum = 0;
  int packet__othersAndPadding = 0;
  int packet__data = 0;
  int cpt = 0;
  bool timerSet = false;
  int tmp = 0;
  int packetA__srcdest = 0;
  int packetA__seqNum = 0;
  int packetA__ackNum = 0;
  int packetA__control = 0;
  int packetA__management = 0;
  int packetA__checksum = 0;
  int packetA__othersAndPadding = 0;
  int packetA__data = 0;
  int elapsedTime = 0;
  int __timerValue = 0;
  
  int __currentState = STATE__START__STATE;
  __attribute__((unused)) request __req0;
  __attribute__((unused))int *__params0[8];
  __attribute__((unused)) request __req1;
  __attribute__((unused))int *__params1[8];
  __attribute__((unused)) request __req2;
  __attribute__((unused))int *__params2[8];
  __attribute__((unused)) request __req3;
  __attribute__((unused))int *__params3[8];
  __attribute__((unused))setOfRequests __list;
  __attribute__((unused))pthread_cond_t __myCond;
  __attribute__((unused))request *__returnRequest;
  
  char * __myname = "TCPPacketManager";
  
  pthread_cond_init(&__myCond, NULL);
  
  fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);
  //printf("my name = %s\n", __myname);
  
  /* Main loop on states */
  while(__currentState != STATE__STOP__STATE) {
    switch(__currentState) {
      case STATE__START__STATE: 
      debug2Msg(__myname, "-> (=====) Entering state + Main");
      __currentState = STATE__Main;
      break;
      
      case STATE__choice__0: 
      if (!(packet__seqNum != packetA__seqNum)) {
        makeNewRequest(&__req0, 405, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (packet__seqNum != packetA__seqNum) {
        __params1[0] = &packet__srcdest;
        __params1[1] = &packet__seqNum;
        __params1[2] = &packet__ackNum;
        __params1[3] = &packet__control;
        __params1[4] = &packet__management;
        __params1[5] = &packet__checksum;
        __params1[6] = &packet__othersAndPadding;
        __params1[7] = &packet__data;
        debug2Msg(__myname, "-> (=====)before MakeNewRequest");
        debugInt("channel address", &__req1);
        makeNewRequest(&__req1, 387, SEND_ASYNC_REQUEST, 0, 0, 0, 8, __params1);
        debug2Msg(__myname, "-> (=====)after MakeNewRequest");
        __req1.asyncChannel = &__TCPPacketManager_storePacket__TCPPacketManager_retrieve;
        addRequestToList(&__list, &__req1);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        nbOfPackets = nbOfPackets-1;
        cpt = nbOfPackets;
        debug2Msg(__myname, "-> (=====) Entering state + ackPacketLoop");
        __currentState = STATE__ackPacketLoop;
        
      }
      else  if (__returnRequest == &__req1) {
        debug2Msg(__myname, "-> (=====) Entering state + ackPacketLoop");
        __currentState = STATE__ackPacketLoop;
        
      }
      break;
      
      case STATE__ackPacketLoop: 
      if (cpt == nbOfPackets) {
        makeNewRequest(&__req0, 414, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      makeNewRequest(&__req1, 492, IMMEDIATE, 0, 0, 0, 0, __params1);
      addRequestToList(&__list, &__req1);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        debug2Msg(__myname, "-> (=====) Entering state + Main");
        __currentState = STATE__Main;
        
      }
      else  if (__returnRequest == &__req1) {
        cpt = cpt+1;
        __params0[0] = &packet__srcdest;
        __params0[1] = &packet__seqNum;
        __params0[2] = &packet__ackNum;
        __params0[3] = &packet__control;
        __params0[4] = &packet__management;
        __params0[5] = &packet__checksum;
        __params0[6] = &packet__othersAndPadding;
        __params0[7] = &packet__data;
        makeNewRequest(&__req0, 389, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 8, __params0);
        __req0.asyncChannel = &__TCPPacketManager_storePacket__TCPPacketManager_retrieve;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        debug2Msg(__myname, "-> (=====) Entering state + choice__0");
        __currentState = STATE__choice__0;
        
      }
      break;
      
      case STATE__choice__1: 
      if (packet__management<elapsedTime) {
        makeNewRequest(&__req0, 421, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (!(packet__management<elapsedTime)) {
        __params1[0] = &packet__srcdest;
        __params1[1] = &packet__seqNum;
        __params1[2] = &packet__ackNum;
        __params1[3] = &packet__control;
        __params1[4] = &packet__management;
        __params1[5] = &packet__checksum;
        __params1[6] = &packet__othersAndPadding;
        __params1[7] = &packet__data;
        debug2Msg(__myname, "-> (=====) test TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket");
        makeNewRequest(&__req1, 394, SEND_SYNC_REQUEST, 0, 0, 0, 8, __params1);
        __req1.syncChannel = &__TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket;
        addRequestToList(&__list, &__req1);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        tmp = tmp+1;
        __params0[0] = &packet__srcdest;
        __params0[1] = &packet__seqNum;
        __params0[2] = &packet__ackNum;
        __params0[3] = &packet__control;
        __params0[4] = &packet__management;
        __params0[5] = &packet__checksum;
        __params0[6] = &packet__othersAndPadding;
        __params0[7] = &packet__data;
        debug2Msg(__myname, "-> (=====)before MakeNewRequest");
        debugInt("channel address", &__req0);
        makeNewRequest(&__req0, 395, SEND_ASYNC_REQUEST, 0, 0, 0, 8, __params0);
        debug2Msg(__myname, "-> (=====)after MakeNewRequest");
        __req0.asyncChannel = &__TCPPacketManager_storePacket__TCPPacketManager_retrieve;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        debug2Msg(__myname, "-> (=====) Entering state + PacketExpirationLoop");
        __currentState = STATE__PacketExpirationLoop;
        
      }
      else  if (__returnRequest == &__req1) {
        debug2Msg(__myname, "-> (=====) Entering state + PacketExpirationLoop");
        __currentState = STATE__PacketExpirationLoop;
        
      }
      break;
      
      case STATE__PacketExpirationLoop: 
      if (cpt == nbOfPackets) {
        makeNewRequest(&__req0, 429, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (cpt<nbOfPackets) {
        makeNewRequest(&__req1, 470, IMMEDIATE, 0, 0, 0, 0, __params1);
        addRequestToList(&__list, &__req1);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        nbOfPackets = tmp;
        debug2Msg(__myname, "-> (=====) Entering state + Main");
        __currentState = STATE__Main;
        
      }
      else  if (__returnRequest == &__req1) {
        cpt = cpt+1;
        __params0[0] = &packet__srcdest;
        __params0[1] = &packet__seqNum;
        __params0[2] = &packet__ackNum;
        __params0[3] = &packet__control;
        __params0[4] = &packet__management;
        __params0[5] = &packet__checksum;
        __params0[6] = &packet__othersAndPadding;
        __params0[7] = &packet__data;
        makeNewRequest(&__req0, 397, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 8, __params0);
        __req0.asyncChannel = &__TCPPacketManager_storePacket__TCPPacketManager_retrieve;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        debug2Msg(__myname, "-> (=====) Entering state + choice__1");
        __currentState = STATE__choice__1;
        
      }
      break;
      
      case STATE__Main: 
      __params0[0] = &packetA__srcdest;
      __params0[1] = &packetA__seqNum;
      __params0[2] = &packetA__ackNum;
      __params0[3] = &packetA__control;
      __params0[4] = &packetA__management;
      __params0[5] = &packetA__checksum;
      __params0[6] = &packetA__othersAndPadding;
      __params0[7] = &packetA__data;
      debug2Msg(__myname, "-> (=====) test TCPIP_ackPacket__TCPPacketManager_ackPacket");
      makeNewRequest(&__req0, 391, RECEIVE_SYNC_REQUEST, 0, 0, 0, 8, __params0);
      __req0.syncChannel = &__TCPIP_ackPacket__TCPPacketManager_ackPacket;
      addRequestToList(&__list, &__req0);
      debug2Msg(__myname, "-> (=====) test TCPIP_emptyListOfPackets__TCPPacketManager_empty");
      makeNewRequest(&__req1, 403, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params1);
      __req1.syncChannel = &__TCPIP_emptyListOfPackets__TCPPacketManager_empty;
      addRequestToList(&__list, &__req1);
      debug2Msg(__myname, "-> (=====) test TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire");
      makeNewRequest(&__req2, 633, RECEIVE_SYNC_REQUEST, 0, 0, 0, 0, __params2);
      __req2.syncChannel = &__TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire;
      addRequestToList(&__list, &__req2);
      __params3[0] = &packet__srcdest;
      __params3[1] = &packet__seqNum;
      __params3[2] = &packet__ackNum;
      __params3[3] = &packet__control;
      __params3[4] = &packet__management;
      __params3[5] = &packet__checksum;
      __params3[6] = &packet__othersAndPadding;
      __params3[7] = &packet__data;
      debug2Msg(__myname, "-> (=====) test TCPIP_addPacket__TCPPacketManager_addPacket");
      makeNewRequest(&__req3, 393, RECEIVE_SYNC_REQUEST, 0, 0, 0, 8, __params3);
      __req3.syncChannel = &__TCPIP_addPacket__TCPPacketManager_addPacket;
      addRequestToList(&__list, &__req3);
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        cpt = 0;
        debug2Msg(__myname, "-> (=====) Entering state + ackPacketLoop");
        __currentState = STATE__ackPacketLoop;
        
      }
      else  if (__returnRequest == &__req1) {
        cpt = 0;
        debug2Msg(__myname, "-> (=====) Entering state + RemovingPacketsLoop");
        __currentState = STATE__RemovingPacketsLoop;
        
      }
      else  if (__returnRequest == &__req2) {
        cpt = 0;
        tmp = 0;
        elapsedTime = elapsedTime+2;
        debug2Msg(__myname, "-> (=====) Entering state + PacketExpirationLoop");
        __currentState = STATE__PacketExpirationLoop;
        
      }
      else  if (__returnRequest == &__req3) {
        packet__management = elapsedTime;
        nbOfPackets = nbOfPackets+1;
        __params0[0] = &packet__srcdest;
        __params0[1] = &packet__seqNum;
        __params0[2] = &packet__ackNum;
        __params0[3] = &packet__control;
        __params0[4] = &packet__management;
        __params0[5] = &packet__checksum;
        __params0[6] = &packet__othersAndPadding;
        __params0[7] = &packet__data;
        debug2Msg(__myname, "-> (=====)before MakeNewRequest");
        debugInt("channel address", &__req0);
        makeNewRequest(&__req0, 392, SEND_ASYNC_REQUEST, 0, 0, 0, 8, __params0);
        debug2Msg(__myname, "-> (=====)after MakeNewRequest");
        __req0.asyncChannel = &__TCPPacketManager_storePacket__TCPPacketManager_retrieve;
        debug2Msg(__myname, "-> (=====)before executeOneRequest");
        __returnRequest = executeOneRequest(&__list, &__req0);
        debug2Msg(__myname, "-> (=====)after executeOneRequest");
        clearListOfRequests(&__list);
        debug2Msg(__myname, "-> (=====) Entering state + Main");
        __currentState = STATE__Main;
        
      }
      break;
      
      case STATE__RemovingPacketsLoop: 
      if ((cpt == nbOfPackets)) {
        makeNewRequest(&__req0, 440, IMMEDIATE, 0, 0, 0, 0, __params0);
        addRequestToList(&__list, &__req0);
      }
      if (cpt<nbOfPackets) {
        __params1[0] = &packet__srcdest;
        __params1[1] = &packet__seqNum;
        __params1[2] = &packet__ackNum;
        __params1[3] = &packet__control;
        __params1[4] = &packet__management;
        __params1[5] = &packet__checksum;
        __params1[6] = &packet__othersAndPadding;
        __params1[7] = &packet__data;
        makeNewRequest(&__req1, 400, RECEIVE_ASYNC_REQUEST, 0, 0, 0, 8, __params1);
        __req1.asyncChannel = &__TCPPacketManager_storePacket__TCPPacketManager_retrieve;
        addRequestToList(&__list, &__req1);
      }
      if (nbOfRequests(&__list) == 0) {
        debug2Msg(__myname, "No possible request");
        __currentState = STATE__STOP__STATE;
        break;
      }
      __returnRequest = executeListOfRequests(&__list);
      clearListOfRequests(&__list);
       if (__returnRequest == &__req0) {
        nbOfPackets = 0;
        elapsedTime = 0;
        debug2Msg(__myname, "-> (=====) Entering state + Main");
        __currentState = STATE__Main;
        
      }
      else  if (__returnRequest == &__req1) {
        cpt = cpt+1;
        debug2Msg(__myname, "-> (=====) Entering state + RemovingPacketsLoop");
        __currentState = STATE__RemovingPacketsLoop;
        
      }
      break;
      
    }
  }
  //printf("Exiting = %s\n", __myname);
  return NULL;
}

