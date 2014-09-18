package antworld.astar;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;

import antworld.client.Client;
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
  private static BufferedImage image;
  private HashMap<Location, Node> nodeMap = new HashMap<Location, Node>();
  
//  public static Set<Rectangle> unwalkableEnemyAntZones = new HashSet<Rectangle>();
  
  public static Set<Rectangle> unwalkableStaticZones = new HashSet<Rectangle>();

  private Node startNode;
  private Node goalNode;

  public static void initializeGraph(String filepath)
  {
    try
    {
      Graph.image = ImageIO.read(Graph.class.getResourceAsStream(filepath));
    }
    catch (IOException e)
    {
      System.out.println("Graph: Error loading map image");
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
    this.nodeMap.put(location, new Node(location, Graph.isWalkable(location)));
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
      Location tmpLocation = new Location(node.getLocation().getX() + dir.deltaX(), node.getLocation().getY()
          + dir.deltaY());

      tmpNode = this.getNode(tmpLocation);

      if (tmpNode == null)
      {
        tmpWeight = Graph.isWalkable(tmpLocation);

        if (tmpWeight == 'X') continue;

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

  public static boolean isWater(Location location)
  {
    if (location.getX() < 0 || location.getX() > 4999 || location.getY() < 0 || location.getY() > 2499) return false;

    if (new Color(Graph.image.getRGB(location.getX(), location.getY())).getBlue() > 230) return true;

    return false;
  }

  public static char isWalkable(Location location)
  {
    if (location.getX() < 0 || location.getX() > 4999 || location.getY() < 0 || location.getY() > 2499) return 'X';
    
    if (new Color(Graph.image.getRGB(location.getX(), location.getY())).getBlue() > 230) return 'X';

    if (!Client.allowedArea.contains(new Point(location.getX(), location.getY()))) return 'X';
    
//    Iterator<Rectangle> antIt = Graph.unwalkableEnemyAntZones.iterator();
    
//    while (antIt.hasNext())
//    {
//      if (antIt.next().contains(new Point(location.getX(), location.getY()))) return 'X';
//    }
    
    Iterator<Rectangle> staticIt = Graph.unwalkableStaticZones.iterator();
    
    while (staticIt.hasNext())
    {
      if (staticIt.next().contains(new Point(location.getX(), location.getY()))) return 'X';
    }

    return '0';
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
