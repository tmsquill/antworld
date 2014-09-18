package antworld.client;

import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import antworld.ant.Ant;
import antworld.ant.AntUtilities;
import antworld.astar.Graph;
import antworld.constants.GatheringEnum;
import antworld.data.AntAction;
import antworld.data.AntData;
import antworld.data.CommData;
import antworld.data.Constants;
import antworld.data.NestNameEnum;
import antworld.data.TeamNameEnum;
import antworld.food.Food;
import antworld.gui.AntLive;
import antworld.info.AntManager;
import antworld.info.FoodManager;

/**
 * The client for AntWorld. The main method in this class should be the run configuration
 * if unit testing is not being done.
 * 
 * @author Troy Squillaci, J. Jake Nichol
 */
public class Client
{
  /** Debugging flag for (mainly) networking issues. */ 
  private static final boolean DEBUG_CLIENT = false;
  
  /** Debugging flag for general issues. */
  private static final boolean DEBUG_GENERAL = true;

  /** The team name to be used when on the server. */
  private static final TeamNameEnum myTeam = TeamNameEnum.Toothachegrass;
  
  /** The password associated with the team. */
  private static final long password = 1039840868147L;
  
  /** The nest associated with the team. */
  private NestNameEnum myNestName = null;

  /** I/O Streams for communication. */
  private ObjectInputStream inputStream = null;
  private ObjectOutputStream outputStream = null;
  
  /** Flag indicating if the client is connected to the server. */
  private boolean isConnected = false;
  
  /** The socket for the connection. */
  private Socket clientSocket;
  
  /** The grid X of the nest. */
  public static int centerX;
  
  /** The grid Y of the nest. */
  public static int centerY;
  
  /** A rectangle contained in the area of the nest. */
  public static Rectangle nestArea;
  
  /** A boundary for the ants. No ants can leave this boundary. */
  public static Rectangle allowedArea;

  /** The AntManager for this game session. */
  private static AntManager antManager;
  
  /** The FoodManager for this game session. */
  private static FoodManager foodManager;

  
  
  /**
   * Instantiates a new Client object with the specified host and port for connecting to a server.
   * 
   * @param host the host for connecting to a server
   * @param portNumber the port for connecting to a server
   */
  public Client(String host, int portNumber)
  {
    System.out.println("Starting Client: " + System.currentTimeMillis());
    isConnected = false;
    while (!isConnected)
    {
      System.out.println("Attempting to connect to " + host + " on port " + portNumber);
      isConnected = openConnection(host, portNumber);
      if (!isConnected) try
      {
        Thread.sleep(1000);
      }
      catch (InterruptedException e1)
      {
      }
    }
    System.out.println("Successfully established a connection to " + host + " on port " + portNumber);
    CommData data = chooseNest();
    mainGameLoop(data);
    closeAll();
  }

  
  
  /**
   * Opens a connection to the server specified by the host name and port.
   * 
   * @param host the host for connecting to a server
   * @param portNumber the port for connecting to a server
   * @return a boolean indicating if the connection was successful
   */
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
      System.err.println("Client Error: Could not open a connection to " + host + " on port " + portNumber);
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

  
  
  /**
   * Gracefully closes the I/O streams and exits the program.
   */
  public void closeAll()
  {
    System.out.println("CLosing I/O streams...");
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
    System.out.println("All I/O streams closed successfully!");
  }

  
  
  /**
   * Chooses a nest from the list of nests and attempts to check it out for the team.
   * 
   * @return the communications data associated with the chosen nest
   */
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

      NestNameEnum requestedNest = NestNameEnum.YELLOW_MEADOW;
      CommData data = new CommData(requestedNest, myTeam);
      data.password = password;

