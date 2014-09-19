package antworld.group;

import java.util.List;

import antworld.ant.Ant;
import antworld.astar.Location;
import antworld.constants.FormationEnum;
import antworld.constants.GroupTypeEnum;

/**
 * This class represents a forager group designed to gather food/water.
 * 
 * @author Troy Squillaci, J. Jake Nichol
 */
public class TypeForager extends Group
{
  /** The group type of this group. */
  private static GroupTypeEnum type = GroupTypeEnum.FORAGER;

  /** The destination of this group. */
  private Location destination;

  /**
   * Instantiates a new TypeForager Group.
   * 
   * @param id the id of the group to be instantiated
   * @param groupList the list of ants to be assigned to this group
   */
  public TypeForager(int id, List<Ant> groupList)
  {
    super(id, type, groupList);
  }

  @Override
  public void startBehavior(FormationEnum formation, Location target)
  {
    switch (formation)
    {
      case TRAVEL:
        walk(destination);
        break;
      case BATTLE:
        // Attack?
        // Find Target
        // Walk to Target
        // Attack Formation
        // Attack Target
        // Retreat?
        // retreat();
        break;
    }
  }
}
