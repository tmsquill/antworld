package antworld.group;

import java.awt.Rectangle;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import antworld.ant.Ant;
import antworld.astar.Location;
import antworld.constants.FormationEnum;
import antworld.constants.GroupTypeEnum;

/**
 * This class represents an ant group object. It is the parent class to all ant
 * groups.
 * 
 * @author Troy Squillaci, J. Jake Nichol
 */
public abstract class Group implements Comparator<Ant>
{
  /** The group id. */
  private int id;

  /** The group's size. */
  private int size;

  /** The group's leader. */
  private Ant leader;

  /** The ant to be at the front of the group at all times. */
  private Ant formationPoint;

  /** The ant to be at the rear of the group at all times. */
  private Ant formationRear;

  /** The list of ants assigned to this group. */
  private PriorityQueue<Ant> groupList;

  /** The type of this group. */
  private GroupTypeEnum type;

  /** The formation type of this group. */
  private FormationEnum formation;

  /**
   * Instantiates a new Group object.
   * 
   * @param id the id of this group
   * @param type the type of this group
   * @param group the list of ants to be assigned to this group
   */
  public Group(int id, GroupTypeEnum type, List<Ant> group)
  {
    this.id = id;
    this.type = type;
    this.groupList = new PriorityQueue<Ant>(11, this);
    groupList.addAll(group);
  }

  /**
   * Walks this Group to the given location by AStar.
   * 
   * @param destination the location to walk to
   */
  public void walk(Location destination)
  {
    // TODO Walk to destination using A*
  }

  /**
   * Begins the core behavior defined by each group type.
   * 
   * @param formation formation type
   * @param target the location to walk to/attack/heal/gather from
   */
  public abstract void startBehavior(FormationEnum formation, Location target);

  /**
   * Given a list of ants, take each ant as necessary and calculate A*
   * such the ants will align into formation.
   * 
   * @param ants ants in the ant group
   * @param formation formation type
   */
  public void alignAnts(List<Ant> ants, Formation formation)
  {

  }

  /**
   * Tells this Group to retreat back to the nest.
   */
  public void retreatToNest()
  {

  }

  /**
   * Tells this Group to retreat to a specified location.
   * 
   * @param location the location to retreat to
   */
  public void retreatToLocation(Location location)
  {

  }

  @Override
  public int compare(Ant ant1, Ant ant2)
  {
    if (ant1.getAntData().carryUnits > ant2.getAntData().carryUnits) return 1;
    else if (ant1.getAntData().carryUnits < ant2.getAntData().carryUnits) return -1;
    return 0;
  }

  /**
   * Gets the id of this Group.
   * 
   * @return returns the id of this Group
   */
  public int getID()
  {
    return id;
  }

  /**
   * Gets the list of ants assigned to this Group.
   * 
   * @return returns the list of ants assigned to this Group
   */
  public PriorityQueue<Ant> getGroupList()
  {
    return this.groupList;
  }

  /**
   * Gets the leader ant of this Group.
   * 
   * @return returns the leader ant of this Group
   */
  public Ant getLeader()
  {
    return leader;
  }

  /**
   * Gets the point ant of this Group.
   * 
   * @return returns the point ant of this Group
   */
  public Ant getFormationPoint()
  {
    return formationPoint;
  }

  /**
   * Gets the rear ant of this Group.
   * 
   * @return returns the rear ant of this Group
   */
  public Ant getFormationRear()
  {
    return formationRear;
  }

  /**
   * Gets the size of this Group.
   * 
   * @return returns the size of this Group
   */
  public int getSize()
  {
    return size;
  }

  /**
   * Gets the type of this Group.
   * 
   * @return returns the type of this Group
   */
  public GroupTypeEnum getType()
  {
    return type;
  }

  /**
   * Gets the Formation object of this Group.
   * 
   * @return returns the Formation object of this Group
   */
  public FormationEnum getFormation()
  {
    return formation;
  }

  /**
   * Sets the leader of this Group.
   * 
   * @param leader the ant to be the leader of this Group
   */
  public void setLeader(Ant leader)
  {
    this.leader = groupList.peek();
  }

  /**
   * Sets point ant of the formation (front) to the ant at the end of the queue.
   * This ant should be the ant with the least food or is ATTACK/DEFENCE.
   * 
   * @param formationPoint the ant to be the point of the Group
   */
  public void setFormationPoint(Ant formationPoint)
  {
    Iterator<Ant> it = groupList.iterator();
    while (it.hasNext())
      this.formationPoint = it.next();
  }

  /**
   * Sets rear ant of the formation to the ant second to last in the queue.
   * This ant should be the ant with the second to least food or is
   * ATTACK/DEFENCE.
   * 
   * @param formationRear the ant to be the rear of this Group
   */
  public void setFormationRear(Ant formationRear)
  {
    Iterator<Ant> it = groupList.iterator();
    Ant previous = null;
    Ant current = null;
    while (it.hasNext())
    {
      current = it.next();
      if (previous != null) this.formationRear = previous;
      previous = current;
    }
  }

  /**
   * Sets the size of this Group.
   * 
   * @param size the size this Group is to be
   */
  public void setSize(int size)
  {
    this.size = size;
  }

  /**
   * Sets the formation type of this Group.
   * 
   * @param formation the formation type this Group is to be
   */
  public void setFormation(FormationEnum formation)
  {
    this.formation = formation;
  }
}
