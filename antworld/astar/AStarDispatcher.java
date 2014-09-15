package antworld.astar;

import antworld.astar.AStar;
import antworld.astar.Graph;
import antworld.astar.Location;
import antworld.astar.Node;
import antworld.constants.Constants;
import antworld.data.Direction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AStarDispatcher
{
  private static final int THREADS = 12;
  private ExecutorService executor = Executors.newFixedThreadPool(THREADS);

  public AStarDispatcher()
  {
    Graph.initializeGraph(Constants.WORLD_MAP_FILEPATH);
  }
  
  public LinkedList<Direction> dispatchAStar(Location start, Location end)
  {
    try
    {
      return this.executor.submit(new AStar(new Graph(), new Node(start, '0'), new Node(end, '0'))).get();
    }
    catch (InterruptedException | ExecutionException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return null;
  }

  public static void main(String[] args)
  {
    new AStarDispatcher();
    
    ExecutorService executor = Executors.newFixedThreadPool(THREADS);
    List<Future<LinkedList<Direction>>> list = new ArrayList<Future<LinkedList<Direction>>>();
    for (int i = 0; i < 1000; i++)
    {
      Callable<LinkedList<Direction>> worker = new AStar(new Graph(), new Node(new Location(1500 + i, 1000 + i), '0'),
                                              new Node(new Location(1525 + i, 1025 + i), '0'));
      System.out.println("Running AStar from (" + (1500 + i) + ", " + (1000 + i) + ") to (" + (1525 + i) + ", " + (1025 + i) + ")");
      Future<LinkedList<Direction>> submit = executor.submit(worker);
      list.add(submit);
    }
    System.out.println("Number of Future objects: " + list.size());

    for (Future<LinkedList<Direction>> future : list)
    {
      try
      {
        System.out.println(future.get());
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
      catch (ExecutionException e)
      {
        e.printStackTrace();
      }
    }
    executor.shutdown();
  }
}
