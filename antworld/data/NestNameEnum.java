package antworld.data;

/**
 * On the Ant World world map, there are 50 ant nests (things that look like Cheez-ITs).
 * Each nest has a unique name.
 * When a client opens a socket to the server for the first time, it must send
 * a nest name in CommData to request possession of a nest. 
 * 
 * When the client's connection request is accepted, the server will return a 
 * CommData with an array of NestData objects containing an element for each 
 * NestNameEnum below. 
 */
public enum NestNameEnum
{
  ARMY,         HARVESTER,    WEAVER,         FIRE,              HONEY_POT, 
  TRAP_JAW,     WOOD,         RED_STINGING,   ARGENTINE,         YELLOW_MEADOW, 
  BLACK_GARDEN, LEAF_CUTTER,  ATTA_LAEVIGATA, BULL,              BULLET,
  CARPENTER,    CRAZY,        GLIDER,         JACK_JUMPER,       LEMON,
  PHARAOH,      SLAVE_MAKER,  THEIF,          YELLOW_CITRONELLA, BIG_HEADED,
  ROVER,        NEEDLE,       TURTLE,         ACROBAT,           FUNGUS_GROWING, 
  CONE,         FORELIUS,     THATCH,         GUEST,             HYPOPONERA,
  LASIUS,       LEPTOGENYS,   LEPTOTHORAX,    VELVETY_TREE,      MYRMICA,
  TIGER,        OCHETELLUS,   STIGMA,         DRACULA,           ACORN,
  WHITE_FOOTED;
  
  public static final int SIZE = values().length;
}
