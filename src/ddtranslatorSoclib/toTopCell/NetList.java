/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package ddtranslatorSoclib.toTopCell;
import java.util.*;
import ddtranslatorSoclib.*;

public class NetList {
    public static final String NAME_CLK = "signal_clk";
    public static final String CR = "\n";
    public static final String CR2 = "\n\n";
    private static final String NAME_RST = "signal_resetn";

    public static String getNetlist(String icn) {

		String netlist;

		netlist = CR2 + "//------------------------------Netlist---------------------------------" + CR2;
       
		netlist = netlist + "// icu" + CR2;

		netlist = netlist + "  vcifdtrom.add_property(\"interrupt-parent\", vcifdtrom.get_device_phandle(\"vci_xicu\"));" + CR2;

		netlist = netlist + "  vcixicu.p_clk(signal_clk);" + CR;
		netlist = netlist + "  vcixicu.p_resetn(signal_resetn);" + CR2;		
		netlist = netlist + "  vcixicu.p_vci(signal_vci_xicu);" + CR2;	
       
		netlist = netlist + "  vcifdtrom.begin_device_node(\"vci_rttimer\", \"soclib:vci_rttimer\");" + CR;

		netlist = netlist + "  vcifdtrom.add_property(\"interrupts\", 4);" + CR;
		netlist = netlist + "  vcifdtrom.add_property(\"frequency\", 1000000);" + CR;	
		netlist = netlist + "  vcifdtrom.end_node();" + CR2;

		netlist = netlist + "  vcifdtrom.begin_device_node(\"vci_xicu\", \"soclib:vci_xicu\");" + CR2;
		netlist = netlist + "  int irq_map[cpus.size() * 3];" + CR;
		netlist = netlist + "  for ( size_t i = 0; i < cpus.size(); ++i )" + CR;
		netlist = netlist + "    {" + CR;
		netlist = netlist + "      irq_map[i*3 + 0] = i;" + CR;
		netlist = netlist + "      irq_map[i*3 + 1] = vcifdtrom.get_cpu_phandle(i);" + CR;
		netlist = netlist + "      irq_map[i*3 + 2] = 0;" + CR;
		netlist = netlist + "    }" + CR2;
		netlist = netlist + "  vcifdtrom.add_property(\"interrupt-map\", irq_map, cpus.size() * 3);" + CR;
		netlist = netlist + "  vcifdtrom.add_property(\"frequency\", 1000000);" + CR2;

		netlist = netlist + "  vcifdtrom.add_property(\"param-int-pti-count\", 1);" + CR;
		netlist = netlist + "  vcifdtrom.add_property(\"param-int-hwi-count\", xicu_n_irq);" + CR;
		netlist = netlist + "  vcifdtrom.add_property(\"param-int-wti-count\", cpus.size());" + CR;
		netlist = netlist + "  vcifdtrom.add_property(\"param-int-irq-count\", cpus.size());" + CR;
		netlist = netlist + "  vcifdtrom.end_node();" + CR2;

		netlist = netlist + "  for ( size_t i = 0; i < xicu_n_irq; ++i )" + CR;
		netlist = netlist + "    vcixicu.p_hwi[i](signal_xicu_irq[i]);" + CR2;


		netlist = netlist + "///////////////// cpus" + CR2;

		netlist = netlist + "vcifdtrom.begin_cpus();" + CR2;
		netlist = netlist + "for ( size_t i = 0; i < cpus.size(); ++i ){" + CR;
		netlist = netlist + "   // configure het_rom" + CR;
		netlist = netlist + "  vcihetrom.add_srcid(*cpus[i]->text_ldr, IntTab(i));" + CR;

		netlist = netlist + "  // add cpu node to device tree" + CR;
		netlist = netlist + "  vcifdtrom.begin_cpu_node(std::string(\"cpu:\") + cpus[i]->type, i);" + CR;
		netlist = netlist + "  vcifdtrom.add_property(\"freq\", 1000000);" + CR;

		netlist = netlist + "  vcifdtrom.end_node();" + CR2;

		netlist = netlist + "// connect cpu" + CR;
		netlist = netlist + "  cpus[i]->connect(cpus[i], signal_clk, signal_resetn, signal_vci_m[i]);" + CR;	              if(icn=="vgmn")
	        {
		    netlist = netlist + "vgmn.p_to_initiator[i](signal_vci_m[i]);" + CR;}
		else{
		    netlist = netlist + "vgsb.p_to_initiator[i](signal_vci_m[i]);" + CR;}

		netlist = netlist + "vcixicu.p_irq[i](cpus[i]->irq_sig[0]);" + CR;
		netlist = netlist + " }" + CR;
		netlist = netlist + " vcifdtrom.end_node();" + CR2;

		netlist = netlist + "  vcihetrom.p_clk(signal_clk);" + CR;
		netlist = netlist + "  vcifdtrom.p_clk(signal_clk);" + CR;
		netlist = netlist + "  vcirom.p_clk(signal_clk);" + CR;
		netlist = netlist + "  vcisimhelper.p_clk(signal_clk);" + CR;
		netlist = netlist + "  vcirttimer.p_clk(signal_clk);" + CR;

		netlist = netlist + "  vcihetrom.p_resetn(signal_resetn);" + CR;	
		netlist = netlist + "  vcifdtrom.p_resetn(signal_resetn);" + CR;
		netlist = netlist + "  vcirom.p_resetn(signal_resetn);" + CR;
		netlist = netlist + "  vcisimhelper.p_resetn(signal_resetn);" + CR;
		netlist = netlist + "  vcirttimer.p_resetn(signal_resetn);" + CR;
		netlist = netlist + "  vcihetrom.p_vci(signal_vci_vcihetrom);" + CR;	
		
		netlist = netlist + "  vcifdtrom.p_vci(signal_vci_vcifdtrom);" + CR;
		netlist = netlist + "  vcirom.p_vci(signal_vci_vcirom);" + CR;
		netlist = netlist + "  vcisimhelper.p_vci(signal_vci_vcisimhelper);" + CR;
		netlist = netlist + "  vcirttimer.p_vci(signal_vci_vcirttimer);" + CR;
		netlist = netlist + "  vcirttimer.p_irq[0](signal_xicu_irq[4]);" + CR2;
		if(icn=="vgmn"){
		netlist = netlist + " vgmn.p_clk(signal_clk);" + CR;
		netlist = netlist + "  vgmn.p_resetn(signal_resetn);" + CR;
		netlist = netlist + "  vgmn.p_to_target[0](signal_vci_vcihetrom);" + CR;
		netlist = netlist + "  vgmn.p_to_target[1](signal_vci_vcirom);" + CR;		       
		netlist = netlist + "  vgmn.p_to_target[3](signal_vci_vcisimhelper);" + CR2;
		netlist = netlist + "  vgmn.p_to_target[4](signal_vci_xicu);" + CR;
		netlist = netlist + "  vgmn.p_to_target[5](signal_vci_vcirttimer);" + CR2;
		netlist = netlist + "  vgmn.p_to_target[6](signal_vci_vcifdtrom);" + CR2;	
		netlist = netlist + "  vgmn.p_to_initiator[cpus.size()](signal_vci_bdi);" + CR;
		netlist = netlist + "  vgmn.p_to_initiator[cpus.size()+1](signal_vci_vcifdaccessi);" + CR;
		netlist = netlist + "  vgmn.p_to_initiator[cpus.size()+2](signal_vci_etherneti);" + CR2;

		}
		else{	
		netlist = netlist + " vgsb.p_clk(signal_clk);" + CR;
		netlist = netlist + "  vgsb.p_resetn(signal_resetn);" + CR;
		netlist = netlist + "  vgsb.p_to_target[0](signal_vci_vcihetrom);" + CR;
		netlist = netlist + "  vgsb.p_to_target[1](signal_vci_vcirom);" + CR;		       
		netlist = netlist + "  vgsb.p_to_target[3](signal_vci_vcisimhelper);" + CR2;
		netlist = netlist + "  vgsb.p_to_target[4](signal_vci_xicu);" + CR;
		netlist = netlist + "  vgsb.p_to_target[5](signal_vci_vcirttimer);" + CR2;
		netlist = netlist + "  vgsb.p_to_target[6](signal_vci_vcifdtrom);" + CR2;	
		netlist = netlist + "  vgsb.p_to_initiator[cpus.size()](signal_vci_bdi);" + CR;
		netlist = netlist + "  vgsb.p_to_initiator[cpus.size()+1](signal_vci_vcifdaccessi);" + CR;
		netlist = netlist + "  vgsb.p_to_initiator[cpus.size()+2](signal_vci_etherneti);" + CR2;
		}
		// There are several targets and we have to generate their numbers
		netlist = netlist + "// RAM netlist" + CR2;
		for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) {         
		    netlist = netlist + ram.getMemoryName()+".p_clk(" + NAME_CLK + ");" + CR;
		    netlist = netlist + ram.getMemoryName()+".p_resetn(" + NAME_RST + ");" + CR;
		    netlist = netlist + ram.getMemoryName()+".p_vci(signal_vci_vciram"+ram.getNo_ram()+");" + CR2;

		    if(icn=="vgmn"){
			netlist = netlist + "vgmn.p_to_target["+ram.getNo_target()+"](signal_vci_vciram"+ram.getNo_ram()+");" + CR2;
		    }
		    else{
			netlist = netlist + "vgsb.p_to_target["+ram.getNo_target()+"](signal_vci_vciram"+ram.getNo_ram()+");" + CR2;
		    }		   
		}


