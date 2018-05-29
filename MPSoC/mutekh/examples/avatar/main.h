#ifndef MAIN_H
#define MAIN_H
extern pthread_mutex_t __mainMutex;

/* Synchronous channels */
/* Asynchronous channels */
extern asyncchannel __InputEngine_packet__Classification_from_IE;
extern asyncchannel __Bootstrap_address__InputEngine_bootstrap;
extern asyncchannel __OutputEngine_address__InputEngine_address;
extern asyncchannel __Scheduling_packet__OutputEngine_packet;
extern asyncchannel __Classif2_from_classif__Classification_to_c2;
extern asyncchannel __Classif2_to_queue_low__Classification_c2_to_queue_low;
extern asyncchannel __Classif2_to_queue_medium__Classification_c2_to_queue_medium;
extern asyncchannel __Classif2_to_queue_high__Classification_c2_to_queue_high;
extern asyncchannel __Classif0_from_classif__Classification_to_c0;
extern asyncchannel __Classif0_to_queue_low__Classification_c0_to_queue_low;
extern asyncchannel __Classif0_to_queue_medium__Classification_c0_to_queue_medium;
extern asyncchannel __Classif0_to_queue_high__Classification_c0_to_queue_high;
extern asyncchannel __Classif1_from_classif__Classification_to_c1;
extern asyncchannel __Classif1_to_queue_low__Classification_c1_to_queue_low;
extern asyncchannel __Classif1_to_queue_medium__Classification_c1_to_queue_medium;
extern asyncchannel __Classif1_to_queue_high__Classification_c1_to_queue_high;
extern asyncchannel __Classification_queue_low__Scheduling_from_queue_low;
extern asyncchannel __Classification_queue_medium__Scheduling_from_queue_medium;
extern asyncchannel __Classification_queue_high__Scheduling_from_queue_high;
extern asyncchannel __Sched0_toScheduler0__Scheduling_to_scheduler0;
extern asyncchannel __Sched0_scheduledPacket0__Scheduling_scheduledPacket0;
extern asyncchannel __Sched1_toScheduler1__Scheduling_to_scheduler1;
extern asyncchannel __Sched1_scheduledPacket1__Scheduling_scheduledPacket1;
#endif
