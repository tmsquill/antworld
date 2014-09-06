package antworld.data;

import java.io.Serializable;

/**
 *!!!!!!!!!! DO NOT MODIFY ANYTHING IN THIS CLASS !!!!!!!!!!<br>
 * This class is serialized across a network socket. Any modifications will
 * prevent the server from being able to read this class.<br><br>
 * 
 * Each AntData contain a reference to an AntAction instance.
 * The AntAction class is used by the client to tell the server what action
 * the corresponding ant asks to do.
 * Each nest has a unique name.
 * When a client opens a socket to the server for the first time, it must send
 * a nest name in CommData to request possession of a nest. 
 * 
 * When the client's connection request is accepted, the server will return a 
 * CommData with an array of NestData objects containing an element for each 
 * NestNameEnum below. 
 */

public class AntAction implements Serializable
{
  private static final long serialVersionUID = Constants.VERSION;
  public enum AntActionType 
  { MOVE,       // MOVE direction
    ATTACK,     // ATTACK direction
    PICKUP,     // PICKUP direction quantity
    DROP,       // DROP direction quantity
    HEAL,       // HEAL direction (must be medic ant) | HEAL (must be underground)
    ENTER_NEST, // ENTER_NEST (must be on home nest area)
    EXIT_NEST,  // EXIT_NEST x y (must be underground and x,y must be in home nest area)
    BIRTH,      // Client adds new ant to antlist, sets ant type. Server deducts needed food from nest store.
    DIED,       // 
    STASIS      // STASIS
  }; 
  
  /** Every AntAction must have a type: 
   * MOVE | ATTACK | PICKUP | DROP | HEAL | ENTER_NEST | EXIT_NEST | BIRTH |DIED | STASIS
   */
  public AntActionType type;
  
  /** One of the 8 possible directions. The AntActionTypes requiring Direction 
   * are: MOVE, ATTACK, PICKUP, DROP, HEAL (when executed by a medic ant outside of the nest)
   */
  public Direction direction;
  
  
  /** (x,y) specify absolute coordinates in the Ant World grid. 
   * These fields are only used by the EXIT_NEST action. 
   * An EXIT_NEST action is only valid when the specified coordinates are within
   * a Manhattan Distance of Constants.NEST_RADIUS from the ant's own nest center and,
   * of course, when the ant is underground.
   */
  public int x, y;
  
  
  /** Specifies a quantity needed by the PICKUP and DROP ant actions.
   */
  public int quantity;
  
  
  /** Simple constructor.
   */
  public AntAction(AntActionType type)
  {
    this.type = type;
  }
  
  
  /** Constructor used for an AntActionType that requires a direction.
   */
  public AntAction(AntActionType type, Direction dir)
  {
    this.type = type;
    this.direction = dir;
  }
  
  
  /** Constructor used for an AntActionType that requires direction and quantity.
   */
  public AntAction(AntActionType type, Direction dir, int quantity)
  {
    this.type = type;
    this.direction = dir;
    this.quantity = quantity;
  }
  
  
  /** Constructor used for an AntActionType that requires ant world map coordinates.
   */
  public AntAction(AntActionType type, int x, int y)
  {
    this.type = type;
    this.x = x;
    this.y = y;
  }
  
  
  /** Constructor that returns a deep copy of an AntAction object. It happens
   *  that all elements of AntAction are enums or primitive types. Thus, a 
   *  "deep copy" is actually the same as a copy; however, if this class were
   *  ever to be expanded to include object instances, then this constructor 
   *  would deep copy those objects.
   */
  public AntAction(AntAction source)
  {
    type = source.type;
    direction = source.direction;
    x = source.x;
    y = source.y;
    quantity = source.quantity;
  }
  
  
  /** Deep copies data in source into this.
   */
  public void copyFrom(AntAction source)
  {
    type = source.type;
    direction = source.direction;
    x = source.x;
    y = source.y;
    quantity = source.quantity;
  }
  
  
  /** Used for debugging, this method returns a formatted string of this action
   * and the field values required by the value of type.
   */
  public String toString()
  {
    String out = "AntAction: ["+type+", ";
    if (type == AntActionType.MOVE) out += direction +"]";
    else if (type == AntActionType.ATTACK) out += direction +"]";
    else if (type == AntActionType.PICKUP) out += direction +" quentity="+quantity+"]";
    else if (type == AntActionType.DROP) out += direction +" quentity="+quantity+"]";
    else if (type == AntActionType.HEAL) out += direction +"]";
    else if (type == AntActionType.ENTER_NEST) out += "]";
    else if (type == AntActionType.EXIT_NEST) out += "("+x + ", " + y + ")]";
    else if (type == AntActionType.STASIS) out += "]";
    
    return out;
  }
}