		    netlist = netlist + "vci_locks.p_clk(" + NAME_CLK + ");" + CR;
		    netlist = netlist + "vci_locks.p_resetn(" + NAME_RST + ");" + CR;
		    netlist = netlist + "vci_locks.p_vci(signal_vci_vcilocks);" + CR2;
		    //MWMR RAM
		    netlist = netlist +"mwmr_ram.p_clk(" + NAME_CLK + ");" + CR;
		    netlist = netlist +"mwmr_ram.p_resetn(" + NAME_RST + ");" + CR;
		    netlist = netlist +"mwmr_ram.p_vci(signal_vci_mwmr_ram);" + CR2;
		    netlist = netlist + "vgsb.p_to_target["+(TopCellGenerator.avatardd.getNb_target()+4)+"](signal_vci_mwmr_ram);" + CR2;
		    //MWMRd RAM
		    netlist = netlist +"mwmrd_ram.p_clk(" + NAME_CLK + ");" + CR;
		    netlist = netlist +"mwmrd_ram.p_resetn(" + NAME_RST + ");" + CR;
		    netlist = netlist +"mwmrd_ram.p_vci(signal_vci_mwmrd_ram);" + CR2;
		    netlist = netlist + "vgsb.p_to_target["+(TopCellGenerator.avatardd.getNb_target()+5)+"](signal_vci_mwmrd_ram);" + CR2;




