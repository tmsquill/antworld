package antworld.ant;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javafx.beans.property.SimpleStringProperty;
import antworld.astar.AStarDispatcher;
import antworld.astar.Graph;
import antworld.astar.Location;
import antworld.client.Client;
import antworld.constants.ActivityEnum;
import antworld.constants.GatheringEnum;
import antworld.data.AntAction;
import antworld.data.AntData;
import antworld.data.CommData;
import antworld.data.Constants;
import antworld.data.Direction;
import antworld.data.AntAction.AntActionType;
import antworld.food.Food;

/**
 * This class represents an ant which controls an AntData object. AntData is a raw data set with no decision making,
 * this class adds that functionality.
 * 
 * @author Troy Squillaci, J. Jake Nichol
 */
public class Ant
{
  /** Used to multi-thread A* */
  public static AStarDispatcher astar = new AStarDispatcher();

  /** The AntData controlled by this ant. */
  private AntData antData;

  /** The current activity the ant is performing, used by the AI. */
  private ActivityEnum activity = ActivityEnum.SEARCHING_FOR_RESOURCE;

  /** The current gather type the ant is performing, used y the AI. */
  private GatheringEnum gather = GatheringEnum.FOOD;

  /** The current list of directions the ant is following, used by the AI. */
  private LinkedList<Direction> directions = new LinkedList<Direction>();

  /** Represents a safe zone, if an enemy ant enters the safe zone, the ant flees. */
  private Rectangle safeZone = new Rectangle();

  /** The list of enemy ants in the safe zone. */
  private ArrayList<Point> enemyInSafeZone = new ArrayList<Point>();

  /**
   * Used when food is spotted and the ant is assigned to pick it up, the ant will walk to a pre-determined location
   * around the food and pick it up in the direction specified here.
   */
  private Direction pickupDirection;

  /** The current destination of the ant, used by the AI. */
  private Location destination;

  /** Specifies a random location above the nest for exiting. */
  private static Random random = Constants.random;

  /** Associates the ant with a group by ID. */
  private int groupID = -1;

  /** Counts the number of failed pickup attempts, if a limit is reached the ant tries a different approach. */
  private int pickupAttempts = 0;

  /** Used to override existing directions with a random walk. */
  private boolean forceDirectedWalk = true;

  // Used by the JavaFX GUI.
  private SimpleStringProperty nest;
  private SimpleStringProperty team;
  private SimpleStringProperty id;
  private SimpleStringProperty x;
  private SimpleStringProperty y;
  private SimpleStringProperty type;
  private SimpleStringProperty carry;
  private SimpleStringProperty health;
  private SimpleStringProperty underground;

  /**
   * Instantiates a new Ant object and associates it with the provided AntData.
   * 
   * @param antData an instance of AntData that this ant will manage
   */
  public Ant(AntData antData)
  {
    this.antData = antData;
    this.pickupDirection = Direction.EAST;
    this.updateModel();
  }

