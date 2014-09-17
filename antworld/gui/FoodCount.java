package antworld.gui;

import antworld.data.FoodType;

public class FoodCount
{
  private FoodType type;
  private int count;

  public FoodCount(FoodType type, int count)
  {
    this.type = type;
    this.count = count;
  }

  public FoodType getType()
  {
    return this.type;
  }

  public int getCount()
  {
    return this.count;
  }

  public void setType(FoodType type)
  {
    this.type = type;
  }

  public void setCount(int count)
  {
    this.count = count;
  }
}
