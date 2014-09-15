package antworld.info;

import java.util.HashMap;

import antworld.ant.Ant;
import antworld.client.Client;
import antworld.group.Group;

public class GroupManager 
{
  private HashMap<Integer, Group> groups = new HashMap<Integer, Group>();
  private static final int GROUP_SIZE = 3;
  
  public GroupManager()
  {
	int i = 0;
    for (Ant value : Client.getActiveAntManager().getAllMyAnts().values())
    {
    	if (i < GroupManager.GROUP_SIZE)
    	{
    		
    	}
    	else
    	{
    		
    	}
    }
  }
}
