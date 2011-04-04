#include <stdlib.h>
#include <pthread.h>
#include <time.h>

#include "request_manager.h"
#include "request.h"
#include "myerrors.h"
#include "debug.h"



void executeSendSyncTransaction(request *req) {
  int cpt;
  request *selectedReq;

  // At least one transaction available -> must select one randomly
  // First: count how many of them are available
  // Then, select one
  // Broadcast the new condition!

  cpt = 0;
  request* currentReq = req->syncChannel->inWaitQueue;
  debugMsg("Execute send sync tr");

  while(currentReq != NULL) {
    cpt ++;
    currentReq = currentReq->next;
  }

  cpt = random() % cpt;

  // Head of the list?
  selectedReq = req->syncChannel->inWaitQueue;
  while (cpt > 0) {
    selectedReq = selectedReq->next;
    cpt --;
  } 

  req->syncChannel->inWaitQueue = removeRequestFromList(req->syncChannel->inWaitQueue, selectedReq);

  // Select the selected request, and notify the information
  selectedReq->selected = 1;

  // Handle parameters
  copyParameters(req, selectedReq);

  debugMsg("Signaling");
  pthread_cond_signal(selectedReq->listOfRequests->wakeupCondition);
}

void executeReceiveSyncTransaction(request *req) {
  int cpt;
  request *selectedReq;
  
  // At least one transaction available -> must select one randomly
  // First: count how many of them are available
  // Then, select one
  // Broadcast the new condition!

  request* currentReq = req->syncChannel->outWaitQueue;
  cpt = 0;
  debugMsg("Execute receive sync tr");

  while(currentReq != NULL) {
    cpt ++;
    currentReq = currentReq->next;
  }
  cpt = random() % cpt;
  selectedReq = req->syncChannel->outWaitQueue;
  while (cpt > 0) {
    selectedReq = selectedReq->next;
    cpt --;
  } 

  req->syncChannel->outWaitQueue = removeRequestFromList(req->syncChannel->outWaitQueue, selectedReq);

  // Select the request, and notify the information in the channel
  selectedReq->selected = 1;

  // Handle parameters
  copyParameters(selectedReq, req);

  debugMsg("Signaling");
  pthread_cond_signal(selectedReq->listOfRequests->wakeupCondition);
}


void executeSendSyncRequest(request *req, syncchannel *channel) {
  /*debugMsg("Locking mutex");

  if(channel == NULL) {
    debugMsg("NULL channel");
    exit(-1);
  }

  if(channel->mutex == NULL) {
    debugMsg("NULL mutex");
    exit(-1);
  }

  pthread_mutex_lock(channel->mutex);

  debugMsg("Execute");
  executeSendSyncTransaction(req, channel);

  while (isRequestSelected(req) == 0) {
    debugMsg("Stuck waiting for a receive request");
    pthread_cond_wait(channel->sendCondition, channel->mutex);
    debugMsg("Woke up from waiting for a receive request");
  }

  pthread_mutex_unlock(channel->mutex);
  debugMsg("Mutex unlocked");*/

}

void executeReceiveSyncRequest(request *req, syncchannel *channel) {
  /*debugMsg("Locking mutex");

  pthread_mutex_lock(channel->mutex);

  debugMsg("Execute");
  executeReceiveSyncTransaction(req, channel);

  while (isRequestSelected(req) == 0) {
    debugMsg("Stuck waiting for a send request");
    pthread_cond_wait(req->listOfRequests->wakeupCondition, channel->mutex);
    debugMsg("Woke up from waiting for a send request");
  }

  pthread_mutex_unlock(channel->mutex);
  debugMsg("Mutex unlocked");*/
}


