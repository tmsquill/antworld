package antworld.astar;

/**
 * This class represents a node in a graph.
 * 
 * @author Troy Squillaci Date: 08-28-2014
 */
public class Node
{
  private final Location location;
  private final char     weight;

  private Node           parent;

  private double         g;
  private double         h;
  private double         f;

  public Node(Location location, char weight)
  {
    this.location = location;
    this.weight = weight;
    this.parent = null;
  }

  public Location getLocation()
  {
    return new Location(this.location.getX(), this.location.getY());
  }

  public char getWeight()
  {
    return this.weight;
  }

  public Node getParent()
  {
    return this.parent;
  }

  public double getG()
  {
    return this.g;
  }

  public double getH()
  {
    return this.h;
  }

  public double getF()
  {
    return this.f;
  }

  public void setParent(Node parent)
  {
    this.parent = parent;
  }

  public void setG(double g)
  {
    this.g = g;
  }

  public void setH(double h)
  {
    this.h = h;
  }

  public void calcF()
  {
    this.f = this.g + this.h;
  }

  @Override
  public String toString()
  {
    return "Location: (" + this.location.getX() + ", " + this.location.getY() + ")" + " Weight: " + this.weight
        + " G: " + this.g + " H: " + this.h + " F: " + this.f;
  }
}