		for (AvatarTTY tty : TopCellGenerator.avatardd.getAllTTY()){

		    /* we can have several TTYs and each is associated to the fdtrom, for the moment limit to one (multi-) TTY */
		   if(icn=="vgmn"){
		    netlist = netlist + "vgmn.p_to_target["+(TopCellGenerator.avatardd.getNb_target())+"](signal_vci_vcifdaccesst);" + CR; 
		    netlist = netlist + "vgmn.p_to_target["+(TopCellGenerator.avatardd.getNb_target()+1)+"](signal_vci_ethernett);" + CR;	
		    netlist = netlist + "vgmn.p_to_target["+(TopCellGenerator.avatardd.getNb_target()+2)+"](signal_vci_bdt);" + CR;	
		   }else{ 
		    netlist = netlist + "vgsb.p_to_target["+(TopCellGenerator.avatardd.getNb_target())+"](signal_vci_vcifdaccesst);" + CR; 
		    netlist = netlist + "vgsb.p_to_target["+(TopCellGenerator.avatardd.getNb_target()+1)+"](signal_vci_ethernett);" + CR;	
		    netlist = netlist + "vgsb.p_to_target["+(TopCellGenerator.avatardd.getNb_target()+2)+"](signal_vci_bdt);" + CR;	
                    netlist = netlist + "vgsb.p_to_target["+(TopCellGenerator.avatardd.getNb_target()+3)+"](signal_vci_vcilocks);" + CR;	
		   }
		    netlist = netlist + "vcifdtrom.begin_device_node(\"vci_multi_tty\",\"soclib:vci_multi_tty\");" + CR2;

		    netlist = netlist + "vcifdtrom.add_property(\"interrupts\", 0);" + CR2;
		    netlist = netlist + "vcifdtrom.end_node();;" + CR2;


		    netlist = netlist + "// TTY netlist" + CR2;
		    netlist = netlist + tty.getTTYName()+".p_clk(signal_clk);" + CR;
		    netlist = netlist + tty.getTTYName()+".p_resetn(signal_resetn);" + CR;
		    netlist = netlist + tty.getTTYName()+".p_vci(signal_vci_tty"+tty.getNo_tty()+");" + CR2;
		    if(icn=="vgmn"){
			netlist = netlist + "vgmn.p_to_target["+tty.getNo_target()+"](signal_vci_tty"+tty.getNo_tty()+");" + CR2;
		    }else{
			netlist = netlist + "vgsb.p_to_target["+tty.getNo_target()+"](signal_vci_tty"+tty.getNo_tty()+");" + CR2;
		    }
		    netlist = netlist + tty.getTTYName()+".p_irq[0](signal_xicu_irq[0]);" + CR2;		      
		}	       
		netlist = netlist + "{" + CR2;
		netlist = netlist + "  vcifdtrom.begin_node(\"aliases\");" + CR;
		netlist = netlist + "  vcifdtrom.add_property(\"timer\", vcifdtrom.get_device_name(\"vci_rttimer\") + \"[0]\");" + CR;
		netlist = netlist + "  vcifdtrom.add_property(\"console\", vcifdtrom.get_device_name(\"vci_multi_tty\") + \"[0]\");" + CR;
		netlist = netlist + "  vcifdtrom.end_node();" + CR;
		netlist = netlist + "}" + CR2;

//////////////// ethernet

