package antworld.astar;

/**
 * This class represents a location on a 2D plane. Objects of this type are immutable.
 * @author Troy Squillaci
 * Date: 08-28-2014
 */
public class Location
{
  private final int x;
  private final int y;

  public Location(int x, int y)
  {
    this.x = x;
    this.y = y;
  }
  
  public int getX()
  {
    return this.x;
  }
  public int getY()
  {
    return this.y;  
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (!(o instanceof Location))
      return false;
    Location key = (Location) o;
    return x == key.x && y == key.y;
  }

  @Override
  public int hashCode()
  {
    int result = x;
    result = 31 * result + y;
    return result;
  }
  
  @Override
  public String toString()
  {
    return "X: " + this.x + " Y: " + this.y;
  }
}
