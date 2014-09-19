package antworld.group;

import java.util.List;

import antworld.ant.Ant;
import antworld.astar.Location;
import antworld.constants.FormationEnum;
import antworld.constants.GroupTypeEnum;

/**
 * This class represents a fortress group designed to be a stationary defense/scout structure.
 * 
 * @author Troy Squillaci, J. Jake Nichol
 */
public class TypeFortress extends Group
{
  /** The group type of this group. */
  private static GroupTypeEnum type = GroupTypeEnum.FORTRESS;

  /** The destination of this group. */
  private Location destination;

  /**
   * Instantiates a new TypeFortress Group.
   * 
   * @param id the id of the group to be instantiated
   * @param groupList the list of ants to be assigned to this group
   */
  public TypeFortress(int id, List<Ant> groupList)
  {
    super(id, type, groupList);
  }

  @Override
  public void startBehavior(FormationEnum formation, Location target)
  {
    switch (formation)
    {
      case TRAVEL: // Will be stationary most of the time.
        walk(destination);
        break;
      case BATTLE:
        // TODO: Once the group is in destination, enter formation and remain
        // stationary.
        // Once an enemy is in range, attack.
        // Retreat if necessary.
        // Attack Formation
        // Attack Target
        break;
    }
  }
}