  /**
   * The primary AI of the ant. Uses the CommData provided each update to determine the next move.
   * 
   * @param data the latest communications data from the server
   * @return the next action the ant will attempt to take to be sent to the server
   */
  public AntAction chooseAction(CommData data)
  {
    // Default action is STASIS.
    AntAction action = new AntAction(AntActionType.STASIS);

    // The ant cannot perform an action this tick, so return STASIS to avoid computation.
    if (this.getAntData().ticksUntilNextAction > 0) { return action; }

    // Update the safe zone boundary of the ant.
    this.safeZone.setBounds(this.antData.gridX - 30, this.antData.gridY - 30, 60, 60);

    // Check for enemy ants in the safe zone.
    this.enemyInSafeZone.clear();
    AntData tmpEnemy = null;
    Point tmpEnemyPoint = null;
    Iterator<AntData> it = data.enemyAntSet.iterator();

    while (it.hasNext())
    {
      tmpEnemy = it.next();
      tmpEnemyPoint = new Point(tmpEnemy.gridX, tmpEnemy.gridY);
      if (this.safeZone.contains(tmpEnemyPoint))
      {
        this.enemyInSafeZone.add(tmpEnemyPoint);
      }
    }

    // If enemy ants are in the safe zone, then command the ant to change its directions to avoid the enemy ant.
    if (!this.enemyInSafeZone.isEmpty())
    {
      this.directions.clear();
      int averageX = 0;
      int averageY = 0;
      for (Point value : this.enemyInSafeZone)
      {
        averageX = averageX + value.x;
        averageY = averageY + value.y;
      }

      averageX = averageX / this.enemyInSafeZone.size();
      averageY = averageY / this.enemyInSafeZone.size();

      Direction run = AntUtilities.getGeneralDirection(new Location(this.antData.gridX, this.antData.gridY),
          new Location(averageX, averageY));

      run = AntUtilities.getOppositeDirection(run);

      this.directions.push(run);

      action.type = AntActionType.MOVE;
      action.direction = this.getNextDirection();
      return action;
    }

    // If the ant is injured, tell it to retreat to the nest.
    if (this.isInjured() && this.activity != ActivityEnum.RETREATING)
    {
      this.activity = ActivityEnum.RETREATING;

      this.directions.clear();
      this.destination = new Location(Client.centerX, Client.centerY);
      this.directions = Ant.astar.dispatchAStar(this.getCurrentLocation(), this.destination);

      action.type = AntActionType.MOVE;
      action.direction = this.getNextDirection();
      return action;
    }

    // If the ant is retreating and is underground, then heal.
    if (this.activity == ActivityEnum.RETREATING && this.antData.underground)
    {
      action.type = AntActionType.HEAL;
      if (this.antData.health == this.antData.antType.getMaxHealth())
      {
        this.activity = ActivityEnum.SEARCHING_FOR_RESOURCE;
      }
      return action;
    }

    // If the ant is retreating and is on the nest, go underground to heal the next tick.
    if (this.activity == ActivityEnum.RETREATING
        && Client.nestArea.contains(new Point(this.antData.gridX, this.antData.gridY)))
    {
      action.type = AntActionType.ENTER_NEST;
      return action;
    }

    // If the ant is still retreating, continue.
    if (this.activity == ActivityEnum.RETREATING && !this.directions.isEmpty())
    {
      action.type = AntActionType.MOVE;
      action.direction = this.getNextDirection();
      return action;
    }

    // Ensure the ant on its way home if it has food.
    if (this.antData.carryUnits > 0) this.setActivity(ActivityEnum.CARRYING_RESOURCE);

    // If the ant is underground and is not carrying food then exit the nest.
    if (this.activity != ActivityEnum.CARRYING_RESOURCE && this.getAntData().underground)
    {
      action.type = AntActionType.EXIT_NEST;
      action.x = Client.centerX - Constants.NEST_RADIUS + random.nextInt(2 * Constants.NEST_RADIUS);
      action.y = Client.centerY - Constants.NEST_RADIUS + random.nextInt(2 * Constants.NEST_RADIUS);
      return action;
    }

    // If the ant is carrying food and it underground then drop the food in the nest.
    if (this.getActivity() == ActivityEnum.CARRYING_RESOURCE && this.antData.underground)
    {
      this.forceDirectedWalk = true;
      this.pickupAttempts = 0;
      action.type = AntActionType.DROP;
      action.direction = Direction.NORTH;
      action.quantity = this.getAntData().carryUnits;
      this.setActivity(ActivityEnum.SEARCHING_FOR_RESOURCE);
      return action;
    }

    // If the ant is carrying food and on the nest then enter the nest.
    if (this.getActivity() == ActivityEnum.CARRYING_RESOURCE
        && Client.nestArea.contains(new Point(this.antData.gridX, this.antData.gridY)))
    {
      action.type = AntActionType.ENTER_NEST;
      return action;
    }

    // If the ant is carrying food, head home.
    if (this.getActivity() == ActivityEnum.CARRYING_RESOURCE && this.isDirectionsEmpty())
    {
      this.directions.clear();

      if (this.forceDirectedWalk)
      {
        for (int i = 0; i < 10; i++)
          this.directions.push(AntUtilities.getOppositeDirection(pickupDirection));
        this.forceDirectedWalk = false;
      }
      else
      {
        this.destination = new Location(Client.centerX, Client.centerY);
        this.setDirections(astar.dispatchAStar(new Location(this.getAntData().gridX, this.getAntData().gridY),
            destination));
        this.forceDirectedWalk = true;
      }

      action.type = AntActionType.MOVE;
      action.direction = this.getNextDirection();
      return action;
    }

    // If the ants fails to pickup the food more than five times then try something else.
    if (this.pickupAttempts > 5)
    {
      this.activity = ActivityEnum.SEARCHING_FOR_RESOURCE;
      this.pickupAttempts = 0;
    }

    // If the ant is at the food, pick it up.
    if (this.getActivity() == ActivityEnum.APPROACHING_FOOD && this.isDirectionsEmpty())
    {
      this.pickupAttempts++;
      action.type = AntActionType.PICKUP;
      action.direction = this.pickupDirection;
      action.quantity = (this.antData.antType.getCarryCapacity() / 2) - 1;
      return action;
    }

    if ((Graph.isWater(new Location(this.antData.gridX - 1, this.antData.gridY - 1))
        || Graph.isWater(new Location(this.antData.gridX, this.antData.gridY - 1)) || Graph.isWater(new Location(
        this.antData.gridX - 1, this.antData.gridY)))
        && this.antData.carryUnits == 0
        && this.gather == GatheringEnum.WATER)
    {
      this.directions.clear();
      action.type = AntActionType.PICKUP;
      action.direction = Direction.SOUTHWEST;
      action.quantity = 24;
      return action;
    }

    // If food is around and the ant is not working on getting it, have them
    // get
    // it.
    if (!data.foodSet.isEmpty() && this.getActivity() == ActivityEnum.SEARCHING_FOR_RESOURCE
        && this.gather == GatheringEnum.FOOD)
    {
      // Loop through food objects and see if any of them need more
      // collectors.
      // If a food object needs another collector, assign this ant to it.
      for (Food value : Client.getActiveFoodManager().getAllFood().values())
      {
        if (value.getCollectors().size() <= 2
            && AntUtilities.manhattanDistance(this.antData.gridX, this.antData.gridY, value.getFoodData().gridX,
                value.getFoodData().gridY) < this.antData.antType.getVisionRadius() * 6)
        {
          this.directions.clear();
          this.destination = value.addCollector(this);

          this.setDirections(astar.dispatchAStar(new Location(this.antData.gridX, this.antData.gridY), destination));
          this.setActivity(ActivityEnum.APPROACHING_FOOD);
          action.type = AntActionType.MOVE;
          action.direction = this.getNextDirection();
          return action;
        }
        else if (value.isFull()
            || AntUtilities.manhattanDistance(this.antData.gridX, this.antData.gridY, value.getFoodData().gridX,
                value.getFoodData().gridY) > this.antData.antType.getVisionRadius() * 4) continue;
        else
        {
          this.directions.clear();
          this.destination = value.addCollector(this);

          this.setDirections(astar.dispatchAStar(new Location(this.antData.gridX, this.antData.gridY), destination));
          this.setActivity(ActivityEnum.APPROACHING_FOOD);
          action.type = AntActionType.MOVE;
          action.direction = this.getNextDirection();
          return action;
        }
      }

      if (this.directions.isEmpty()) this.assignRandomDirections(100);
      action.type = AntActionType.MOVE;
      action.direction = this.getNextDirection();
      return action;
    }

    // Ant is looking for a resource.
    if (this.getActivity() == ActivityEnum.SEARCHING_FOR_RESOURCE && this.isDirectionsEmpty())
    {
      if (this.gather == GatheringEnum.FOOD)
      {
        this.assignRandomDirections(100);

        action.type = AntActionType.MOVE;
        action.direction = this.getNextDirection();
        return action;
      }
      else
      {
        if (this.antData.gridX < 1060 && this.antData.gridY > 1010)
        {
          Direction tmp = null;
          int i = random.nextInt(3);
          if (i == 0) tmp = Direction.WEST;
          else if (i == 1) tmp = Direction.SOUTHWEST;
          else if (i == 2) tmp = Direction.SOUTH;

          this.directions.push(tmp);
          action.type = AntActionType.MOVE;
          action.direction = this.getNextDirection();
          return action;
        }
        else
        {
          this.setDestination(new Location(1055, 1015));
          this.setDirections(astar.dispatchAStar(new Location(this.antData.gridX, this.antData.gridY), destination));
          action.type = AntActionType.MOVE;
          action.direction = this.getNextDirection();
          return action;
        }
      }
    }

    else
    {
      action.type = AntActionType.MOVE;

      Direction next = this.directions.peek();

      for (Ant ant : Client.getActiveAntManager().getAllMyAnts().values())
      {
        if (next != null)
        {
          if (this.antData.gridX + next.deltaX() == ant.getAntData().gridX
              && this.antData.gridY + next.deltaY() == ant.getAntData().gridY)
          {
            Direction tmp = Direction.getRandomDir();
            this.directions.push(tmp);
            this.directions.push(AntUtilities.getOppositeDirection(tmp));
          }
        }
      }

      action.direction = this.getNextDirection();
      return action;
    }
  }