      if (sendCommData(data))
      {
        try
        {
          if (DEBUG_CLIENT) System.out.println("Client: listening to socket....");
          CommData recvData = (CommData) inputStream.readObject();
          if (DEBUG_CLIENT)
            System.out.println("Client: recived <<<<<<<<<" + inputStream.available() + "<...\n" + recvData);

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

  
  
  /**
   * The main game loop.
   * 
   * @param data the communications data received from the server associated with the chosen nest
   */
  public void mainGameLoop(CommData data)
  {
    // Initialize managers
    Client.antManager = new AntManager(data);
    Client.foodManager = new FoodManager(data);

    System.out.println("Ants assigned to collect water: " + Client.antManager.getWaterAnts());

    // Initialize the nest boundary
    Client.nestArea = new Rectangle(Client.centerX - (Constants.NEST_RADIUS / 2), Client.centerY
        - (Constants.NEST_RADIUS / 2), Constants.NEST_RADIUS, Constants.NEST_RADIUS);
    
   Client.allowedArea = new Rectangle(Client.centerX - 400, Client.centerY - 300, 800, 600);
    
   Graph.unwalkableStaticZones.add(new Rectangle(Client.allowedArea.x, Client.allowedArea.y + 525, 150, 75));
   Graph.unwalkableStaticZones.add(new Rectangle(Client.allowedArea.x, Client.allowedArea.y, 350, 150));

    // Create and show the Swing GUI
    AntLive live = new AntLive();
    JFrame frame = new JFrame("Live Ant Statistics");

    WindowListener exitListener = new WindowListener()
    {
      @Override
      public void windowOpened(WindowEvent e)
      {
      }

      @Override
      public void windowClosing(WindowEvent e)
      {
        int confirm = JOptionPane.showOptionDialog(null, "Close Ant World?", "Exit Confirmation",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (confirm == 0)
        {
          closeAll();
          System.exit(0);
        }
      }

      @Override
      public void windowClosed(WindowEvent e)
      {
      }

      @Override
      public void windowIconified(WindowEvent e)
      {
      }

      @Override
      public void windowDeiconified(WindowEvent e)
      {
      }

      @Override
      public void windowActivated(WindowEvent e)
      {
      }

      @Override
      public void windowDeactivated(WindowEvent e)
      {
      }
    };
    frame.addWindowListener(exitListener);

    frame.add(live);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    int i = 0;

    while (true)
    {
      /**********************************************/
      i++;

      if (i % 20 == 0)
      {
        if (Client.DEBUG_GENERAL)
        {
          System.out.println("=================================================================");
          System.out.println("There are (" + data.foodSet.size() + " - " + Client.foodManager.getAllFood().size()
              + ") food packets.");

          int totalCollectors = 0;
          for (Food thisFood : foodManager.getAllFood().values())
          {
            totalCollectors += thisFood.getCollectors().size();
          }

          System.out.println("There are " + totalCollectors + " ants approaching food.");
          System.out.println("=================================================================");
        }
      }
      if (i % 100 == 0)
      {
    	System.out.println("Ant Count: " + data.myAntList.size());
        System.out.println("Food Stockpile: " + Arrays.toString(data.foodStockPile));
        System.out.println("Food Stockpile Total: " + AntUtilities.getTotalFoodCount(data.foodStockPile));

        Ant tmp = null;
        Iterator<Ant> it = Client.antManager.getAllMyAnts().values().iterator();

        while (it.hasNext())
        {
          tmp = it.next();
          if (tmp.getAntData().carryUnits > 0 && tmp.getGathering() == GatheringEnum.FOOD) System.out.println(tmp);
        }
      }
      /**********************************************/
      
      

      try
      {
        if (DEBUG_CLIENT) System.out.println("Client: chooseActions: " + myNestName);

        chooseActionsOfAllAnts(data);

        CommData sendData = data.packageForSendToServer();

        if (DEBUG_CLIENT) System.out.println("ClientRandomWalk: Sending >>>>>>>: " + sendData);
        outputStream.writeObject(sendData);
        outputStream.flush();
        outputStream.reset();

        if (DEBUG_CLIENT) System.out.println("Client: listening to socket....");
        CommData recivedData = (CommData) inputStream.readObject();
        if (DEBUG_CLIENT)
          System.out.println("Client: received <<<<<<<<<" + inputStream.available() + "<...\n" + recivedData);
        data = recivedData;

        Client.antManager.updateAllAnts(data);
        Client.foodManager.updateAllFood(data);

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

  
  
  /**
   * Sends the updated communications data to the server. Occurs on each iteration of the game loop.
   * 
   * @param data the communications data received from the server associated with the chosen nest 
   * @return a boolean indicating if the data was successfully sent to the server
   */
  private boolean sendCommData(CommData data)
  {
    CommData sendData = data.packageForSendToServer();
    try
    {
      if (DEBUG_CLIENT) System.out.println("Client.sendCommData(" + sendData + ")");
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

  
  
  /**
   * Chooses the actions of all of the ants, called during each iteration of the main game loop.
   * 
   * @param data the communications data received from the server associated with the chosen nest 
   */
  private void chooseActionsOfAllAnts(CommData data)
  {
    for (Ant thisAnt : Client.antManager.getAllMyAnts().values())
    {
      AntAction action = thisAnt.chooseAction(data);
      thisAnt.getAntData().myAction.copyFrom(action);
    }
  }

  
  
  /**
   * Used for debugging to verify that references are maintained betweeen the CommData and the AntManager.
   * 
   * @param data the communications data received from the server associated with the chosen nest 
   * @param manager the active AntManager for this game session
   */
  public void printComparisons(CommData data, AntManager manager)
  {
    AntData tmpComm = null;
    int count = 0;
    Iterator<AntData> commIt = data.myAntList.iterator();

    System.out.print("CommData Ants:");

    while (commIt.hasNext() && count < 5)
    {
      count++;
      tmpComm = commIt.next();
      System.out.print(" ID: " + tmpComm.id + " Location: (" + tmpComm.gridX + ", " + tmpComm.gridY + ") Type: "
          + tmpComm.antType + " Health: " + tmpComm.health + " Action: " + tmpComm.myAction);
    }

    System.out.println("\n\n");

    count = 0;

    System.out.print("AntManager Ants:");

    for (Ant value : Client.antManager.getAllMyAnts().values())
    {
      count++;
      if (count > 2) break;
      System.out.print(" ID: " + value.getAntData().id + " Location: (" + value.getAntData().gridX + ", "
          + value.getAntData().gridY + ") Type: " + value.getAntData().antType + " Health: "
          + value.getAntData().health + " Action: " + value.getAntData().myAction);
    }

    System.out.println();
  }

  
  
  //Getter methods.
  /**
   * Gets the active AntManager for the game session.
   * 
   * @return the active AntManager
   */
  public static AntManager getActiveAntManager()
  {
    return Client.antManager;
  }

  /**
   * Gets the active FoodManager for the game session.
   * 
   * @return the active FoodManager
   */
  public static FoodManager getActiveFoodManager()
  {
    return Client.foodManager;
  }

  public static void main(String[] args)
  {
    String host = "b146-69";
    if (args.length > 0) host = args[0];
    new Client(host, Constants.PORT);
  }
}
