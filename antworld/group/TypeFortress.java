package antworld.group;

import java.awt.Rectangle;
import java.util.List;
import java.util.PriorityQueue;

import antworld.ant.Ant;
import antworld.astar.Location;
import antworld.constants.FormationEnum;
import antworld.constants.GroupTypeEnum;

public class TypeFortress extends Group
{
  private int id;
  private int size;
  private Ant leader;
  private Ant formationPoint;
  private Ant formationRear;
  private PriorityQueue<Ant> groupList;

  private Rectangle shape;
  private static GroupTypeEnum type = GroupTypeEnum.FORTRESS;
  private FormationEnum formation;
  private Location destination;

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

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
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

  public int getSize()
  {
    return size;
  }

  public void setSize(int size)
  {
    this.size = size;
  }

}