  /**
   * Assigns random directions to the current ant. Used when searching for food and can also be forced by the user from
   * the GUI
   * 
   * @param distance the number of steps to take in the random direction
   */
  public void assignRandomDirections(int distance)
  {
    Point randomDestination = new Point();

    do
    {
      switch (Direction.getRandomDir())
      {
        case NORTH:
          randomDestination.x = this.getAntData().gridX;
          randomDestination.y = this.getAntData().gridY - distance;
          break;
        case NORTHEAST:
          randomDestination.x = this.getAntData().gridX + distance;
          randomDestination.y = this.getAntData().gridY - distance;
          break;
        case EAST:
          randomDestination.x = this.getAntData().gridX + distance;
          randomDestination.y = this.getAntData().gridY;
          break;
        case SOUTHEAST:
          randomDestination.x = this.getAntData().gridX + distance;
          randomDestination.y = this.getAntData().gridY + distance;
          break;
        case SOUTH:
          randomDestination.x = this.getAntData().gridX;
          randomDestination.y = this.getAntData().gridY + distance;
          break;
        case SOUTHWEST:
          randomDestination.x = this.getAntData().gridX - distance;
          randomDestination.y = this.getAntData().gridY + distance;
          break;
        case WEST:
          randomDestination.x = this.getAntData().gridX - distance;
          randomDestination.y = this.getAntData().gridY;
          break;
        case NORTHWEST:
          randomDestination.x = this.getAntData().gridX - distance;
          randomDestination.y = this.getAntData().gridY - distance;
          break;
        default:
          System.out.println("There a big problem here...");
          break;
      }
    }
    while (Graph.isWalkable(new Location(randomDestination.x, randomDestination.y)) == 'X');

    this.destination = new Location(randomDestination.x, randomDestination.y);
    this.setDirections(astar.dispatchAStar(new Location(this.getAntData().gridX, this.getAntData().gridY), destination));
  }

