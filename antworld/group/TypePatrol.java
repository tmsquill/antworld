package antworld.group;

import java.util.List;

import antworld.ant.Ant;
import antworld.astar.Location;
import antworld.constants.FormationEnum;
import antworld.constants.GroupTypeEnum;

/**
 * This class represents a Patrol group designed patrol around an area.
 * 
 * @author Troy Squillaci, J. Jake Nichol
 */
public class TypePatrol extends Group
{
  /** The group type of this group. */
  private static GroupTypeEnum type = GroupTypeEnum.PATROL;
  
  /** The destination of this group. */
  private Location destination;

  /**
   * Instantiates a new TypePatrol Group.
   * 
   * @param id the id of the group to be instantiated
   * @param groupList the list of ants to be assigned to this group
   */
  public TypePatrol(int id, List<Ant> groupList)
  {
    super(id, type, groupList);
  }

  @Override
  public void startBehavior(FormationEnum formation, Location target)
  {
    switch (formation)
    {
      case TRAVEL:
        // TODO: Walk in circle around target
        walk(destination);
        break;
      case BATTLE:
        // Find Target
        // Walk to Target
        // Attack Formation
        // Attack Target
        break;
    }
  }
}
