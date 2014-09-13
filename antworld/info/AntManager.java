package antworld.info;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import antworld.ant.Ant;
import antworld.data.AntData;
import antworld.data.AntType;
import antworld.data.CommData;
import antworld.exceptions.InvalidAntTypeException;
import antworld.gui.AntCount;

/**
 * This class represents an ant management system that is used to track usage of the ants.
 */
public class AntManager
{
	
	/** The counts for each type of ant. */
	private List<AntCount> antCount = new ArrayList<AntCount>();
	
	/** The list of all ants controlled by the user. */
	private List<Ant> allMyAnts = new ArrayList<Ant>();
	
	/** The list of all ants controlled by enemies. */
	private HashSet<AntData> allEnemyAnts;
	
	/** The list of attack ants. */
	private List<Ant> attackAnts = new ArrayList<Ant>();
	
	/** The list of basic ants. */
	private List<Ant> basicAnts = new ArrayList<Ant>();
	
	/** The list of carry ants. */
	private List<Ant> carryAnts = new ArrayList<Ant>();
	
	/** The list of defense ants. */
	private List<Ant> defenseAnts = new ArrayList<Ant>();
	
	/** The list of medic ants. */
	private List<Ant> medicAnts = new ArrayList<Ant>();
	
	/** The list of speed ants. */
	private List<Ant> speedAnts = new ArrayList<Ant>();
	
	/** The list of vision ants. */
	private List<Ant> visionAnts = new ArrayList<Ant>();

	/**
	 * Creates a new AntManager object and populates all ant lists and counts.
	 *
	 * @param data the communications data received from the server that contains ants
	 */
	public AntManager(CommData data)
	{
	  Iterator<AntData> it = data.myAntList.iterator();
	  
	  while (it.hasNext())
	  {
	    this.allMyAnts.add(new Ant(it.next()));
	  }
	  
	  this.allEnemyAnts = data.enemyAntSet;
	  
		this.antCount.add(new AntCount("Attack", 0));
		this.antCount.add(new AntCount("Basic", 0));
		this.antCount.add(new AntCount("Carry", 0));
		this.antCount.add(new AntCount("Defense", 0));
		this.antCount.add(new AntCount("Medic", 0));
		this.antCount.add(new AntCount("Speed", 0));
		this.antCount.add(new AntCount("Vision", 0));
		
		this.updateAllAnts(data);		
	}
	
	/**
	 * Updates the lists in the AntManger object, this method should be called when new
	 * communications data is recieved from the server.
	 */
	public void updateAllAnts(CommData data)
	{
	  Ant tmp = null;
    Iterator<Ant> it = this.allMyAnts.iterator();
    
    while (it.hasNext())
    {
      tmp = it.next();
      
      if (!tmp.antData.alive)
      {
        switch (tmp.antData.antType)
        {
          case ATTACK: this.attackAnts.remove(tmp);
          break;
          case BASIC: this.basicAnts.remove(tmp);
          break;
          case CARRY: this.carryAnts.remove(tmp);
          break;
          case DEFENCE: this.defenseAnts.remove(tmp);
          break;
          case MEDIC: this.medicAnts.remove(tmp);
          break;
          case SPEED: this.speedAnts.remove(tmp);
          break;
          case VISION: this.visionAnts.remove(tmp);
          break;
          default: 
            try
            {
              throw new InvalidAntTypeException(tmp.antData.antType.toString());
            }
            catch (InvalidAntTypeException e)
            {
              e.printStackTrace();
            }
          break;
        }
      }     
    }
		
		this.antCount.get(0).setCount(this.getAttackAntCount());
		this.antCount.get(1).setCount(this.getBasicAntCount());
		this.antCount.get(2).setCount(this.getCarryAntCount());
		this.antCount.get(3).setCount(this.getDefenseAntCount());
		this.antCount.get(4).setCount(this.getMedicAntCount());
		this.antCount.get(5).setCount(this.getSpeedAntCount());
		this.antCount.get(6).setCount(this.getVisionAntCount());
	}
	
	
	
	//TODO
	/**
	 * Births a new ant.
	 *
	 * @param data the communications data
	 * @param type the type of ant to birth
	 */
	public void birthAnt(CommData data, AntType type)
	{

	}
	
	
	
	/**
	 * Clears all ant lists.
	 */
	public void clearAllAnts()
	{
		if (!(this.allMyAnts == null)) this.allMyAnts.clear();
    if (!(this.allEnemyAnts == null)) this.allEnemyAnts.clear();

		this.attackAnts.clear();
		this.basicAnts.clear();
		this.carryAnts.clear();
		this.defenseAnts.clear();
		this.medicAnts.clear();
		this.speedAnts.clear();
		this.visionAnts.clear();
	}
	
	
	
	/**
	 * Gets the ant counts.
	 *
	 * @return the ant counts
	 */
	public List<AntCount> getAntCounts()
	{
		return this.antCount;
	}
	
	/**
	 * Gets the all my ants.
	 *
	 * @return the all my ants
	 */
	public List<Ant> getAllMyAnts()
	{
		return this.allMyAnts;
	}
	
	/**
	 * Gets the all enemy ants.
	 *
	 * @return the all enemy ants
	 */
	public Set<AntData> getAllEnemyAnts()
  {
    return this.allEnemyAnts;
  }
	
	
	
	//Get methods for the count of each ant type.
	/**
	 * Gets the attack ant count.
	 *
	 * @return the attack ant count
	 */
	public int getAttackAntCount()
	{
		return this.attackAnts.size();
	}
	
	/**
	 * Gets the basic ant count.
	 *
	 * @return the basic ant count
	 */
	public int getBasicAntCount()
	{
		return this.basicAnts.size();
	}
	
	/**
	 * Gets the carry ant count.
	 *
	 * @return the carry ant count
	 */
	public int getCarryAntCount()
	{
		return this.carryAnts.size();
	}
	
	/**
	 * Gets the defense ant count.
	 *
	 * @return the defense ant count
	 */
	public int getDefenseAntCount()
	{
		return this.defenseAnts.size();
	}
	
	/**
	 * Gets the medic ant count.
	 *
	 * @return the medic ant count
	 */
	public int getMedicAntCount()
	{
		return this.medicAnts.size();
	}
	
	/**
	 * Gets the speed ant count.
	 *
	 * @return the speed ant count
	 */
	public int getSpeedAntCount()
	{
		return this.speedAnts.size();
	}
	
	/**
	 * Gets the vision ant count.
	 *
	 * @return the vision ant count
	 */
	public int getVisionAntCount()
	{
		return this.visionAnts.size();
	}
	
	/**
	 * Gets the total ant count.
	 *
	 * @return the total ant count
	 */
	public int getTotalAntCount()
	{
		return this.attackAnts.size() + this.basicAnts.size() + this.carryAnts.size() + this.defenseAnts.size()
				   + this.medicAnts.size() + this.speedAnts.size() + this.visionAnts.size();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "All Ants: " + this.allMyAnts.toString() + "\nAttack Ants: " + this.attackAnts.toString() + "\nBasic Ants: " + this.basicAnts.toString() +
				   "\nCarry Ants: " + this.carryAnts.toString() + "\nDefense Ants: " + this.defenseAnts.toString() + "\nMedic Ants: " + this.medicAnts.toString() +
				   "\nSpeed Ants: " + this.speedAnts.toString() + "\nVision Ants: " + this.visionAnts.toString();
	        
	}
}
