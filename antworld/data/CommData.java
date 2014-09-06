package antworld.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

// TODO: Auto-generated Javadoc
/**
 * The Class CommData.
 *!!!!!!!!!! DO NOT MODIFY ANYTHING IN THIS CLASS !!!!!!!!!!<br>
 * This class is serialized across a network socket. Any modifications will
 * prevent the server from being able to read this class.<br><br>
 */
public class CommData implements Serializable
{
  private static final long serialVersionUID = Constants.VERSION;

  /** The password. */
  public volatile long password; //Must be assigned number when requesting nest. Must be zero otherwise
  
  /** The wall clock milli sec. */
  public volatile long wallClockMilliSec; //latency debugging
  
  /** The game tick. */
  public volatile int gameTick; // simtime - if received by serv and past this value, data dropped
  
  /** The error msg. */
  public volatile String errorMsg = null;

  /** The my nest. */
  public volatile NestNameEnum myNest;
  
  /** The my team. */
  public volatile TeamNameEnum myTeam;
  
  //To get the location of your own nest, use: 
  //    nestData[myNestName.ordinal()].centerX;
  //    nestData[myNestName.ordinal()].centerY;

  // Return myAntList ordered where the ant's actions are executed from first
  // element to last.
  // Set Ant's action
  // Add ants you want to birth to myAntList.
  /** The my ant list. */
  public volatile ArrayList<AntData> myAntList = new ArrayList<AntData>();

  
  
  // To reduce network traffic, set each of these to null when returning
  // CommData to server
  /** The nest data. */
  public volatile NestData[] nestData; // set to null before sending to server.
  
  /** The food stock pile. */
  public volatile int[] foodStockPile; // set to null before sending to server.
  
  /** The enemy ant set. */
  public volatile HashSet<AntData> enemyAntSet;  // set to null before sending to server.
  
  /** The food set. */
  public volatile HashSet<FoodData> foodSet;  // set to null before sending to server.

  //The server will automatically set requestNestData=true whenever
  //   a new client attaches or whenever a client changes nest homes.
  /** The request nest data. */
  public volatile boolean requestNestData = false;
  
  /** The return to nest on disconnect. */
  public volatile boolean returnToNestOnDisconnect = true;

  /**
   * Instantiates a new comm data.
   *
   * @param nestName the nest name
   * @param team the team
   */
  public CommData(NestNameEnum nestName, TeamNameEnum team)
  {
    this.myNest = nestName;
    this.myTeam = team;
  }
  
  
  
  
  /**
   * Package for socket send to server.
   *  When you construct an ObjectOutputStream and an ObjectInputStream, 
     * they each contain a cache of objects that have already been sent 
     * across this stream. The cache relies on object identity, rather 
     * than the traditional hashing function. It is more similar to a 
     * java.util.IdentityHashMap than a normal java.util.HashMap. So, 
     * if you resend the same object, only a pointer to the object is 
     * sent across the network. This is very clever, and saves network 
     * bandwidth. However, the ObjectOutputStream cannot detect 
     * whether your object was changed internally, resulting in the 
     * Receiver just seeing the same object over and over again. 
     * You will notice that this was quite fast. We sent 1'500'000 
     * objects in 19548ms (on my machine). (well, we only sent one 
     * object, and 1'499'999 pointers to that object). 
     * 
   *
   * @return the comm data
   */
  public CommData packageForSendToServer()
  {
    CommData outData = new CommData(myNest,  myTeam);
    
    outData.wallClockMilliSec = System.currentTimeMillis(); 
    outData.gameTick = gameTick;

    outData.myNest = myNest;
    outData.myTeam =  myTeam;
    outData.password = password;
    outData.errorMsg = null;
    

    outData.myAntList = new ArrayList<AntData>();
    for (AntData ant : myAntList)
    {
      outData.myAntList.add(new AntData(ant));
    }

    outData.nestData = null;
    outData.foodStockPile = null;
    outData.enemyAntSet = null;
    outData.foodSet = null;

    outData.requestNestData = requestNestData;
    outData.returnToNestOnDisconnect = returnToNestOnDisconnect;
    
    return outData;
  }
  
  

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    String out = "CommData["+serialVersionUID+":"+gameTick+":"+wallClockMilliSec+"]: "+ myNest + ", myTeam=" + myTeam+ "\n     ";
    if (errorMsg != null)
    { out = out + "**ERROR**: " + errorMsg + "\n     ";
    }
    
    out = out+ "myAntList:";
    for (AntData ant : myAntList)
    { out = out + "\n     " + ant;
    }
    if (enemyAntSet != null) 
    {  out = out + "\n     enemyAntSet:";
      for (AntData ant : enemyAntSet)
      { out = out + "\n     " + ant;
      }
    }
    return out;
  }
}