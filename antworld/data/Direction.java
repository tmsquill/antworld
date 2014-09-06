package antworld.data;

public enum Direction 
{ NORTH
  { public int deltaX() {return  0;}
    public int deltaY() {return -1;}
  }, 
  
  NORTHEAST
  { public int deltaX() {return  1;}
    public int deltaY() {return -1;}
  },
  
  EAST
  { public int deltaX() {return  1;}
    public int deltaY() {return  0;}
  },

  SOUTHEAST
  { public int deltaX() {return  1;}
    public int deltaY() {return  1;}
  },

  SOUTH
  { public int deltaX() {return  0;}
    public int deltaY() {return  1;}
  },
  
  SOUTHWEST
  { public int deltaX() {return -1;}
    public int deltaY() {return  1;}
  },
  
  WEST
  { public int deltaX() {return -1;}
    public int deltaY() {return  0;}
  },
 
  NORTHWEST
  { public int deltaX() {return -1;}
    public int deltaY() {return -1;}
  };
  

  public abstract int deltaX();
  public abstract int deltaY();
  public static final int SIZE = values().length;
  public static final Direction getRandomDir() {return values()[Constants.random.nextInt(SIZE)];}
  public static final Direction getLeftDir(Direction dir) {return values()[(dir.ordinal()+SIZE-1) % SIZE];}
  public static final Direction getRightDir(Direction dir) {return values()[(dir.ordinal()+1) % SIZE];}
}