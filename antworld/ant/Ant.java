package antworld.ant;

import java.util.LinkedList;

import antworld.constants.ActivityEnum;
import antworld.data.AntData;
import antworld.data.Direction;

public class Ant
{
	public AntData antData;
	private int antID;
	private ActivityEnum activity;
  public LinkedList<Direction> directions = new LinkedList<Direction>();  
  
  public Ant(AntData antData)
  {
    this.antData = antData;
    this.antID = antData.id;
  }
  
  public boolean verifyID()
  {
  	return antID == antData.id ? true : false;
  }
  
  public ActivityEnum getActivity()
  {
    return this.activity;
  }
  
  public Direction getNextDirection()
  {
    return this.directions.poll();
  }
  
  public boolean isDirectionsEmpty()
  {
  	return this.directions.isEmpty();
  }
  
  
  
  public void setActivity(ActivityEnum activity)
  {
  	this.activity = activity;
  }
  
  public void setDirections(LinkedList<Direction> directions)
  {
    this.directions = directions;
  }
}