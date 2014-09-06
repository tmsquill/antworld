package antworld.gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import antworld.ant.Ant;
import antworld.data.AntData;
import antworld.info.AntManager;

public class AntTableModel extends AbstractTableModel
{
	private List<Ant> ants;

	public AntTableModel(List<Ant> ants)
	{
		this.ants = ants;
	}
	
	public void updateAnts(List<Ant> ants)
	{
		this.ants = ants;
	}

	@Override
	public int getRowCount()
	{
		return this.ants.size();
	}

	@Override
	public int getColumnCount()
	{
		return 11;
	}

	@Override
	public String getColumnName(int column)
	{
		String name = "???";
		switch (column)
		{
		case 0:
			name = "Nest";
			break;
		case 1:
			name = "Team";
			break;
		case 2:
			name = "ID";
			break;
		case 3:
			name = "Grid X";
			break;
		case 4:
			name = "Grid Y";
			break;
		case 5:
			name = "Alive";
			break;
		case 6:
			name = "Type";
			break;
		case 7:
			name = "Carry Type";
			break;
		case 8:
			name = "Carry Units";
			break;
		case 9:
			name = "Health";
			break;
		case 10:
			name = "Underground";
			break;
		}
		return name;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		AntData data = this.ants.get(rowIndex).antData;
		Object value = null;
		switch (columnIndex)
		{
		case 0:
			value = data.nestName.toString();
			break;
		case 1:
			value = data.teamName.toString();
			break;
		case 2:
			value = data.id;
			break;
		case 3:
			value = data.gridX;
			break;
	  case 4:
		  value = data.gridY;
		  break;
    case 5:
	    value = data.alive;
	    break;
    case 6:
	    value = data.antType;
	    break;
    case 7:
	    value = data.carryType;
	    break;
    case 8:
	    value = data.carryUnits;
	    break;
    case 9:
	    value = data.health;
	    break;
    case 10:
	    value = data.underground;
	    break;
    }
		return value;
	}
}
