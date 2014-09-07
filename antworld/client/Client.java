package antworld.client;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JFrame;

import antworld.ant.Ant;
import antworld.astar.AStar;
import antworld.astar.Graph;
import antworld.astar.Location;
import antworld.constants.ActivityEnum;
import antworld.data.AntAction;
import antworld.data.AntData;
import antworld.data.CommData;
import antworld.data.Constants;
import antworld.data.Direction;
import antworld.data.FoodData;
import antworld.data.NestNameEnum;
import antworld.data.TeamNameEnum;
import antworld.data.AntAction.AntActionType;
import antworld.gui.AntLive;
import antworld.info.AntManager;

public class Client
{
  private static final boolean DEBUG = true;
  private static final TeamNameEnum myTeam = TeamNameEnum.Toothachegrass;
  private static final long password = 1039840868147L;
  private ObjectInputStream inputStream = null;
  private ObjectOutputStream outputStream = null;
  private boolean isConnected = false;
  private NestNameEnum myNestName = null;
  private int centerX, centerY;

  private ArrayList<Ant> ants = new ArrayList<Ant>();

  private Graph graph;
  private AStar astar;

  private Socket clientSocket;

  private static Random random = Constants.random;

  public Client(String host, int portNumber)
  {
    System.out.println("Starting Client: " + System.currentTimeMillis());
    isConnected = false;
    while (!isConnected)
    {
      isConnected = openConnection(host, portNumber);
      if (!isConnected) try
      {
        Thread.sleep(1000);
      }
      catch (InterruptedException e1)
      {
      }
    }
    CommData data = chooseNest();
    mainGameLoop(data);
    closeAll();
  }

  private boolean openConnection(String host, int portNumber)
  {
    try
    {
      clientSocket = new Socket(host, portNumber);
    }
    catch (UnknownHostException e)
    {
      System.err.println("Client Error: Unknown Host " + host);
      e.printStackTrace();
      return false;
    }
    catch (IOException e)
    {
      System.err.println("Client Error: Could not open connection to " + host + " on port " + portNumber);
      e.printStackTrace();
      return false;
    }

    try
    {
      outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
      inputStream = new ObjectInputStream(clientSocket.getInputStream());
    }
    catch (IOException e)
    {
      System.err.println("Client Error: Could not open I/O streams");
      e.printStackTrace();
      return false;
    }

    return true;
  }

  public void closeAll()
  {
    System.out.println("Client.closeAll()");
    {
      try
      {
        if (outputStream != null) outputStream.close();
        if (inputStream != null) inputStream.close();
        clientSocket.close();
      }
      catch (IOException e)
      {
        System.err.println("Client Error: Could not close");
        e.printStackTrace();
      }
    }
  }

  public CommData chooseNest()
  {
    while (myNestName == null)
    {
      try
      {
        Thread.sleep(100);
      }
      catch (InterruptedException e1)
      {
      }

      NestNameEnum requestedNest = NestNameEnum.values()[random.nextInt(NestNameEnum.SIZE)];
      CommData data = new CommData(requestedNest, myTeam);
      data.password = password;

      if (sendCommData(data))
      {
        try
        {
          if (DEBUG) System.out.println("Client: listening to socket....");
          CommData recvData = (CommData) inputStream.readObject();
          if (DEBUG) System.out.println("Client: recived <<<<<<<<<" + inputStream.available() + "<...\n" + recvData);

          if (recvData.errorMsg != null)
          {
            System.err.println("Client ***ERROR***: " + recvData.errorMsg);
            continue;
          }

          if ((myNestName == null) && (recvData.myTeam == myTeam))
          {
            myNestName = recvData.myNest;
            centerX = recvData.nestData[myNestName.ordinal()].centerX;
            centerY = recvData.nestData[myNestName.ordinal()].centerY;
            System.out.println("Client: !!!!!Nest Request Accepted!!!! " + myNestName);
            return recvData;
          }
        }
        catch (IOException e)
        {
          System.err.println("Client ***ERROR***: client read failed");
          e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
          System.err.println("Client ***ERROR***: client sent incorect data format");
        }
      }
    }
    return null;
  }

