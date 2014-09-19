package antworld.group;

import java.util.List;
//import java.util.PriorityQueue;

import antworld.ant.Ant;
import antworld.astar.Location;
import antworld.constants.FormationEnum;
import antworld.constants.GroupTypeEnum;

/**
 * This class represents a army group designed for combat.
 * 
 * @author Troy Squillaci, J. Jake Nichol
 */
public class TypeArmy extends Group
{ 
//  private Ant leader;
//  private Ant formationPoint;
//  private Ant formationRear;
//  private PriorityQueue<Ant> groupList;

  
  /** The group type of this group. */
  private static GroupTypeEnum type = GroupTypeEnum.ARMY;
  
  /** The destination of this group. */
  private Location destination;

  /**
   * Instantiates a new TypeArmy Group.
   * 
   * @param id the id of the group to be instantiated
   * @param groupList the list of ants to be assigned to this group
   */
  public TypeArmy(int id, List<Ant> groupList)
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
        // Find Target
        // Walk to Target
        // Attack Formation
        // Attack Target
        break;
    }

  }
}