int executable(setOfRequests *list, int nb) {
  int cpt = 0;
  int index = 0;
  request *req = list->head;

  debugMsg("Starting loop");

  while((req != NULL) && (cpt < nb)) {
    req->executable = 0;
    if (req->type == SEND_SYNC_REQUEST) {
      debugMsg("Send sync");
      if (req->syncChannel->inWaitQueue != NULL) {
	req->executable = 1;
	cpt ++;
      } 
      index ++;
    }

    if (req->type == RECEIVE_SYNC_REQUEST) {
      debugMsg("receive sync");
      if (req->syncChannel->outWaitQueue != NULL) {
	req->executable = 1;
	cpt ++;
      }
      index ++;
    }

    if (req->type == IMMEDIATE) {
      req->executable = 1;
      cpt ++;
    }

    req = req->nextRequestInList;
    
  }

  return cpt;
}

void private__makeRequestPending(setOfRequests *list) {
  request *req = list->head;
  while(req != NULL) {
    if (req->type == SEND_SYNC_REQUEST) {
      req->syncChannel->outWaitQueue = addToRequestQueue(req->syncChannel->outWaitQueue, req);
    }
    if (req->type ==  RECEIVE_SYNC_REQUEST) {
      req->syncChannel->inWaitQueue = addToRequestQueue(req->syncChannel->inWaitQueue, req);
    }

    req = req->nextRequestInList;
  }
}

void private__makeRequest(request *req) {
  if (req->type == SEND_SYNC_REQUEST) {
    executeSendSyncTransaction(req);
  }

  if (req->type == RECEIVE_SYNC_REQUEST) {
    executeReceiveSyncTransaction(req);
  }

  // IMMEDIATE: Nothing to do
}


request *private__executeRequests0(setOfRequests *list, int nb) {
  int howMany, found;
  int selectedIndex, realIndex;
  request *selectedReq;
  request *req;
  
  // Compute which requests can be executed
  debugMsg("counting requests");
  howMany = executable(list, nb);

  debugInt("Counting requests=", howMany);

  if (howMany == 0) {
    debugMsg("No pending requests");
    // Must make them pending
    
    private__makeRequestPending(list);

    return NULL;
  }
  
  debugInt("At least one pending request", howMany);

  
  // Select a request
  req = list->head;
  selectedIndex = (rand() % howMany)+1;
  debugInt("selectedIndex=", selectedIndex);
  realIndex = 0;
  found = 0;
  while(req != NULL) {
    if (req->executable == 1) {
      found ++;
      if (found == selectedIndex) {
	break;
      }
    }
    realIndex ++;
    req = req->nextRequestInList;
  }

  debugInt("Getting request at index", realIndex);
  selectedReq = getRequestAtIndex(list, realIndex);

  debugInt("Selected request of type", selectedReq->type);

  // Execute that request
  private__makeRequest(selectedReq);

  return selectedReq;  
}


request *private__executeRequests(setOfRequests *list) {
  // Is a request already selected?
  request *req;

  req = list->head;

  while(req != NULL) {
    if (req->selected == 1) {
      return req;
    }
    req = req->nextRequestInList;
  }


  debugMsg("No request selected -> looking for one!");

  return private__executeRequests0(list, nbOfRequests(list));
}




request *executeOneRequest(setOfRequests *list, request *req) {
  req->nextRequestInList = NULL;
  req->listOfRequests = list;
  list->head = req;
  return executeListOfRequests(list);
}


// Return the executed request
request *executeListOfRequests(setOfRequests *list) {
  request *req;

  clock_gettime(CLOCK_REALTIME, &list->startTime);
  
  // Try to find a request that could be executed
  pthread_mutex_lock(list->mutex);

  debugMsg("Going to execute request");

  while((req = private__executeRequests(list)) == NULL) {
    debugMsg("Waiting for request!");
    pthread_cond_wait(list->wakeupCondition, list->mutex);
    debugMsg("Waking up for requests!");
  }

  debugMsg("Request selected!");

  clock_gettime(CLOCK_REALTIME, &list->completionTime);

  pthread_mutex_unlock(list->mutex);  
  return req;
}