  public void mainGameLoop(CommData data)
  {
    AntManager manager = new AntManager(data);
    AntLive live = new AntLive(manager, null);

    JFrame frame = new JFrame("Live Ant Statistics");
    frame.add(live);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    this.graph = new Graph(antworld.constants.Constants.WORLD_MAP_FILEPATH);
    this.astar = new AStar(graph);

    this.populateAnts(data.myAntList);

    int i = 0;

    while (true)
    {
      i++;

      if (i % 20 == 0)
      {
        System.out.println("Food Packets: " + data.foodSet.size());

        FoodData tmp = null;
        Iterator<FoodData> it = data.foodSet.iterator();

        while (it.hasNext())
        {
          tmp = it.next();

          System.out.println("(" + tmp.gridX + ", " + tmp.gridY + ")");
        }
      }

      try
      {
        if (DEBUG) System.out.println("Client: chooseActions: " + myNestName);

        chooseActionsOfAllAnts(data);

        CommData sendData = data.packageForSendToServer();

        // System.out.println("ClientRandomWalk: Sending>>>>>>>: " + sendData);
        outputStream.writeObject(sendData);
        outputStream.flush();
        outputStream.reset();

        if (DEBUG) System.out.println("Client: listening to socket....");
        CommData recivedData = (CommData) inputStream.readObject();
        if (DEBUG) System.out.println("Client: received <<<<<<<<<" + inputStream.available() + "<...\n" + recivedData);
        data = recivedData;

        manager.updateAllAnts();
        live.update(data.foodSet);

        if ((myNestName == null) || (data.myTeam != myTeam))
        {
          System.err.println("Client: !!!!ERROR!!!! " + myNestName);
        }
      }
      catch (IOException e)
      {
        System.err.println("Client ***ERROR***: client read failed");
        e.printStackTrace();
        try
        {
          Thread.sleep(1000);
        }
        catch (InterruptedException e1)
        {
        }

      }
      catch (ClassNotFoundException e)
      {
        System.err.println("ServerToClientConnection***ERROR***: client sent incorect data format");
        e.printStackTrace();
        try
        {
          Thread.sleep(1000);
        }
        catch (InterruptedException e1)
        {
        }
      }
    }
  }

  public void populateAnts(ArrayList<AntData> antData)
  {
    Iterator<AntData> it = antData.iterator();

    while (it.hasNext())
    {
      this.ants.add(new Ant(it.next()));
    }
  }

  private boolean sendCommData(CommData data)
  {
    CommData sendData = data.packageForSendToServer();
    try
    {
      if (DEBUG) System.out.println("Client.sendCommData(" + sendData + ")");
      outputStream.writeObject(sendData);
      outputStream.flush();
      outputStream.reset();
    }
    catch (IOException e)
    {
      System.err.println("Client ***ERROR***: client read failed");
      e.printStackTrace();
      try
      {
        Thread.sleep(1000);
      }
      catch (InterruptedException e1)
      {
      }
      return false;
    }

    return true;
  }

  private void chooseActionsOfAllAnts(CommData commData)
  {
    for (int i = 0; i < 5; i++)
    {
      AntAction action = chooseAction(commData, this.ants.get(i));
      commData.myAntList.get(i).myAction = action;
      System.out.println("Chosen action type: " + action.type + ", direction " + action.direction);
    }

    // for (Ant ant : this.ants)
    // {
    // AntAction action = chooseAction(commData, ant);
    // ant.antData.myAction = action;
    // }
  }

