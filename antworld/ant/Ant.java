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
import antworld.data.AntAction;
import antworld.data.AntData;
import antworld.data.CommData;
import antworld.data.Constants;
import antworld.data.Direction;
import antworld.data.FoodData;
import antworld.data.AntAction.AntActionType;
import antworld.food.Food;

public class Ant
{
  private static final boolean   DEBUG_AI   = false;

  private static Random          random     = Constants.random;

  private AntData                antData;
  private int                    antID;
  private int                    groupID    = -1;   // -1 signifies no group.

  private Direction pickupDirection;
  private static AStarDispatcher astar      = new AStarDispatcher();
  AntAction                      action     = new AntAction(AntActionType.STASIS);

  private ActivityEnum           activity   = ActivityEnum.SEARCHING_FOR_FOOD;
  public LinkedList<Direction>   directions = new LinkedList<Direction>();

  // Used by the GUI.
  private SimpleStringProperty   nest;
  private SimpleStringProperty   team;
  private SimpleStringProperty   id;
  private SimpleStringProperty   x;
  private SimpleStringProperty   y;
  private SimpleStringProperty   type;
  private SimpleStringProperty   carry;
  private SimpleStringProperty   health;
  private SimpleStringProperty   underground;

  public Ant(AntData antData)
  {
    this.antData = antData;
    this.antID = antData.id;
    this.updateModel();
  }

  public AntAction chooseAction(CommData data)
  {
    // If ant is in Group, then ignore this.
    // Check if need to retreat.
    // Check ActivityEnum for current activity.
    // If ActivityEnum is not IDLE, carry out the next activity.
    // If ActivityEnum is IDLE then reference PriorityEnum for next activity.
    // Check PriorityEnum for current priority.
    // If PriorityEnum is not IDLE, carry out the priority.
    // If PriorityEnum is IDLE then determine next priority.

    if (DEBUG_AI) System.out.println("Getting the action of ant " + this.getAntData().id);

    AntAction action = new AntAction(AntActionType.STASIS);

    if (this.getAntData().ticksUntilNextAction > 0)
    {
      if (DEBUG_AI) System.out.println("Ant unable to move, returning STASIS...");
      return action;
    }

    if (this.getAntData().underground)
    {
      action.type = AntActionType.EXIT_NEST;
      action.x = Client.centerX - Constants.NEST_RADIUS + random.nextInt(2 * Constants.NEST_RADIUS);
      action.y = Client.centerY - Constants.NEST_RADIUS + random.nextInt(2 * Constants.NEST_RADIUS);
      if (DEBUG_AI) System.out.println("Ant is underground, bringing it above ground...");
      return action;
    }

    if (this.getAntData().carryUnits > 0) this.setActivity(ActivityEnum.CARRYING_FOOD);

    // If the ant is at the food, pick it up.
    if (this.getActivity() == ActivityEnum.APPROACHING_FOOD && this.isDirectionsEmpty())
    {
      action.type = AntActionType.PICKUP;
      action.direction = this.pickupDirection;
      action.quantity = 24;
      if (DEBUG_AI) System.out.println("Ant is at food, trying to pick it up...");
      return action;
    }

    // If food is around and the ant is not working on getting it, have them get
    // it.
    else if (!data.foodSet.isEmpty() && this.getActivity() == ActivityEnum.SEARCHING_FOR_FOOD)
    {
      //Loop through food objects and see if any of them need more collectors.
    	//If a food object needs another collector, assign this ant to it.
      for (Food value : Client.getActiveFoodManager().getAllFood().values())
      {
        if (value.isFull()) continue;
        else
        {
          this.directions.clear();

          this.setDirections(astar.dispatchAStar(new Location(this.antData.gridX, this.antData.gridY), value.addCollector(this)));
          this.setActivity(ActivityEnum.APPROACHING_FOOD);
          action.type = AntActionType.MOVE;
          action.direction = this.getNextDirection();
          if (DEBUG_AI) System.out.println("Ant found food, setting course for it...");
          return action;
        }
      }
      
      if (this.directions.isEmpty()) this.assignRandomDirections();
      action.type = AntActionType.MOVE;
      action.direction = this.getNextDirection();
      if (DEBUG_AI) System.out.println("Food is seen and the ant is searching, but other ants are gathering...");
      return action;
    }

    // If the ant is carrying food, head home.
    else if (this.getActivity() == ActivityEnum.CARRYING_FOOD && this.isDirectionsEmpty())
    {
      this.setDirections(astar.dispatchAStar(new Location(this.getAntData().gridX, this.getAntData().gridY),
          (new Location(Client.centerX, Client.centerY))));

      action.type = AntActionType.MOVE;
      action.direction = this.getNextDirection();
      if (DEBUG_AI) System.out.println("Ant just picked up food and is heading home...");
      return action;
    }

    // If the ant is carrying food and on the nest then drop the food.
    else if (this.getActivity() == ActivityEnum.CARRYING_FOOD && this.isDirectionsEmpty())
    {
      action.type = AntActionType.DROP;
      action.direction = Direction.NORTH;
      action.quantity = this.getAntData().carryUnits;
      if (DEBUG_AI) System.out.println("Ant is home and dropping food on nest...");
      return action;
    }

    // If the ant is not carrying or has found food, look for food.
    else if (this.getActivity() == ActivityEnum.SEARCHING_FOR_FOOD && this.isDirectionsEmpty())
    {
      this.assignRandomDirections();

      action.type = AntActionType.MOVE;
      action.direction = this.getNextDirection();
      if (DEBUG_AI) System.out.println("Ant is looking for food via random walk...");
      return action;
    }

    else
    {
      action.type = AntActionType.MOVE;
      action.direction = this.getNextDirection();
      if (DEBUG_AI) System.out.println("Ant is moving to the " + action.direction + "...");
      return action;
    }
  }
  
  public void assignRandomDirections()
  {
    Point randomDestination = new Point();

    int distance = 100;

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

    this.setDirections(astar.dispatchAStar(new Location(this.getAntData().gridX, this.getAntData().gridY), new Location(
        randomDestination.x, randomDestination.y)));
  }

  public void retreatToNest()
  {
    directions.clear();

    Location antPosition = new Location(this.getAntData().gridX, this.getAntData().gridY);
    Location nestPosition = new Location(Client.centerX, Client.centerY);

    this.setDirections(astar.dispatchAStar(antPosition, nestPosition));
    this.setActivity(ActivityEnum.RETREATING);

    action.type = AntActionType.MOVE;
    action.direction = this.getNextDirection();
  }

  public void retreatToLocation(Location end)
  {
    directions.clear();

    Location antPosition = new Location(this.getAntData().gridX, this.getAntData().gridY);
    end = new Location(Client.centerX, Client.centerY);

    this.setDirections(astar.dispatchAStar(antPosition, end));
    this.setActivity(ActivityEnum.RETREATING);

    action.type = AntActionType.MOVE;
    action.direction = this.getNextDirection();
  }

  // get methods
  public int getAntID()
  {
    return this.antID;
  }

  public int getGroupID()
  {
    return this.groupID;
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
  public void setGroupID(int groupID)
  {
    this.groupID = groupID;
  }

  public void setAntData(AntData data)
  {
    this.antData = data;
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
    return this.antData.health < (this.antData.antType.getMaxHealth() / 4) ? true : false;
  }

  public boolean verifyID()
  {
    return antID == antData.id ? true : false;
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
}