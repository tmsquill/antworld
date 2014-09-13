package antworld.ant;

import java.util.LinkedList;

import javafx.beans.property.SimpleStringProperty;
import antworld.constants.ActivityEnum;
import antworld.data.AntData;
import antworld.data.Direction;

public class Ant
{
	public AntData antData;
	private int antID;
	private ActivityEnum activity = ActivityEnum.SEARCHING_FOR_FOOD;
  public LinkedList<Direction> directions = new LinkedList<Direction>();  
  
  //Used by the GUI.
  private SimpleStringProperty nest;
  private SimpleStringProperty team;
  private SimpleStringProperty id;
  private SimpleStringProperty x;
  private SimpleStringProperty y;
  private SimpleStringProperty type;
  private SimpleStringProperty carry;
  private SimpleStringProperty health;
  private SimpleStringProperty underground;
  
  public Ant(AntData antData)
  {
    this.antData = antData;
    this.antID = antData.id;
    this.updateModel();
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