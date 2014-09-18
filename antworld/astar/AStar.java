package antworld.astar;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;

import antworld.client.Client;
import antworld.data.Direction;

/**
 * This class implements the A* algorithm.
 * 
 * @author Troy Squillaci Date: 08-28-2014
 */
public class AStar implements Callable<LinkedList<Direction>>
{
  /**
   * A graph that represents the map.
   */
  private Graph activeGraph;

  private Node start;
  private Node finish;

  /**
   * The shortest path that is derived by A*.
   */
  private List<Node> constructedPath = new ArrayList<Node>();

  public AStar(Graph graph, Node start, Node finish)
  {
    this.activeGraph = graph;
    this.start = start;
    this.finish = finish;
  }

  /**
   * A comparator that compares the F values of nodes. Used for placing nodes
   * into the open list which is implemented with a priority quene.
   * 
   * @author Troy Squillaci Date: 08-28-2014
   */
  public class NodeComparator implements Comparator<Node>
  {
    public int compare(Node nodeOne, Node nodeTwo)
    {
      if (nodeOne.getF() > nodeTwo.getF()) return 1;
      if (nodeTwo.getF() > nodeOne.getF()) return -1;
      return 0;
    }
  }

  /**
   * The A* algorithm that will find the shortest path from the start to the
   * goal location using a best first search.
   * 
   * @param start
   *          the start location where the search will begin
   * @param goal
   *          the goal location where the search will end
   * @return a list of ordered nodes on the graph representing the shortest path
   */
  @Override
  public LinkedList<Direction> call() throws Exception
  {
    Queue<Node> open = new PriorityQueue<Node>(11, new NodeComparator());
    Set<Node> neighbors = new HashSet<Node>();
    Set<Node> closed = new HashSet<Node>();

    Node current = this.start;
    Node destination = this.finish;

    current.setG(0);
    current.setH(this.calcHeuristic(current, this.finish));
    current.calcF();

    destination.setG(0);
    destination.setH(0);
    destination.calcF();

    open.add(current);

    while (!open.isEmpty())
    {
      current = open.poll();

      if (current.getLocation().equals(destination.getLocation()))
      {
        destination.setParent(current.getParent());
        this.constructPath(destination);
        Collections.reverse(constructedPath);

        return this.getDirections(constructedPath);
      }

      closed.add(current);

      neighbors.clear();
      neighbors = this.activeGraph.getInteractiveAdjacentNodes(current);

      Iterator<Node> it = neighbors.iterator();

      while (it.hasNext())
      {
        Node tmp = it.next();

        if (closed.contains(tmp)) continue;
        double tentativeG = current.getG() + this.calcWeight(tmp.getWeight());

        if (!open.contains(tmp) || tentativeG < this.calcWeight(tmp.getWeight()))
        {
          // System.out.println(current);
          tmp.setParent(current);
          tmp.setG(tentativeG);
          tmp.setH(this.calcHeuristic(tmp, destination));
          tmp.calcF();

          if (!open.contains(tmp)) open.add(tmp);
        }
      }
    }

    return new LinkedList<Direction>();
  }

  public LinkedList<Direction> getDirections(List<Node> constructedPath)
  {
    LinkedList<Direction> directions = new LinkedList<Direction>();

    Node previous = null;
    Node current = null;

    Iterator<Node> it = constructedPath.iterator();

    while (it.hasNext())
    {
      previous = current;
      current = it.next();

      if (previous == null) continue;

      int deltaX = current.getLocation().getX() - previous.getLocation().getX();
      int deltaY = current.getLocation().getY() - previous.getLocation().getY();

      if (deltaX == 0 && deltaY == -1) directions.push(Direction.NORTH);
      else if (deltaX == 1 && deltaY == -1) directions.push(Direction.NORTHEAST);
      else if (deltaX == 1 && deltaY == 0) directions.push(Direction.EAST);
      else if (deltaX == 1 && deltaY == 1) directions.push(Direction.SOUTHEAST);
      else if (deltaX == 0 && deltaY == 1) directions.push(Direction.SOUTH);
      else if (deltaX == -1 && deltaY == 1) directions.push(Direction.SOUTHWEST);
      else if (deltaX == -1 && deltaY == 0) directions.push(Direction.WEST);
      else if (deltaX == -1 && deltaY == -1) directions.push(Direction.NORTHWEST);
      else System.out.println("Directions are Fucked");
    }

    Collections.reverse(directions);

    return directions;
  }

  /**
   * Calculates the integral weight from a character.
   * 
   * @param weight
   *          a character representation of the weight
   * @return the integral value of the weight
   */
  private double calcWeight(char weight)
  {
    if (weight == '0') return 1.0;
    if (weight == '1') return 2.0;
    if (weight == '2') return 4.0;
    return 0;
  }

  /**
   * The heuristic used to calculate the H value of a node. Uses the Pythagorean
   * Theorem to calculate distances between locations.
   * 
   * @param nodeOne
   *          the current node
   * @param nodeTwo
   *          the destination node (the final goal)
   * @return the distance between the two nodes
   */
  private double calcHeuristic(Node nodeOne, Node nodeTwo)
  {
    double deltaX = nodeOne.getLocation().getX() - nodeTwo.getLocation().getX();
    double deltaY = nodeOne.getLocation().getY() - nodeTwo.getLocation().getY();

    return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
  }

  /**
   * Constructs a backwards path from the goal node to the start node by
   * recursively stepping through the parents of each node until the start node
   * is reached in which case a null parent is found which breaks recursion.
   * 
   * @param list
   *          the list where the constructed path will be stored
   * @param node
   *          the goal node
   * @return a list of nodes containing a backwards path that goes from the goal
   *         node to the start node
   */
  private void constructPath(Node node)
  {
    this.constructedPath.add(node);

    if (node.getParent() == null) return;

    this.constructPath(node.getParent());
  }

  /**
   * Prints a character representation of the constructed path.
   * 
   * U - Move one node up R - Move one node to the right D - Move one node down
   * L - Move one node to the left
   */
  public void printConstructedPath()
  {
    if (this.constructedPath == null || this.constructedPath.isEmpty())
    {
      System.out.println("No path was found.");
    }
    else
    {
      Iterator<Node> it = this.constructedPath.iterator();

      Node previous;
      Node current = it.next();

      while (it.hasNext())
      {
        previous = current;
        current = it.next();

        if (current.getLocation().getX() > previous.getLocation().getX()) System.out.print("R");
        if (current.getLocation().getX() < previous.getLocation().getX()) System.out.print("L");
        if (current.getLocation().getY() > previous.getLocation().getY()) System.out.print("D");
        if (current.getLocation().getY() < previous.getLocation().getY()) System.out.print("U");
      }

      System.out.println();
    }
  }
}
