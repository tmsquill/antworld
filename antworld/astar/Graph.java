package antworld.astar;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import antworld.constants.Constants;
import antworld.data.Direction;

/**
 * This class represents a graph that associates locations with nodes.
 * 
 * @author Troy Squillaci Date: 08-28-2014
 */
public class Graph
{
  /**
   * Map data structure that represents the graph by associating Location keys
   * to Node values.
   */
  private BufferedImage           image;
  private HashMap<Location, Node> nodeMap;

  private Node                    startNode;
  private Node                    goalNode;

  /**
   * Instantiates a new HashMap object to be used as the map implementation of
   * the graph.
   */
  public Graph(String filename)
  {
    this.nodeMap = new HashMap<Location, Node>();
    try
    {
      this.image = ImageIO.read(Graph.class.getResourceAsStream(filename));
    } catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Adds a weighted node to the graph at the specified location.
   * 
   * @param location
   *          a location on a 2D plane
   * @param weight
   *          the traversal cost of moving to the node
   */
  public void addNode(Location location)
  {
    this.nodeMap.put(location, new Node(location, this.calcWeight(location)));
  }

  /**
   * Retrieves the node at the specified location.
   * 
   * @param location
   *          the location of the desired node
   * @return the node at the specified location, if a node does not exist at the
   *         location null is returned
   */
  public Node getNode(Location location)
  {
    return this.nodeMap.get(location);
  }

  /**
   * Retrieves adjacent nodes that can be traversed.
   * 
   * @param node
   *          the node to be inspected for adjacent nodes
   * @return a set of nodes that are can be traversed and are adjacent to the
   *         specified node
   */
  public Set<Node> getInteractiveAdjacentNodes(Node node)
  {
    HashSet<Node> adjacentNodes = new HashSet<Node>();

    // Gather adjacent nodes and put them into a set.
    Node tmpNode = null;
    char tmpWeight = 0;

    for (Direction dir : Direction.values())
    {
      Location tmpLocation = new Location(node.getLocation().getX()
          + dir.deltaX(), node.getLocation().getY() + dir.deltaY());

      tmpNode = this.getNode(tmpLocation);

      if (tmpNode == null)
      {
        tmpWeight = this.calcWeight(tmpLocation);

        if (tmpWeight == 'X')
          continue;

        this.addNode(tmpLocation);
        tmpNode = this.getNode(tmpLocation);
      }

      adjacentNodes.add(tmpNode);
    }

    return adjacentNodes;
  }

  public void clearGraph()
  {
    this.nodeMap.clear();
  }

  public char calcWeight(Location location)
  {
    if (location.getX() < 0 || location.getX() > 4999 || location.getY() < 0 || location.getY() > 2499)
    {
      return 'X';
    }

    int colorValue = image.getRGB(location.getX(), location.getY());
    char weight = '0';

    switch (colorValue)
    {
      case 0x0000C0:
        weight = 'X'; 
        break;
      case 0x0000C8: // Cannot traverse terrain, not Jesus.
        weight = 'X'; 
        break;
      case 0xAA00FF:
        weight = 'X'; 
        break;
      case 0xA300F4:
        weight = 'X';
        break;
      case 0x9800E3:
        weight = 'X'; 
        break;
      case 0x8E00D5:
        weight = 'X';
        break;
      case 0x8900CD:
        weight = 'X'; 
        break;
      case 0x7C00BA:
        weight = 'X'; 
        break;
      case 0x7000A8:
        weight = 'X'; 
        break;
      default:
        break;
    }

    return weight;
  }

  /**
   * Assigns a parent node to the specified set of children nodes.
   * 
   * @param children
   *          a set of children nodes
   * @param parent
   *          the node that will become the parent of the children nodes
   */
  public void assignParent(Set<Node> children, Node parent)
  {
    Iterator<Node> it = children.iterator();

    while (it.hasNext())
    {
      it.next().setParent(parent);
    }
  }

  /**
   * Gets the start node.
   * 
   * @return a reference to the start node
   */
  public Node getStartNode()
  {
    return this.startNode;
  }

  /**
   * Gets the goal node.
   * 
   * @return a reference to the goal node
   */
  public Node getGoalNode()
  {
    return this.goalNode;
  }

  /**
   * Sets the start node in the graph.
   * 
   * @param start
   *          the node representing the start of the search
   */
  public void setStartNode(Node start)
  {
    this.startNode = start;
  }

  /**
   * Sets the goal node in the graph.
   * 
   * @param goal
   *          the node representing the start of the search
   */
  public void setGoalNode(Node goal)
  {
    this.goalNode = goal;
  }

  /**
   * Prints each node in the graph by calling the Node.toString() method.
   */
  public void printGraph()
  {
    for (Location name : nodeMap.keySet())
    {
      System.out.println(nodeMap.get(name).toString());
    }
  }
}
