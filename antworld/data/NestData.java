package antworld.data;

import java.io.Serializable;

public class NestData implements Serializable 
{
  private static final long serialVersionUID = Constants.VERSION;

  public final NestNameEnum nestName;
  public final int centerX, centerY;
  public TeamNameEnum team;
  
  public int score;
  
  public NestData(NestNameEnum nestName, TeamNameEnum teamName, int x, int y)
  { 
    this.nestName = nestName;
    this.team = teamName;
    this.centerX = x;
    this.centerY = y;
    score = 0;
  }
  
  public String toString()
  {
     return "nestData=[nest="+nestName+", team="+team+", center=("+centerX+", "+centerY+") score="+score+"]"; 
  }
}
