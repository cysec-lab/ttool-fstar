#ifndef ASYNCCHANNEL_H
#define ASYNCCHANNEL_H

struct asyncchannel;

#include "message.h"
#include "request.h"


struct asyncchannel {
  char *outname;
  char *inname;
  int isBlocking; // In writing. Reading is always blocking
  int maxNbOfMessages; //
  struct request* outWaitQueue;
  struct request* inWaitQueue;
  setOfMessages *pendingMessages;
};

typedef struct asyncchannel asyncchannel;

asyncchannel *getNewAsyncchannel(char *inname, char *outname, int isBlocking, int maxNbOfMessages);
void destroyAsyncchannel(asyncchannel *syncch);


#endif
