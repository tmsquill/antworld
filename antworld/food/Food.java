package antworld.food;

import java.util.ArrayList;
import java.util.List;

import antworld.ant.Ant;
import antworld.astar.Location;
import antworld.data.Direction;
import antworld.data.FoodData;

/**
 * This class holds data for food found around the map.
 * 
 * @author Troy Squillaci, J. Jake Nichol
 */
public class Food
{
  /** The maximum number of collectors this Food can have. */
  private static final int COLLECTORS = 4;
  
  /** Data from CommData containing information about food in sight. */
  private FoodData foodData;
  
  /** The list of collectors collecting this Food. */
  private List<Ant> collectors = new ArrayList<Ant>();

  /**
   * Instantiates a new food with FoodData.
   * 
   * @param foodData data from CommData containing information about food in sight
   */
  public Food(FoodData foodData)
  {
    this.foodData = foodData;
  }

  /**
   * Sets this Food's foodData field.
   * 
   * @param foodData data from CommData containing information about food in sight
   */
  public void setFoodData(FoodData foodData)
  {
    this.foodData = foodData;
  }

  /**
   * Gets this Food's foodData.
   * 
   * @return returns the foodData associated with this Food
   */
  public FoodData getFoodData()
  {
    return this.foodData;
  }

  /**
   * Adds a collector for this Food.
   * 
   * @param ant an ant to be added
   * @return returns the location of this Food
   */
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
      default:
        System.out.println("Food: Error more than three ants as collectors");
        break;
    }
    this.collectors.add(ant);
    System.out.println("Assigning ant " + ant.getAntData().id + " to get food at (" + this.foodData.gridX + ", "
        + this.foodData.gridY + ").");
    return tmp;
  }

  /**
   * Gets the collectors list for this Food.
   * 
   * @return returns the collectors list for this Food
   */
  public List<Ant> getCollectors()
  {
    return this.collectors;
  }

  /**
   * Determines if this Food's collectors list is full.
   * 
   * @return returns if this Food's collectors list is full
   */
  public boolean isFull()
  {
    return this.collectors.size() >= Food.COLLECTORS ? true : false;
  }
}
