package antworld.ant;

import antworld.astar.Location;
import antworld.data.Direction;

/**
 * This class provides utility functions that ants use in the AI methods.
 * 
 * @author Troy Squillaci, J. Jake Nichol
 */
public class AntUtilities
{
  /**
   * Gets the opposite direction of the provided direction.
   * 
   * @param direction a direction to find the opposite direction of
   * @return the opposite direction
   */
  public static Direction getOppositeDirection(Direction direction)
  {
    switch (direction)
    {
      case NORTH:
        return Direction.SOUTH;
      case NORTHEAST:
        return Direction.SOUTHWEST;
      case EAST:
        return Direction.WEST;
      case SOUTHEAST:
        return Direction.NORTHWEST;
      case SOUTH:
        return Direction.NORTH;
      case SOUTHWEST:
        return Direction.NORTHEAST;
      case WEST:
        return Direction.EAST;
      case NORTHWEST:
        return Direction.SOUTHEAST;
      default:
        return null;
    }
  }

  
  
  /**
   * Gets the relative direction from location one to location two.
   * 
   * @param location1 the starting location
   * @param location2 the destination location
   * @return the general direction from location one to location two
   */
  public static Direction getGeneralDirection(Location location1, Location location2)
  {
    int deltaX = location2.getX() - location1.getX();
    int deltaY = location2.getY() - location1.getY();

    if (deltaY < 0) return Direction.NORTH;
    else if (deltaX > 0 && deltaY < 0) return Direction.NORTHEAST;
    else if (deltaX > 0) return Direction.EAST;
    else if (deltaX > 0 && deltaY > 0) return Direction.SOUTHEAST;
    else if (deltaY > 0) return Direction.SOUTH;
    else if (deltaX < 0 && deltaY > 0) return Direction.SOUTHWEST;
    else if (deltaY < 0) return Direction.WEST;
    else if (deltaX < 0 && deltaY < 0) return Direction.NORTHWEST;
    else 
    {
      System.out.println("THEY'RE ON ME LIKE FLIES ON SHIT OVER HERE!!!");
      return Direction.getRandomDir();
    }
  }

  
  
  /**
   * Gets the total food count of the nest.
   * 
   * @param resources the stockpile of the nest
   * @return the total count of food in the nest
   */
  public static int getTotalFoodCount(int[] resources)
  {
    int count = 0;

    for (int i = 0; i < resources.length; i++)
    {
      count += resources[i];
    }
    count -= resources[1];

    return count;
  }

  
  
  /**
   * Finds the Manhattan distance between two points.
   * 
   * @param x1 the grid X of the starting location
   * @param y1 the grid Y of the destination location
   * @param x2 the grid X of the starting location
   * @param y2 the grid Y of the destination location
   * @return the distance between the two points
   */
  public static int manhattanDistance(int x1, int y1, int x2, int y2)
  {
    int dx = Math.abs(x2 - x1);
    int dy = Math.abs(y2 - y1);
    return dx + dy;
  }
}
