package antworld.group;

import java.util.List;

import antworld.ant.Ant;
import antworld.astar.Location;
import antworld.constants.FormationEnum;
import antworld.constants.GroupTypeEnum;

/**
 * This class represents a scout group designed to scout an area.
 * 
 * @author Troy Squillaci, J. Jake Nichol
 */
public class TypeScout extends Group
{
  /** The group type of this group. */
  private static GroupTypeEnum type = GroupTypeEnum.SCOUT;
  
  /** The destination of this group. */
  private Location destination;

  /**
   * Instantiates a new TypeScout Group.
   * 
   * @param id the id of the group to be instantiated
   * @param groupList the list of ants to be assigned to this group
   */
  public TypeScout(int id, List<Ant> groupList)
  {
    super(id, type, groupList);
  }

  @Override
  public void startBehavior(FormationEnum formation, Location target)
  {
    switch (formation)
    {
      case TRAVEL: // Dynamic destination
        walk(destination);
        break;
      case BATTLE:
        // Group should only enter battle if no escape is possible
        // Retreat?
        // retreat();
        // Attack?
        // Find Target
        // Walk to Target
        // Attack Formation
        // Attack Target
        break;
    }
  }
}
