package antworld.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import antworld.ant.Ant;
import antworld.astar.Location;
import antworld.constants.ActivityEnum;
import antworld.data.CommData;
import antworld.data.FoodData;
import antworld.data.FoodType;
import antworld.food.Food;
import antworld.gui.FoodCount;

/**
 * This class represents an food management system that is used to track usage and discovery of the food.
 * 
 * @author Troy Squillaci, J. Jake Nichol
 */
public class FoodManager
{
  /** The list of food counts. */
  private ArrayList<FoodCount> foodCount = new ArrayList<FoodCount>();

  /** A HashMap of all food in view of the ants. */
  private HashMap<Location, Food> allFood = new HashMap<Location, Food>();

  /** The set of all world food. */
  private HashSet<FoodData> worldFood;

  /** The total amount of food in the stock pile. */
  private static int totalFood;

  /** The total amount of water in the stock pile */
  private static int totalWater;

  // These values represent the amount of food for each type.
  private int attackFood;
  private int basicFood;
  private int carryFood;
  private int defenseFood;
  private int medicFood;
  private int speedFood;
  private int visionFood;

  /**
   * Instantiates a FoodManager.
   * 
   * @param data CommData
   */
  public FoodManager(CommData data)
  {
    this.worldFood = data.foodSet;

    this.foodCount.add(new FoodCount(FoodType.ATTACK, 0));
    this.foodCount.add(new FoodCount(FoodType.BASIC, 0));
    this.foodCount.add(new FoodCount(FoodType.CARRY, 0));
    this.foodCount.add(new FoodCount(FoodType.DEFENCE, 0));
    this.foodCount.add(new FoodCount(FoodType.MEDIC, 0));
    this.foodCount.add(new FoodCount(FoodType.SPEED, 0));
    this.foodCount.add(new FoodCount(FoodType.UNKNOWN, 0));
    this.foodCount.add(new FoodCount(FoodType.VISION, 0));
    this.foodCount.add(new FoodCount(FoodType.WATER, 0));

    this.updateAllFood(data);
  }

  /**
   * Updates all of the food lists from CommData.
   * 
   * @param data CommData
   */
  public void updateAllFood(CommData data)
  {
    Food tmp = null;

    for (FoodData food : data.foodSet)
    {
      tmp = this.allFood.get(new Location(food.gridX, food.gridY));

      if (tmp != null)
      {
        tmp.setFoodData(food);
      }
      else
      {
        this.allFood.put(new Location(food.gridX, food.gridY), new Food(food));
      }
    }

    Food tmpFood = null;
    Iterator<Food> managerIt = this.allFood.values().iterator();

    while (managerIt.hasNext())
    {
      tmpFood = managerIt.next();
      if (!data.foodSet.contains(tmpFood.getFoodData()))
      {
        for (Ant ant : tmpFood.getCollectors())
        {
          if (ant.getActivity() != ActivityEnum.CARRYING_RESOURCE)
            ant.setActivity(ActivityEnum.SEARCHING_FOR_RESOURCE);
        }
        managerIt.remove();
      }
    }

    this.worldFood = data.foodSet;
  }

  /**
   * Gets the list of all food counts.
   * 
   * @return returns the list of all food counts.
   */
  public List<FoodCount> getFoodCounts()
  {
    return this.foodCount;
  }

  /**
   * Gets the set of world food.
   * 
   * @return returns the set of the world food
   */
  public HashSet<FoodData> getFoodData()
  {
    return this.worldFood;
  }

  /**
   * Gets the HashMap of all food seen by ants.
   * 
   * @return returns the HashMap of all food seen by ants
   */
  public HashMap<Location, Food> getAllFood()
  {
    return this.allFood;
  }

  // Get methods for the count of each ant type.
  public int getAttackFoodCount()
  {
    return this.attackFood;
  }

  public int getBasicFoodCount()
  {
    return this.basicFood;
  }

  public int getCarryFoodCount()
  {
    return this.carryFood;
  }

  public int getDefenseFoodCount()
  {
    return this.defenseFood;
  }

  public int getMedicFoodCount()
  {
    return this.medicFood;
  }

  public int getSpeedFoodCount()
  {
    return this.speedFood;
  }

  public int getVisionFoodCount()
  {
    return this.visionFood;
  }

  public static int getFoodTotal()
  {
    return FoodManager.totalFood;
  }

  public static int getWaterTotal()
  {
    return FoodManager.totalWater;
  }

  // Set methods for the totals.
  public static void setFoodTotal(int total)
  {
    FoodManager.totalFood = total;
  }

  public static void setWaterTotal(int total)
  {
    FoodManager.totalWater = total;
  }
}
