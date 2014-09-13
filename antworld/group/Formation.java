package antworld.group;

import antworld.ant.Ant;
import antworld.astar.Location;

public class Formation
{
  private Group group;

  public Formation(Group group)
  {
    this.group = group;
  }
  
  // Entire group moves at the speed of the slowest ant.
  public void keepFormationIntact()
  {
    
  }

  // TODO: Methods for each type and formation
  
  public void singleFileFive()
  {
    Ant leader = group.getLeader();
    Ant point = group.getFormationPoint();
    Ant rear = group.getFormationRear();
    Location leaderLoc = new Location(leader.antData.gridX, leader.antData.gridY);
    
  }
}
