package antworld.ant;

import java.awt.Point;
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

public class Ant
{
  /** Used to multi-thread A* */
  public static AStarDispatcher astar      = new AStarDispatcher();

  /** The AntData controlled by this ant */
  private AntData                antData;

  /** The current activity the ant is performing, used by the AI */
  private ActivityEnum           activity   = ActivityEnum.SEARCHING_FOR_RESOURCE;
  
  /** The current gather type the ant is performing, used y the AI */
  private GatheringEnum          gather = GatheringEnum.FOOD;

  /** The current list of directions the ant is following, used by the AI */
  private LinkedList<Direction>  directions = new LinkedList<Direction>();

  /**
   * Used when food is spotted and the ant is assigned to pick it up, the ant
   * will walk to a pre-determined location around the food and pick it up in
   * the direction specified here
   */
  private Direction              pickupDirection;

  /** The current destination of the ant, used by the AI */
  private Location               destination;

  /** Specifies a random location above the nest for exiting */
  private static Random          random     = Constants.random;

  /** Associates the ant with a group by ID */
  private int                    groupID    = -1; 
  
  private int pickupAttempts = 0;
  
  private boolean forceDirectedWalk = true;

  // Used by the JavaFX GUI.
  private SimpleStringProperty   nest;
  private SimpleStringProperty   team;
  private SimpleStringProperty   id;
  private SimpleStringProperty   x;
  private SimpleStringProperty   y;
  private SimpleStringProperty   type;
  private SimpleStringProperty   carry;
  private SimpleStringProperty   health;
  private SimpleStringProperty   underground;

  /**
   * Instantiates a new Ant object and associates it with the provided AntData.
   * 
   * @param antData
   *          an instance of AntData that this ant will manage
   */
  public Ant(AntData antData)
  {
    this.antData = antData;
    this.pickupDirection = Direction.EAST;
    this.updateModel();
  }

  /**
   * The primary AI of the ant. Uses the CommData provided each update to
   * determine the next move.
   * 
   * @param data
   *          the latest communications data from the server
   * @return the next action the ant will attempt to take to be sent to the
   *         server
   */
  public AntAction chooseAction(CommData data)
  {
    // Default action is STASIS.
    AntAction action = new AntAction(AntActionType.STASIS);

    // The ant cannot perform an action this tick, so return STASIS to avoid
    // computation.
    if (this.getAntData().ticksUntilNextAction > 0)
    { 
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
    if (this.activity == ActivityEnum.RETREATING && 
        Client.nestArea.contains(new Point(this.antData.gridX, this.antData.gridY)))
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

    //If the ant is underground and is not carrying food then exit the nest.
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
        for (int i = 0; i < 10; i++) this.directions.push(AntUtilities.getOppositeDirection(pickupDirection));
        this.forceDirectedWalk = false;
      }
      else
      {
        this.destination = new Location(Client.centerX, Client.centerY);
        this.setDirections(astar.dispatchAStar(new Location(this.getAntData().gridX, this.getAntData().gridY), destination));
        this.forceDirectedWalk = true;
      }

      action.type = AntActionType.MOVE;
      action.direction = this.getNextDirection();
      return action;
    }
    
    if (this.pickupAttempts > 5)
    {
      System.out.println("Some dumb dumb can't pick up food.");
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
    
    if (Graph.isWater(new Location(this.antData.gridX - 1, this.antData.gridY)) && this.antData.carryUnits == 0 
        && this.gather == GatheringEnum.WATER)
    {
      this.directions.clear();
      action.type = AntActionType.PICKUP;
      action.direction = Direction.WEST;
      action.quantity = 24;
      return action;
    }

    // If food is around and the ant is not working on getting it, have them get it.
    if (!data.foodSet.isEmpty() && this.getActivity() == ActivityEnum.SEARCHING_FOR_RESOURCE && this.gather == GatheringEnum.FOOD)
    {
      // Loop through food objects and see if any of them need more collectors.
      // If a food object needs another collector, assign this ant to it.
      for (Food value : Client.getActiveFoodManager().getAllFood().values())
      {
        if (value.isFull() || AntUtilities.manhattanDistance(this.antData.gridX, this.antData.gridY, value.getFoodData().gridX,
            value.getFoodData().gridY) > this.antData.antType.getVisionRadius() * 3) continue;
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

    // Ant is looking for resource.
    if (this.getActivity() == ActivityEnum.SEARCHING_FOR_RESOURCE && this.isDirectionsEmpty())
    {
      if (this.gather == GatheringEnum.FOOD)
      {
        this.directions.clear();
        this.assignRandomDirections(100);

        action.type = AntActionType.MOVE;
        action.direction = this.getNextDirection();
        return action;
      }
      else
      {
        Direction tmp = null;
        int i = random.nextInt(3);
        if (i == 0) tmp = Direction.NORTHWEST;
        if (i == 1) tmp = Direction.WEST;
        if (i == 2) tmp = Direction.SOUTHWEST;

        this.directions.push(tmp);
        action.type = AntActionType.MOVE;
        action.direction = this.getNextDirection();
        return action;
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
    while (Graph.calcWeight(new Location(randomDestination.x, randomDestination.y)) == 'X');

    this.destination = new Location(randomDestination.x, randomDestination.y);
    this.setDirections(astar.dispatchAStar(new Location(this.getAntData().gridX, this.getAntData().gridY), destination));
  }
  
  

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

  public Location getCurrentLocation()
  {
    return new Location(this.antData.gridX, this.antData.gridY);
  }

  // get methods
  public LinkedList<Direction> getDirections()
  {
    return this.directions;
  }
  
  public Location getDestination()
  {
    return this.destination;
  }
  
  public int getGroupID()
  {
    return this.groupID;
  }

  public GatheringEnum getGathering()
  {
    return this.gather;
  }
  
  public ActivityEnum getActivity()
  {
    return this.activity;
  }

  public Direction getNextDirection()
  {
    return this.directions.poll();
  }

  public AntData getAntData()
  {
    return this.antData;
  }

  // set methods  
  public void setDestination(Location destination)
  {
    this.destination = destination;
  }
  
  public void setGroupID(int groupID)
  {
    this.groupID = groupID;
  }

  public void setAntData(AntData data)
  {
    this.antData = data;
  }
  
  public void setGathering(GatheringEnum gather)
  {
    this.gather = gather;
  }

  public void setActivity(ActivityEnum activity)
  {
    this.activity = activity;
  }

  public void setDirections(LinkedList<Direction> directions)
  {
    this.directions = directions;
  }

  // is methods
  public boolean isEncumbered()
  {
    return this.antData.carryUnits > ((this.antData.antType.getCarryCapacity() / 2) - 1) ? true : false;
  }

  public boolean isInjured()
  {
    return this.antData.health < (this.antData.antType.getMaxHealth() / 2) ? true : false;
  }

  public boolean isInGroup()
  {
    return this.groupID > 0 ? true : false;
  }

  public boolean isDirectionsEmpty()
  {
    return this.directions.isEmpty();
  }

  public Direction getPickupDirection()
  {
    return this.pickupDirection;
  }

  public void setPickupDirection(Direction dir)
  {
    this.pickupDirection = dir;
  }

  /*************************************************************************/
  /* GUI Methods */
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
    return "Ant: " + this.antData.id + " Gather: " + this.gather + " Activity: " + this.activity + " Carry Units: " +
            this.antData.carryUnits + " Carry Type: " + this.antData.carryType;
  }
}