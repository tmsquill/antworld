package antworld.data;
/**
 *!!!!!!!!!! DO NOT MODIFY ANYTHING IN THIS CLASS !!!!!!!!!!<br>
 * This class is serialized across a network socket. Any modifications will
 * prevent the server from being able to read this class.<br><br>
 */


public enum AntType
{
  DEFENCE
  { public FoodType getBirthFoodType() {return FoodType.DEFENCE;}
    public int getMaxHealth() {return super.getMaxHealth()*2;}
    public int getHealPointsPerWaterUnit() {return super.getHealPointsPerWaterUnit()*2;}
  }, 
  
  ATTACK 
  { public FoodType getBirthFoodType() {return FoodType.ATTACK;}
    public int getMaxAttackDamage() {return super.getMaxAttackDamage()*2;}
  },  
  
  SPEED
  { public FoodType getBirthFoodType() {return FoodType.SPEED;}
    public int getBaseMovementTicksPerCell() {return super.getBaseMovementTicksPerCell()/2;}
  }, 
  
  VISION
  { public FoodType getBirthFoodType() {return FoodType.VISION;}
    public int getVisionRadius() {return super.getVisionRadius()*2;}
  },
  
  CARRY
  { public FoodType getBirthFoodType() {return FoodType.CARRY;}
    public int getCarryCapacity() {return super.getCarryCapacity()*2;}
  },
  
  MEDIC    
  { public FoodType getBirthFoodType() {return FoodType.MEDIC;}
  },
  
  BASIC
  { public FoodType getBirthFoodType() {return FoodType.BASIC;}
  };
  
  
  //==========================================================================
  public int getFoodUnitsToSpawn() {return 100;} //100
  public abstract FoodType getBirthFoodType();
  public int getMaxHealth() {return 20;}
  public int getMaxAttackDamage() {return 6;} //actual damage is 1 through getMaxAttackDamage()
  public double getAttritionDamageProbability() {return 0.0001;}
  public int getBaseMovementTicksPerCell() {return 2;}
  public int getUpHillMultiplier() {return 5;}
  public int getHalfEncumbranceMultiplier() {return 2;}
  
  public int getVisionRadius() {return 30;}
  public int getCarryCapacity() {return 50;}
  public int getFoodUnitsOfDeadBody() {return 5;}
  public int getHealPointsPerWaterUnit() {return 1;}
  

}