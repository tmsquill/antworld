package antworld.data;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class FoodData.
 * 
 *!!!!!!!!!! DO NOT MODIFY ANYTHING IN THIS CLASS !!!!!!!!!!<br>
 * This class is serialized across a network socket. Any modifications will
 * prevent the server from being able to read this class.<br><br>
 * 
 * 
 * FoodData contains all data about a food pile game object  that is exchanged 
 * between client and server.
 * 
 */
public class FoodData  implements Serializable 
{
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = Constants.VERSION;

  /** The food type. */
  public FoodType foodType;
  
  /** World Map pixel coordinates of this food pile with (0,0) being upper-left. 
   * In the ant world map, each game object (an ant or a food pile) occupies
   * exactly one pixel. No two game objects may occupy the same pixel at the 
   * same time. NOTE: food being carried by an ant is part of the ant game object.*/
  public int gridX, gridY;
  
  /** The number of food units in the game object food pile. */
  protected int count;
  
  /**
   * Instantiates a new food data.
   *
   * @param foodType the food type
   * @param x the x
   * @param y the y
   * @param count the count
   */
  public FoodData(FoodType foodType, int x, int y, int count)
  {
    this.foodType = foodType;
    this.gridX = x;
    this.gridY = y;
    this.count = count;
  }
  
  /**
   * Gets the count.
   *
   * @return the count
   */
  public int getCount() {return count;}
}