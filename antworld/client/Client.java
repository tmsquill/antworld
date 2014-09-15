package antworld.client;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import antworld.ant.Ant;
import antworld.astar.AStarDispatcher;
import antworld.astar.Graph;
import antworld.astar.Location;
import antworld.constants.ActivityEnum;
import antworld.data.AntAction;
import antworld.data.AntData;
import antworld.data.AntType;
import antworld.data.CommData;
import antworld.data.Constants;
import antworld.data.Direction;
import antworld.data.FoodData;
import antworld.data.FoodType;
import antworld.data.NestNameEnum;
import antworld.data.TeamNameEnum;
import antworld.data.AntAction.AntActionType;
import antworld.gui.AntLive;
import antworld.gui2.WorldGUI;
import antworld.info.AntManager;
import antworld.info.FoodManager;

public class Client
{
  private static final boolean      DEBUG_CLIENT  = false;
  private static final boolean      DEBUG_GENERAL = true;

  private static final TeamNameEnum myTeam        = TeamNameEnum.Toothachegrass;
  private static final long         password      = 1039840868147L;
  private ObjectInputStream         inputStream   = null;
  private ObjectOutputStream        outputStream  = null;
  private boolean                   isConnected   = false;
  private NestNameEnum              myNestName    = null;
  public static int                 centerX, centerY;

  private Socket                    clientSocket;

  private static AntManager         antManager;
  private static FoodManager        foodManager;

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

      NestNameEnum requestedNest = NestNameEnum.FIRE;
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

  public void mainGameLoop(CommData data)
  {
    Client.antManager = new AntManager(data);
    Client.foodManager = new FoodManager(data);
    AntLive live = new AntLive();

    JFrame frame = new JFrame("Live Ant Statistics");
    frame.add(live);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    live.update();

    if (Client.DEBUG_GENERAL) System.out.println("Starting GUI...");

    // javafx.application.Application.launch(WorldGUI.class);

    // TODO This is used only for debugging purposes.
    int i = 0;

    while (true)
    {
      // TODO This is used only for debugging purposes.
      /**********************************************/
      i++;

      if (i % 20 == 0)
      {
        if (Client.DEBUG_GENERAL)
        {
          System.out.println("=================================================================");
          System.out.println("There are (" + data.foodSet.size() + " - " + Client.foodManager.getAllFood().size() + 
              " food packets.");
          System.out.println("=================================================================");
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

  private void chooseActionsOfAllAnts(CommData commData)
  {
    for (Ant thisAnt : Client.antManager.getAllMyAnts().values())
    {
      AntAction action = thisAnt.chooseAction(commData);
      thisAnt.getAntData().myAction.copyFrom(action);
    }
  }

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

  // TODO This is for testing the GUI only.
  public static void initManagers()
  {
    CommData data = new CommData(NestNameEnum.ACROBAT, TeamNameEnum.Canarygrass);

    ArrayList<AntData> myAnts = new ArrayList<AntData>();
    AntData a1 = new AntData(213, AntType.ATTACK, NestNameEnum.ACORN, TeamNameEnum.BlueGrama);
    a1.gridX = 1234;
    a1.gridY = 1000;

    AntData a2 = new AntData(214, AntType.DEFENCE, NestNameEnum.ACORN, TeamNameEnum.BlueGrama);
    a2.gridX = 1264;
    a2.gridY = 1104;

    AntData a3 = new AntData(215, AntType.MEDIC, NestNameEnum.ACORN, TeamNameEnum.BlueGrama);
    a3.gridX = 1215;
    a3.gridY = 1045;

    AntData a4 = new AntData(216, AntType.SPEED, NestNameEnum.ACORN, TeamNameEnum.BlueGrama);
    a4.gridX = 1190;
    a4.gridY = 1099;

    AntData a5 = new AntData(217, AntType.VISION, NestNameEnum.ACORN, TeamNameEnum.BlueGrama);
    a5.gridX = 1243;
    a5.gridY = 1111;

    myAnts.add(a1);
    myAnts.add(a2);
    myAnts.add(a3);
    myAnts.add(a4);
    myAnts.add(a5);

    HashSet<AntData> enemyAnts = new HashSet<AntData>();
    AntData b1 = new AntData(576, AntType.BASIC, NestNameEnum.VELVETY_TREE, TeamNameEnum.Lovegrass);
    b1.gridX = 1334;
    b1.gridY = 1100;

    AntData b2 = new AntData(577, AntType.BASIC, NestNameEnum.VELVETY_TREE, TeamNameEnum.Lovegrass);
    b2.gridX = 1364;
    b2.gridY = 1204;

    AntData b3 = new AntData(578, AntType.ATTACK, NestNameEnum.VELVETY_TREE, TeamNameEnum.Lovegrass);
    b3.gridX = 1315;
    b3.gridY = 1145;

    AntData b4 = new AntData(579, AntType.MEDIC, NestNameEnum.VELVETY_TREE, TeamNameEnum.Lovegrass);
    b4.gridX = 1290;
    b4.gridY = 1199;

    AntData b5 = new AntData(580, AntType.CARRY, NestNameEnum.VELVETY_TREE, TeamNameEnum.Lovegrass);
    b5.gridX = 1343;
    b5.gridY = 1211;

    enemyAnts.add(b1);
    enemyAnts.add(b2);
    enemyAnts.add(b3);
    enemyAnts.add(b4);
    enemyAnts.add(b5);

    HashSet<FoodData> food = new HashSet<FoodData>();
    food.add(new FoodData(FoodType.ATTACK, 1400, 235, 50));
    food.add(new FoodData(FoodType.MEDIC, 1410, 214, 50));
    food.add(new FoodData(FoodType.BASIC, 1415, 267, 50));
    food.add(new FoodData(FoodType.VISION, 1420, 246, 50));
    food.add(new FoodData(FoodType.ATTACK, 1425, 232, 50));

    data.myAntList = myAnts;
    data.enemyAntSet = enemyAnts;
    data.foodSet = food;

    Client.antManager = new AntManager(data);
    Client.foodManager = new FoodManager(data);
  }

  // TODO This is for debugging purposes only.
  // public static void initializeActions(WorldGUI gui)
  // {
  // Random rand = new Random();
  //
  // while (true)
  // {
  // try
  // {
  // Thread.sleep(1000);
  // }
  // catch (InterruptedException e1)
  // {
  // }
  //
  // Ant tmp = null;
  // Iterator<Ant> it = Client.antManager.getAllMyAnts().iterator();
  //
  // while (it.hasNext())
  // {
  // tmp = it.next();
  //
  // tmp.antData.gridX = tmp.antData.gridX + rand.nextInt(3) - 1;
  // tmp.antData.gridY = tmp.antData.gridY + rand.nextInt(3) - 1;
  // tmp.updateModel();
  // }
  //
  // Platform.runLater(new Runnable()
  // {
  // public void run()
  // {
  // gui.updateGUI();
  // }
  // });
  // }
  // }

  public static AntManager getActiveAntManager()
  {
    return Client.antManager;
  }

  public static FoodManager getActiveFoodManager()
  {
    return Client.foodManager;
  }

  public static void main(String[] args)
  {
    new Client("phoebe.cs.unm.edu", Constants.PORT);
  }
}