  private AntAction chooseAction(CommData data, Ant ant)
  {
    System.out.println("Getting the action of ant " + ant.antData.id);

    AntAction action = new AntAction(AntActionType.STASIS);

    if (ant.antData.ticksUntilNextAction > 0) return action;

    if (ant.antData.underground)
    {
      action.type = AntActionType.EXIT_NEST;
      action.x = centerX - Constants.NEST_RADIUS + random.nextInt(2 * Constants.NEST_RADIUS);
      action.y = centerY - Constants.NEST_RADIUS + random.nextInt(2 * Constants.NEST_RADIUS);
      return action;
    }

    if (ant.antData.carryUnits > 0) ant.setActivity(ActivityEnum.CARRYING_FOOD);

    // If the ant is at the food, pick it up.
    if (ant.getActivity() == ActivityEnum.APPROACHING_FOOD && ant.isDirectionsEmpty())
    {
      action.type = AntActionType.PICKUP;
      action.direction = Direction.NORTH;
      action.quantity = 24;
      return action;
    }

    // If food is around and the ant is searching, set course for food.
    else if (!data.foodSet.isEmpty() && ant.getActivity() == ActivityEnum.SEARCHING_FOR_FOOD)
    {
      ant.directions.clear();
      graph.clearGraph();

      FoodData food = data.foodSet.iterator().next();

      Location antPosition = new Location(ant.antData.gridX, ant.antData.gridY);
      Location foodPosition = new Location(food.gridX, food.gridY - 1);

      this.graph.addNode(antPosition);
      this.graph.addNode(foodPosition);

      this.graph.setStartNode(this.graph.getNode(antPosition));
      this.graph.setGoalNode(this.graph.getNode(foodPosition));

      ant.setDirections(this.astar.getDirections(this.astar.aStar(this.graph.getStartNode(), this.graph.getGoalNode())));
      ant.setActivity(ActivityEnum.APPROACHING_FOOD);

      action.type = AntActionType.MOVE;
      action.direction = ant.getNextDirection();
      return action;
    }

    // If the ant is carrying food, head home.
    else if (ant.getActivity() == ActivityEnum.CARRYING_FOOD && ant.isDirectionsEmpty())
    {
      this.graph.clearGraph();

      this.graph.addNode(new Location(ant.antData.gridX, ant.antData.gridY));
      this.graph.addNode(new Location(this.centerX, this.centerY));
      this.graph.setStartNode(this.graph.getNode(new Location(ant.antData.gridX, ant.antData.gridY)));
      this.graph.setGoalNode(this.graph.getNode(new Location(this.centerX, this.centerY)));
      ant.setDirections(astar.getDirections(astar.aStar(this.graph.getStartNode(), this.graph.getGoalNode())));

      action.type = AntActionType.MOVE;
      action.direction = ant.getNextDirection();
      return action;
    }

    // If the ant is carrying food and on the nest then drop the food.
    else if (ant.getActivity() == ActivityEnum.CARRYING_FOOD && ant.isDirectionsEmpty())
    {
      action.type = AntActionType.DROP;
      action.direction = Direction.NORTH;
      action.quantity = ant.antData.carryUnits;
      return action;
    }

    // If the ant is not carrying or found food, look for food.
    else if (ant.getActivity() == ActivityEnum.SEARCHING_FOR_FOOD && ant.isDirectionsEmpty())
    {
      Point randomDestination = new Point();

      int distance = 100;

      do
      {
        switch (Direction.getRandomDir())
        {
          case NORTH:
            randomDestination.x = ant.antData.gridX;
            randomDestination.y = ant.antData.gridY - distance;
            break;
          case NORTHEAST:
            randomDestination.x = ant.antData.gridX + distance;
            randomDestination.y = ant.antData.gridY - distance;
            break;
          case EAST:
            randomDestination.x = ant.antData.gridX + distance;
            randomDestination.y = ant.antData.gridY;
            break;
          case SOUTHEAST:
            randomDestination.x = ant.antData.gridX + distance;
            randomDestination.y = ant.antData.gridY + distance;
            break;
          case SOUTH:
            randomDestination.x = ant.antData.gridX;
            randomDestination.y = ant.antData.gridY + distance;
            break;
          case SOUTHWEST:
            randomDestination.x = ant.antData.gridX - distance;
            randomDestination.y = ant.antData.gridY + distance;
            break;
          case WEST:
            randomDestination.x = ant.antData.gridX - distance;
            randomDestination.y = ant.antData.gridY;
            break;
          case NORTHWEST:
            randomDestination.x = ant.antData.gridX - distance;
            randomDestination.y = ant.antData.gridY - distance;
            break;
          default:
            System.out.println("There a big problem here...");
            break;
        }
      }
      while (graph.calcWeight(new Location(randomDestination.x, randomDestination.y)) == 'X');

      this.graph.clearGraph();
      this.graph.addNode(new Location(ant.antData.gridX, ant.antData.gridY));
      this.graph.addNode(new Location(randomDestination.x, randomDestination.y));
      this.graph.setStartNode(this.graph.getNode(new Location(ant.antData.gridX, ant.antData.gridY)));
      this.graph.setGoalNode(this.graph.getNode(new Location(randomDestination.x, randomDestination.y)));
      ant.setDirections(astar.getDirections(astar.aStar(this.graph.getStartNode(), this.graph.getGoalNode())));

      action.type = AntActionType.MOVE;
      action.direction = ant.getNextDirection();
      return action;
    }

    else
    {
      action.type = AntActionType.MOVE;
      action.direction = ant.getNextDirection();
      return action;
    }

    // if (ant.directionIsEmpty())
    // {
    // System.out.println("Ant is idle, awaiting instructions.");
    //
    // if (ant.antData.carryUnits > 0 && ant.antData.gridX == this.centerX &&
    // ant.antData.gridY == this.centerY)
    // {
    // System.out.print("Ant is on the nest and has food, dropping food.");
    // action.type = AntActionType.DROP;
    // action.direction = Direction.NORTH;
    // action.quantity = ant.antData.carryUnits;
    // }
    // else if (ant.antData.carryUnits > 40)
    // {
    // this.graph.clearGraph();
    // this.graph.addNode(new Location(ant.antData.gridX, ant.antData.gridY));
    // this.graph.addNode(new Location(this.centerX, this.centerY));
    // this.graph.setStartNode(this.graph.getNode(new
    // Location(ant.antData.gridX, ant.antData.gridY)));
    // this.graph.setGoalNode(this.graph.getNode(new Location(this.centerX,
    // this.centerY)));
    // ant.setPath(astar.getDirections(astar.aStar(this.graph.getStartNode(),
    // this.graph.getGoalNode())));
    //
    // System.out.println("Ant is encumbered, moving home to the nest located at: "
    // + this.graph.getGoalNode().toString());
    // }
    // else if (!data.foodSet.isEmpty())
    // {
    // FoodData tmp = null;
    //
    // Iterator<FoodData> it = data.foodSet.iterator();
    // tmp = it.next();
    //
    // this.graph.clearGraph();
    // this.graph.addNode(new Location(ant.antData.gridX, ant.antData.gridY));
    // this.graph.addNode(new Location(tmp.gridX, tmp.gridY));
    // this.graph.setStartNode(this.graph.getNode(new
    // Location(ant.antData.gridX, ant.antData.gridY)));
    // this.graph.setGoalNode(this.graph.getNode(new Location(tmp.gridX,
    // tmp.gridY)));
    // ant.setPath(astar.getDirections(astar.aStar(this.graph.getStartNode(),
    // this.graph.getGoalNode())));
    //
    // System.out.println("Food Spotted, moving to: " +
    // this.graph.getGoalNode().toString());
    // }
    // else
    // {
    // System.out.println("Ant is deciding where to move, looking for food.");
    //
    // Point tmp = new Point();
    //
    // int distance = 100;
    //
    // do
    // {
    // switch (Direction.getRandomDir())
    // {
    // case NORTH:
    // tmp.x = ant.antData.gridX;
    // tmp.y = ant.antData.gridY - distance;
    // break;
    // case NORTHEAST:
    // tmp.x = ant.antData.gridX + distance;
    // tmp.y = ant.antData.gridY - distance;
    // break;
    // case EAST:
    // tmp.x = ant.antData.gridX + distance;
    // tmp.y = ant.antData.gridY;
    // break;
    // case SOUTHEAST:
    // tmp.x = ant.antData.gridX + distance;
    // tmp.y = ant.antData.gridY + distance;
    // break;
    // case SOUTH:
    // tmp.x = ant.antData.gridX;
    // tmp.y = ant.antData.gridY + distance;
    // break;
    // case SOUTHWEST:
    // tmp.x = ant.antData.gridX - distance;
    // tmp.y = ant.antData.gridY + distance;
    // break;
    // case WEST:
    // tmp.x = ant.antData.gridX - distance;
    // tmp.y = ant.antData.gridY;
    // break;
    // case NORTHWEST:
    // tmp.x = ant.antData.gridX - distance;
    // tmp.y = ant.antData.gridY - distance;
    // break;
    // default: System.out.println("There a big problem here...");
    // break;
    // }
    // }
    // while (graph.calcWeight(new Location(tmp.x, tmp.y)) == 'X');
    //
    // System.out.println("Ant has decided to move.");
    //
    // this.graph.clearGraph();
    // this.graph.addNode(new Location(ant.antData.gridX, ant.antData.gridY));
    // this.graph.addNode(new Location(tmp.x, tmp.y));
    // this.graph.setStartNode(this.graph.getNode(new
    // Location(ant.antData.gridX, ant.antData.gridY)));
    // this.graph.setGoalNode(this.graph.getNode(new Location(tmp.x, tmp.y)));
    // ant.setPath(astar.getDirections(astar.aStar(this.graph.getStartNode(),
    // this.graph.getGoalNode())));
    // }
    // }
    // else
    // {
    // action.type = AntActionType.MOVE;
    // action.direction = ant.getNextDirection();
    // }
  }

  public static void main(String[] args)
  {
    if (args.length == 0) new Client("b146-75", Constants.PORT);
    else new Client(args[0], Constants.PORT);
  }
}