  // TODO
  public void retreatToNest()
  {
    directions.clear();

    Location antPosition = new Location(this.getAntData().gridX, this.getAntData().gridY);
    Location nestPosition = new Location(Client.centerX, Client.centerY);

    this.setDirections(astar.dispatchAStar(antPosition, nestPosition));
    this.setActivity(ActivityEnum.RETREATING);

    this.antData.myAction.type = AntActionType.MOVE;
    this.antData.myAction.direction = this.getNextDirection();
  }

  // TODO
  public void retreatToLocation(Location end)
  {
    directions.clear();

    Location antPosition = new Location(this.getAntData().gridX, this.getAntData().gridY);
    end = new Location(Client.centerX, Client.centerY);

    this.setDirections(astar.dispatchAStar(antPosition, end));
    this.setActivity(ActivityEnum.RETREATING);

    this.antData.myAction.type = AntActionType.MOVE;
    this.antData.myAction.direction = this.getNextDirection();
  }

  // Getter methods.
  /**
   * Gets the AntData managed by this ant object.
   * 
   * @return the AntData associated with this ant object
   */
  public AntData getAntData()
  {
    return this.antData;
  }

  /**
   * Gets the current activity type of the ant.
   * 
   * @return the current activity type of the ant
   */
  public ActivityEnum getActivity()
  {
    return this.activity;
  }

  /**
   * Gets the current gathering type of the ant.
   * 
   * @return the current gather type of the ant
   */
  public GatheringEnum getGathering()
  {
    return this.gather;
  }

  /**
   * Gets the location of the current ant.
   * 
   * @return the location of the current ant
   */
  public Location getCurrentLocation()
  {
    return new Location(this.antData.gridX, this.antData.gridY);
  }

  /**
   * Gets the current destination of the ant.
   * 
   * @return the current destination of the ant
   */
  public Location getDestination()
  {
    return this.destination;
  }

  /**
   * Gets the current list of directions.
   * 
   * @return the current list of directions
   */
  public LinkedList<Direction> getDirections()
  {
    return this.directions;
  }

  /**
   * Gets the next direction from the list of directions. The direction obtained this was is removed from the list of
   * directions.
   * 
   * @return the next direction to be performed by the ant
   */
  public Direction getNextDirection()
  {
    return this.directions.poll();
  }

