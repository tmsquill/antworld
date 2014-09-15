package antworld.food;

import java.util.ArrayList;
import java.util.List;

import antworld.ant.Ant;
import antworld.astar.Location;
import antworld.data.Direction;
import antworld.data.FoodData;

public class Food
{
  private static final int COLLECTORS = 4;
  private FoodData foodData;
  private List<Ant> collectors = new ArrayList<Ant>();
  
  public Food(FoodData foodData)
  {
    this.foodData = foodData;
  }
  
  public void setFoodData(FoodData foodData)
  {
    this.foodData = foodData;
  }
  
  public FoodData getFoodData()
  {
    return this.foodData;
  }
  
  public Location addCollector(Ant ant)
  {
    Location tmp = null;
    switch (this.collectors.size())
    {
      case 0:
        tmp = new Location(this.foodData.gridX - 1, this.foodData.gridY);
        ant.setPickupDirection(Direction.EAST); 
      break;
      case 1: 
        tmp = new Location(this.foodData.gridX + 1, this.foodData.gridY);
        ant.setPickupDirection(Direction.WEST); 
      break;
      case 2: 
        tmp = new Location(this.foodData.gridX, this.foodData.gridY + 1);
        ant.setPickupDirection(Direction.NORTH); 
      break;
      case 3: 
        tmp = new Location(this.foodData.gridX, this.foodData.gridY - 1);
        ant.setPickupDirection(Direction.SOUTH); 
      break;
      default: System.out.println("Food: Error more than three ants as collectors");
      break;
    }
    this.collectors.add(ant);
    System.out.println("Assigning ant " + ant.getAntData().id + " to get food at (" + this.foodData.gridX + ", " + this.foodData.gridY +
        "). Collector Number: " + (this.collectors.size() - 1));
    return tmp;
  }
  
  public List<Ant> getCollectors()
  {
    return this.collectors;
  }
  
  public boolean isFull()
  {
    return this.collectors.size() >= Food.COLLECTORS ? true : false;
  }
}
