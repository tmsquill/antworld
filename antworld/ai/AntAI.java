package antworld.ai;

import java.awt.Point;

import antworld.data.AntAction;

public abstract class AntAI
{
  public AntAction chooseAction()
  {
    //If ant is in Group, then ignore this.
    //Check if need to retreat.
    //Check ActivityEnum for current activity.
      //If ActivityEnum is not IDLE, carry out the next activity.
      //If ActivityEnum is IDLE then reference PriorityEnum for next activity.
    //Check PriorityEnum for current priority.
      //If PriorityEnum is not IDLE, carry out the priority.
      //If PriorityEnum is IDLE then determine next priority.
    
    return null;
  }
  
  public abstract void retreatToBase();

  public abstract void retreatToLocation();

  public abstract boolean isEncumbered();

  public abstract boolean isInjured();

  public abstract void setDestination(Point destination);
}
