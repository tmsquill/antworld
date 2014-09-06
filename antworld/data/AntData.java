package antworld.data;

import java.io.Serializable;
import antworld.data.AntAction.AntActionType;

// TODO: Auto-generated Javadoc
/**
 *!!!!!!!!!! DO NOT MODIFY ANYTHING IN THIS CLASS !!!!!!!!!!<br>
 * This class is serialized across a network socket. Any modifications will
 * prevent the server from being able to read this class.<br><br>
 * 
 * 
 * AntData contains all data about an ant agent that is exchanged between 
 * client and server.
 * 
 */

public class AntData implements Comparable<AntData>, Serializable
{
  private static final long serialVersionUID = Constants.VERSION;

  /** The name of the nest to which this ant belongs. */
  public final NestNameEnum nestName;
  
  /** The name of the team to which this ant belongs. */
  public final TeamNameEnum teamName;

  /** Unique id. When the client uses the birth action to create an ant, the 
   * client should leave this set to the default value of Constants.UNKNOWN_ANT_ID.<br><br>
   * When the server accepts an ant's birth action, the server sets the id to a
   * unique value that monotonically increases with the tick.*/
  public int id = Constants.UNKNOWN_ANT_ID; //Use whenever you birth an ant

  /** World Map pixel coordinates of this ant with (0,0) being upper-left. 
   * In the ant world map, each game object (an ant or a food pile) occupies
   * exactly one pixel. No two game objects may occupy the same pixel at the 
   * same time. NOTE: food being carried by an ant is part of the ant game object.*/
  public int gridX, gridY;
  
  /** alive. */
  public boolean alive = true;

  /** The ant type. */
  public AntType antType;
  
  /** The type of food the ant is carrying. This equals null if the ant is not 
   * carrying any food.*/
  public FoodType carryType = null;
  
  /** The number of food units the ant is carrying. An ant may only carry 
   * one type of food at a time. The maximum number of units an ant can 
   * carry is given by this.antType.getCarryCapacity()
   *  */
  public int carryUnits = 0;

  /** The client sets myAction to tell the server what the ant wants to do.
   * The server returns this set to the action that was actually taken.<br><br>
   * 
   * For example, the server set myAction to STASIS under two conditions:<br>
   * <ol> 
   * <li> This ant attempts a move that is not legal (move onto water, attack 
   * in a direction where there is not an ant, ...)</li>
   * <li> When ticksUntilNextAction is > 0.</li>
   * </ol>
   * 
   * */
  public AntAction myAction;

  /** The ticks until next action. 
   * This is used for actions that take more than one tick to complete. 
   * Currently, the only such action is movement,
   * */
  public int ticksUntilNextAction = 0;

  /** This ant's health. When an ant's health reaches 0, it dies.*/
  public int health;

  /** True when the ant is underground. When an ant is underground, it is in its nest. This means:
   * <ol>
   * <li> The ant can exit its nest any coordinates within its 
   * nest radius that is not occupied by a game object.</li>
   * <li> The ant is totally save: it cannot be attacked and never takes attrition damage.</li>
   * <li> The ant may heal itself this.antType.getHealPointsPerWaterUnit() health point 
   * by consuming one water drop from the nest storage.</li>
   * <li> The ant may attack ants that are above ground in any of the 8 cells 
   * surrounding its underground coordinates.</li>
   * <li> The ant can see above ground cells that are on its nest.</li>
   * </ol>
   * */
  public boolean underground = true;

  /**
   * Instantiates a new ant data.
   *
   * @param id the id (when birthed by the client, this must be Constants.UNKNOWN_ANT_ID).
   * @param type the type
   * @param nestName the nest name
   * @param teamName the team name
   */
  public AntData(int id, AntType type, NestNameEnum nestName, TeamNameEnum teamName)
  {
    this.id = id;
    antType = type;
    this.nestName = nestName;
    this.teamName = teamName;
    health = type.getMaxHealth();
    myAction = new AntAction(AntActionType.BIRTH);
  }
  
  
  /**
   * Instantiates a new ant data by creating a deep copy of the given source ant.
   *
   * @param source the source
   */
  public AntData(AntData source)
  {
    id = source.id;
    nestName = source.nestName;
    teamName = source.teamName;
    
    gridX = source.gridX;
    gridY = source.gridY;
    alive = source.alive;

    antType = source.antType;
    carryType = source.carryType;
    carryUnits = source.carryUnits;

    myAction = new AntAction(source.myAction);

    ticksUntilNextAction = source.ticksUntilNextAction;

    health = source.health;

    underground = source.underground;
  }

  /* 
   * Creates a formatted string showing many of this ant's fields.
   */
  public String toString()
  {
    String out = "AntData: [id=" + id + ", nest=" + nestName + ", team=" + teamName + ", " + antType + ", health="
        + health + ", " + myAction;
    if (carryUnits > 0) out += ", carry: [" + carryType + ", " + carryUnits + "]";
    if (underground) out += ", underground ]";
    else out += ", x=" + gridX + ", y=" + gridY + "]";

    return out;
  }

  /* 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(AntData otherAnt)
  {
    return id - otherAnt.id;
  }

}
