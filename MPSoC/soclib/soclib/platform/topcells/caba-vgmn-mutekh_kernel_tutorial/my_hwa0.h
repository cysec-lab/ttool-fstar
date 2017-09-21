#ifndef _ADDER_COPRO_H
#define _ADDER_COPRO_H

#include <systemc>

#include "fifo_virtual_copro_wrapper.h"

namespace dsx { namespace caba {

class MyHWA0
    : public dsx::caba::FifoVirtualCoprocessorWrapper
{

    public:
    ~MyHWA0();
    MyHWA0(sc_core::sc_module_name insname);


    private:
    void * task_func(); // Task code

};

}}
#endif /* _ADDER_COPRO_H */