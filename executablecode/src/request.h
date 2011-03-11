#ifndef REQUEST_H
#define REQUEST_H

#define SEND_SYNC_REQUEST 0
#define RECEIVE_SYNC_REQUEST 2
#define SEND_ASYNC_REQUEST 4
#define RECEIVE_ASYNC_REQUEST 6
#define DELAY 8

struct request;

struct setOfRequests {
  struct request *head;
};

typedef struct setOfRequests setOfRequests;

struct request {
  struct request *next;
  struct setOfRequests* listOfRequests;
  int type;
  int hasDelay;
  long delay;
  int delayElapsed;
  int selected;
  int nbOfParams;
  int *params[];
};

typedef struct request request;

request *getNewRequest(int type, int hasDelay, long delay, int nbOfParams, int *params[]);
void destroyRequest(request *req);
extern int isRequestSelected(request *req);

#endif
