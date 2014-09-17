package antworld.ant;

import antworld.astar.Location;
import antworld.data.Direction;

public class AntUtilities
{
  public static Direction getOppositeDirection(Direction direction)
  {
    switch (direction)
    {
      case NORTH: return Direction.SOUTH;
      case NORTHEAST: return Direction.SOUTHWEST;
      case EAST: return Direction.WEST;
      case SOUTHEAST: return Direction.NORTHWEST;
      case SOUTH: return Direction.NORTH;
      case SOUTHWEST: return Direction.NORTHEAST;
      case WEST: return Direction.EAST;
      case NORTHWEST: return Direction.SOUTHEAST;
      default: return null;
    }
  }
  
  public static Direction getGeneralDirectionToNest(Location ant, Location nest)
  {
    int deltaX = nest.getX() - ant.getX();
    int deltaY = nest.getY() - ant.getY();
    
    if (deltaY < 0) return Direction.NORTH;
    else if (deltaX > 0 && deltaY < 0) return Direction.NORTHEAST;
    else if (deltaX > 0) return Direction.EAST;
    else if (deltaX > 0 && deltaY > 0) return Direction.SOUTHEAST;
    else if (deltaY > 0) return Direction.SOUTH;
    else if (deltaX < 0 && deltaY > 0) return Direction.SOUTHWEST;
    else if (deltaY < 0) return Direction.WEST;
    else if (deltaX < 0 && deltaY < 0) return Direction.NORTHWEST;
    else return null;
  }
  
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
  
  public static int manhattanDistance(int x1, int y1, int x2, int y2)
  {
    int dx = Math.abs(x2 - x1);
    int dy = Math.abs(y2 - y1);
    return dx + dy;
  }
}
