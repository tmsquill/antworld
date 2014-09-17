package antworld.group;

import java.awt.Rectangle;
import java.util.List;
import java.util.PriorityQueue;

import antworld.ant.Ant;
import antworld.astar.Location;
import antworld.constants.FormationEnum;
import antworld.constants.GroupTypeEnum;

public class TypeHealer extends Group
{
  private int id;
  private int size;
  private Ant leader;
  private Ant formationPoint;
  private Ant formationRear;
  private PriorityQueue<Ant> groupList;

  private Rectangle shape;
  private static GroupTypeEnum type = GroupTypeEnum.HEALER;
  private FormationEnum formation;
  private Location destination;

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
