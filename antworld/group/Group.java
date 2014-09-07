package antworld.group;

import antworld.astar.Location;

public abstract class Group
{
  private int id;
  private GroupTypeEnum type;
  private FormationEnum formation;
  private int size;

  public Group(int id, GroupTypeEnum type)
  {
    this.setID(id);
    this.setType(type);
  }

  public void walk(Location destination)
  {
    // Walk to destination using A*
  }

  public abstract void startBehavior(FormationEnum formation, Location target);

  
  public int getID()
  {
    return id;
  }

  public void setID(int id)
  {
    this.id = id;
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

  public void setType(GroupTypeEnum type)
  {
    this.type = type;
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