 	netlist = netlist + "vcieth.p_clk(signal_clk);" + CR;
  	netlist = netlist + "vcieth.p_resetn(signal_resetn);" + CR;
  	netlist = netlist + "vcieth.p_irq(signal_xicu_irq[3]);" + CR;
  	netlist = netlist + "vcieth.p_vci_target(signal_vci_ethernett);" + CR;
  	netlist = netlist + "vcieth.p_vci_initiator(signal_vci_etherneti);" + CR;

  	netlist = netlist + "vcifdtrom.begin_device_node(\"vci_ethernet\", \"soclib:vci_ethernet\");" + CR;
  	netlist = netlist + "vcifdtrom.add_property(\"interrupts\", 3);" + CR;
  	netlist = netlist + "vcifdtrom.end_node();" + CR;

//////////////// block device

 	netlist = netlist + "vcibd.p_clk(signal_clk);" + CR;
  	netlist = netlist + "vcibd.p_resetn(signal_resetn);" + CR;
  	netlist = netlist + "vcibd.p_irq(signal_xicu_irq[1]);" + CR;
  	netlist = netlist + "vcibd.p_vci_target(signal_vci_bdt);" + CR;
  	netlist = netlist + "vcibd.p_vci_initiator(signal_vci_bdi);" + CR;

  	netlist = netlist + "vcifdtrom.begin_device_node(\"vci_block_device\", \"soclib:vci_block_device\");" + CR;
  	netlist = netlist + "vcifdtrom.add_property(\"interrupts\", 1);" + CR;
  	netlist = netlist + "vcifdtrom.end_node();" + CR;

 //////////////// fd access
 	netlist = netlist + "vcihetrom.add_srcid(*cpus[0]->text_ldr, IntTab(cpus.size()+1));" + CR; /* allows dma read in rodata */

  	netlist = netlist + "vcifd.p_clk(signal_clk);" + CR;
  	netlist = netlist + "vcifd.p_resetn(signal_resetn);" + CR;
  	netlist = netlist + "vcifd.p_irq(signal_xicu_irq[2]);" + CR;
  	netlist = netlist + "vcifd.p_vci_target(signal_vci_vcifdaccesst);" + CR;
  	netlist = netlist + "vcifd.p_vci_initiator(signal_vci_vcifdaccessi);" + CR;

  	netlist = netlist + "vcifdtrom.begin_device_node(\"vci_fd_access\", \"soclib:vci_fd_access\");" + CR;
  	netlist = netlist + "vcifdtrom.add_property(\"interrupts\", 2);" + CR;
  	netlist = netlist + "vcifdtrom.end_node();" + CR2;

		netlist = netlist + "  sc_core::sc_start(sc_core::sc_time(0, sc_core::SC_NS));" + CR;
		netlist = netlist + "  signal_resetn = false;" + CR;
		netlist = netlist + "  sc_core::sc_start(sc_core::sc_time(1, sc_core::SC_NS));" + CR;
		netlist = netlist + "  signal_resetn = true;" + CR;
		netlist = netlist + "  sc_core::sc_start();" + CR;
		netlist = netlist + CR + "  return EXIT_SUCCESS;"+ CR;
		netlist = netlist +"}" + CR;
		return netlist;
	}
}