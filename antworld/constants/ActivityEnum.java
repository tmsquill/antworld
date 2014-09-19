package antworld.constants;

/**
 * Enumerates the activities of each ant.
 * 
 * @author Troy Squillaci, J. Jake Nichol
 */
public enum ActivityEnum
{
  RETREATING, CARRYING_RESOURCE, APPROACHING_FOOD, SEARCHING_FOR_RESOURCE;

  public static final int SIZE = values().length;
}
