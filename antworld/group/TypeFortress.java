package antworld.group;

import antworld.astar.Location;

public class TypeFortress extends Group
{
  private int id;
  private static GroupTypeEnum type = GroupTypeEnum.FORTRESS;
  private FormationEnum formation;
  private Location destination;
  private int size;

  public TypeFortress(int id)
  {
    super(id, type);
  }

  @Override
  public void startBehavior(FormationEnum formation, Location target)
  {
    switch (formation)
    {
      case TRAVEL:  // Will be stationary most of the time.
        walk(destination);
        break;
      case BATTLE:
        // TODO: Once the group is in destination, enter formation and remain stationary.
        //         Once an enemy is in range, attack.  
        //         Retreat if necessary.
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
