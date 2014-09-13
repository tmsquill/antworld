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

public abstract class Group implements Comparator<Ant>
{
  private int id;
  private int size;
  private Ant leader;
  private Ant formationPoint;
  private Ant formationRear;
  private PriorityQueue<Ant> groupList;

  private GroupTypeEnum type;
  private FormationEnum formation;
  private Rectangle shape;

  public Group(int id, GroupTypeEnum type, List<Ant> group)
  {
    this.id = id;
    this.type = type;
    this.groupList = new PriorityQueue<Ant>(11, this);
    groupList.addAll(group);
  }

  public void walk(Location destination)
  {
    // Walk to destination using A*
  }

  public abstract void startBehavior(FormationEnum formation, Location target);

  // Given a list of ants, it will take each ant as necessary, and calculate A*
  // such the ants will align into formation
  public void alignAnts(List<Ant> ants, Formation formation)
  {

  }

  // Retreats back to nest.
  public void retreatToNest()
  {

  }

  // Retreats to specified location
  public void retreatToLocation(Location location)
  {

  }

  @Override
  public int compare(Ant ant1, Ant ant2)
  {
    if (ant1.antData.carryUnits > ant2.antData.carryUnits) return 1;
    else if (ant1.antData.carryUnits < ant2.antData.carryUnits) return -1;
    return 0;
  }

  public int getID()
  {
    return id;
  }

  public PriorityQueue<Ant> getGroupList()
  {
    return this.groupList;
  }

  public Ant getLeader()
  {
    return leader;
  }

  public void setLeader(Ant leader)
  {
    this.leader = groupList.peek();
  }

  public Ant getFormationPoint()
  {
    return formationPoint;
  }

  // Sets point ant of the formation (front) to the ant at the end of the queue.
  // This ant should be the ant with the least food or is ATTACK/DEFENCE.
  public void setFormationPoint(Ant formationPoint)
  {
    Iterator<Ant> it = groupList.iterator();
    while (it.hasNext()) this.formationPoint = it.next();
  }

  public Ant getFormationRear()
  {
    return formationRear;
  }

  // Sets rear ant of the formation to the ant second to last in the queue.
  // This ant should be the ant with the second to least food or is
  // ATTACK/DEFENCE.
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

  public int getSize()
  {
    return size;
  }

  public void setSize(int size)
  {
    this.size = size;
  }

  public GroupTypeEnum getType()
  {
    return type;
  }

  public FormationEnum getFormation()
  {
    return formation;
  }

  public void setFormation(FormationEnum formation)
  {
    this.formation = formation;
  }

}
