package antworld.group;

//import antworld.ant.Ant;
//import antworld.astar.Location;

/**
 * This class represents an ant group formation.
 * 
 * @author Troy Squillaci, J. Jake Nichol
 */
public class Formation
{
  /**
   * Instantiates a new Formation object.
   * 
   * @param group the group this formation is tied to
   */
  public Formation(Group group)
  {
    this.setGroup(group);
  }

  /** The group object this formation is tied to. */
  private Group group;

  // TODO Entire group moves at the speed of the slowest ant.
  // public void keepFormationIntact()
  // {
  //
  // }

  // TODO: Methods for each type and formation
  // public void singleFileFive()
  // {
  // Ant leader = group.getLeader();
  // Ant point = group.getFormationPoint();
  // Ant rear = group.getFormationRear();
  // Location leaderLoc = new Location(leader.getAntData().gridX, leader.getAntData().gridY);
  //
  // }

  /**
   * Gets the group object this Formation is tied to.
   * 
   * @return returns the group object associated with this Formation
   */
  public Group getGroup()
  {
    return group;
  }

  /**
   * Sets the group object to be associated with this Formation.
   * 
   * @param group the group object to be associated with this Formation
   */
  public void setGroup(Group group)
  {
    this.group = group;
  }
}
