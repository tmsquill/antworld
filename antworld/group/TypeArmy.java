package antworld.group;

import antworld.astar.Location;

public class TypeArmy extends Group
{
  private int id;
  private static GroupTypeEnum type = GroupTypeEnum.ARMY;
  private FormationEnum formation;
  private Location destination;
  private int size;

  public TypeArmy(int id)
  {
    super(id, type);
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
