package antworld.ai;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;

import antworld.data.AntData;

public class AntUtilities
{
	public static Point getAverageLocation(List<AntData> antData)
	{
		int x = 0;
		int y = 0;
		
		AntData tmp = null;
	  Iterator<AntData> it = antData.iterator();
	  
	  while(it.hasNext())
	  {
	  	tmp = it.next();
	  	x += tmp.gridX;
	  	y += tmp.gridY;
	  }
	  
	  if (antData.size() == 0) return new Point(-1, -1);
		x /= antData.size();
		y /= antData.size();
				
		return new Point(x, y);
	}
}
