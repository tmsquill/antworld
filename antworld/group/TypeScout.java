package antworld.group;

import antworld.astar.Location;

public class TypeScout extends Group
{
  private int id;
  private static GroupTypeEnum type = GroupTypeEnum.SCOUT;
  private FormationEnum formation;
  private Location destination;
  private int size;

  public TypeScout(int id)
  {
    super(id, type);
  }

  @Override
  public void startBehavior(FormationEnum formation, Location target)
  {
    switch (formation)
    {
      case TRAVEL:  // Dynamic destination
        walk(destination);
        break;
      case BATTLE:
        // Group should only enter battle if no escape is possible
        //   Retreat?
        //     retreat();
        //   Attack?
        //     Find Target
        //     Walk to Target
        //     Attack Formation
        //     Attack Target
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
