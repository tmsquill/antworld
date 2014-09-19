package antworld.group;

import java.util.List;

import antworld.ant.Ant;
import antworld.astar.Location;
import antworld.constants.FormationEnum;
import antworld.constants.GroupTypeEnum;

/**
 * This class represents a Healer group designed to heal quickly.
 * 
 * @author Troy Squillaci, J. Jake Nichol
 */
public class TypeHealer extends Group
{
  /** The group type of this group. */
  private static GroupTypeEnum type = GroupTypeEnum.HEALER;
  
  /** The destination of this group. */
  private Location destination;

  /**
   * Instantiates a new TypeHealer Group.
   * 
   * @param id the id of the group to be instantiated
   * @param groupList the list of ants to be assigned to this group
   */
  public TypeHealer(int id, List<Ant> groupList)
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
      case BATTLE: // Healer groups should never enter battle: BATTLE == HEAL
        // Heal?
        // Find Target
        // Walk to Target
        // Heal Target
        // Retreat?
        // retreat();
        break;
    }
  }
}
