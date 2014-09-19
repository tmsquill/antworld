package antworld.constants;

/**
 * Enumerates the ant aggression levels.
 * 
 * @author Troy Sqillaci, J. Jake Nichol
 */
public enum AggressionEnum
{
  PASSIVE, WHEN_APPROACHED, WHEN_HIT, WHEN_ENEMY_SPOTTED, BALLS_FUCKING_DEEP;

  public static final int SIZE = values().length;
}