  /**
   * Gets the pickup direction of the ant.
   * 
   * @return the pickup direction of the ant
   */
  public Direction getPickupDirection()
  {
    return this.pickupDirection;
  }

  /**
   * Gets the ID of the group that the ant is associated with.
   * 
   * @return the group ID associated with the ant
   */
  public int getGroupID()
  {
    return this.groupID;
  }

  // Setter methods.
  /**
   * Sets the AntData to be associated with this ant object.
   * 
   * @param data the AntData to associate with this ant object
   */
  public void setAntData(AntData data)
  {
    this.antData = data;
  }

  /**
   * Sets the gathering type of the ant.
   * 
   * @param activity the activity type to associate with this ant
   */
  public void setActivity(ActivityEnum activity)
  {
    this.activity = activity;
  }

  /**
   * Sets the gathering type of the ant.
   * 
   * @param gather the gathering type to associate with this ant
   */
  public void setGathering(GatheringEnum gather)
  {
    this.gather = gather;
  }

  /**
   * Sets the destination of the ant to the specified location.
   * 
   * @param destination the desired destination of the ant
   */
  public void setDestination(Location destination)
  {
    this.destination = destination;
  }

  /**
   * Sets the directions of the ant.
   * 
   * @param directions a list of directions
   */
  public void setDirections(LinkedList<Direction> directions)
  {
    this.directions = directions;
  }

  /**
   * Sets the pickup direction of the ant.
   * 
   * @param direction the pickup direction
   */
  public void setPickupDirection(Direction direction)
  {
    this.pickupDirection = direction;
  }

  /**
   * Sets the group ID of the ant.
   * 
   * @param groupID the ID of the group to associate with this ant
   */
  public void setGroupID(int groupID)
  {
    this.groupID = groupID;
  }

  // Is Methods
  /**
   * Determines if the ant is encumbered.
   * 
   * @return a boolean indicating if the ant is encumbered
   */
  public boolean isEncumbered()
  {
    return this.antData.carryUnits > ((this.antData.antType.getCarryCapacity() / 2) - 1) ? true : false;
  }

  /**
   * Determines if the ant is injured.
   * 
   * @return a boolean indicating if the ant is injured
   */
  public boolean isInjured()
  {
    return this.antData.health < (this.antData.antType.getMaxHealth() - 5) ? true : false;
  }

  /**
   * Determines if the ant is in a group.
   * 
   * @return a boolean indicating if the ant is in a group
   */
  public boolean isInGroup()
  {
    return this.groupID > 0 ? true : false;
  }

  /**
   * Determines if the direction list of the ant is empty.
   * 
   * @return a boolean indicating if the direction list of the ant is empty
   */
  public boolean isDirectionsEmpty()
  {
    return this.directions.isEmpty();
  }

  /*************************************************************************/
  // Methods for the JavaFX GUI. Currently unused because the Swing GUI is being used.
  public void updateModel()
  {
    this.nest = new SimpleStringProperty(this.antData.nestName.toString());
    this.team = new SimpleStringProperty(this.antData.teamName.toString());
    this.id = new SimpleStringProperty(Integer.toString(this.antData.id));
    this.x = new SimpleStringProperty(Integer.toString(this.antData.gridX));
    this.y = new SimpleStringProperty(Integer.toString(this.antData.gridY));
    this.type = new SimpleStringProperty(this.antData.antType.toString());
    this.carry = new SimpleStringProperty(Integer.toString(this.antData.carryUnits));
    this.health = new SimpleStringProperty(Integer.toString(this.antData.health));
    this.underground = new SimpleStringProperty(Boolean.toString(this.antData.underground));
  }

  public String getNest()
  {
    return nest.get();
  }

  public String getTeam()
  {
    return team.get();
  }

  public String getId()
  {
    return id.get();
  }

  public String getX()
  {
    return x.get();
  }

  public String getY()
  {
    return y.get();
  }

  public String getType()
  {
    return type.get();
  }

  public String getCarry()
  {
    return carry.get();
  }

  public String getHealth()
  {
    return health.get();
  }

  public String getUnderground()
  {
    return underground.get();
  }

  @Override
  public String toString()
  {
    return "Ant: " + this.antData.id + " Gather: " + this.gather + " Activity: " + this.activity + " Carry Units: "
        + this.antData.carryUnits + " Carry Type: " + this.antData.carryType;
  }
}