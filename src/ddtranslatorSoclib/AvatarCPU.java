/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */

package ddtranslatorSoclib;
import java.util.*;

public class AvatarCPU extends AvatarComponent{

    private String cpuName;

    private int nbOfIRQs;

    private int iCacheWays;
    private int iCacheSets;
    private int iCacheWords;

    private int dCacheWays;
    private int dCacheSets;
    private int dCacheWords;
    private int nb_init;
    private int no_proc;
   

    private LinkedList<AvatarTask> tasksMapped;

    public AvatarCPU(String _cpuName, int _nbOfIRQs,int _ICacheWays, int _ICacheSets, int _ICacheWords, int  _DCacheWays, int _DCacheSets, int _DCacheWords , int _nb_init , int _no_proc ){
      
      cpuName = _cpuName;
      nbOfIRQs = _nbOfIRQs;
      iCacheWays = _ICacheWays;
      iCacheSets = _ICacheSets;
      iCacheWords = _ICacheWords;
      dCacheWays = _DCacheWays;
      dCacheSets = _DCacheSets;
      dCacheWords = _DCacheWords;
      nb_init = _nb_init;
      no_proc = _no_proc;
      tasksMapped = new LinkedList<AvatarTask>();
    }

    public int getNb_init(){
      return  nb_init;
    }

    public int getNo_proc(){
      return  no_proc;
    }

    public	String getCpuName(){
	return cpuName;
	}

    public	int getNbOfIRQS(){
      return  nbOfIRQs;
	}

	public int getICacheWays(){
	return iCacheWays;
	}

	public int getICacheSets(){
	return  iCacheSets;
	}	

	public int getICacheWords(){
	return iCacheWords;
	}

	public int getDCacheWays(){
	return dCacheWays;
	}

	public int getDCacheSets(){
	return dCacheSets;
	}

	public int getDCacheWords(){
	return dCacheWords;
	}

    public LinkedList<AvatarTask> getAllTasks(){
      return tasksMapped;
    }

    public void addTask(AvatarTask task){
      tasksMapped.add(task);
    }

}
