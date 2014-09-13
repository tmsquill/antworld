package antworld.info;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import antworld.data.AntData;
import antworld.data.CommData;
import antworld.data.FoodData;
import antworld.data.FoodType;
import antworld.exceptions.InvalidAntTypeException;
import antworld.gui.FoodCount;

public class FoodManager
{
  private ArrayList<FoodCount> foodCount = new ArrayList<FoodCount>();
  private HashSet<FoodData>    worldFood;

  private int                  attackFood;
  private int                  basicFood;
  private int                  carryFood;
  private int                  defenseFood;
  private int                  medicFood;
  private int                  speedFood;
  private int                  visionFood;

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

  public void updateAllFood(CommData data)
  {
    // TODO Can't currently get individual food resources from CommData.
  }

  public List<FoodCount> getFoodCounts()
  {
    return this.foodCount;
  }

  public HashSet<FoodData> getFoodData()
  {
    return this.worldFood;
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
}
