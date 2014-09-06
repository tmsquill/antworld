package antworld.ai;

import java.awt.Point;

public abstract class AntAI
{
	//public abstract Enum<E> getJob();
	public abstract void retreatToBase();
	public abstract void retreatToLocation();
	public abstract boolean isEncumbered();
	public abstract boolean isInjured();
	public abstract void setDestination(Point destination);
}
