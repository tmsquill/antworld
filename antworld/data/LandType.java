package antworld.data;

// TODO: Auto-generated Javadoc
/**
 * The Enum LandType.
 */
public enum LandType
{
  
  /** The nest. */
  NEST
  { public int getMapColor() {return 0xF0E68C;}
  },
  
  /** The grass. */
  GRASS
  { 
    //Note: grass land only uses the green color channel.
    //   Thus, the client AI can use the red and blue channels to store
    //   other data, such as something that takes the role of a pheromone trail
    public int getMapColor() {return 0x283724;}
    public int getMapHeight(int rgb)
    {
      int g = (rgb & 0x0000FF00) >> 8;
      return g - 55;
    }
  },
  
  
  /** The water. */
  WATER
  { public int getMapColor() {return 0x1E90FF;}
  };
  
  /**
   * Gets the map color.
   *
   * @return the map color
   */
  public abstract int getMapColor();
  
  /**
   * Gets the map height.
   *
   * @param rgb the rgb
   * @return the map height
   */
  public int getMapHeight(int rgb) {return 0;}
  
  /**
   * Gets the max map height.
   *
   * @return the max map height
   */
  public static int getMaxMapHeight() {return 200;}
 
  
}
