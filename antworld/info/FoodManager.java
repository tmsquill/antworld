package antworld.info;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import antworld.data.AntData;
import antworld.data.CommData;
import antworld.data.FoodData;
import antworld.data.FoodType;
import antworld.exceptions.InvalidAntTypeException;

public class FoodManager
{
	private int attackFood;
	private int basicFood;
	private int carryFood;
	private int defenseFood;
	private int medicFood;
	private int speedFood;
	private int visionFood;

	public FoodManager(CommData data)
	{
		this.updateAllFood(data);
	}
	
	
	
	public void updateAllFood(CommData data)
	{		
		//TODO Can't currently get individual food resources from CommData.
	}

	
	
	//Get methods for the count of each ant type.
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
