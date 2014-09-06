package antworld.gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class FoodCountTableModel extends AbstractTableModel
{
	private List<FoodCount> nestCount;

	public FoodCountTableModel(List<FoodCount> nestCount)
	{
		this.nestCount = nestCount;
	}

	@Override
	public int getRowCount()
	{
		if (nestCount == null) return 0;
		return this.nestCount.size();
	}

	@Override
	public int getColumnCount()
	{
		return 2;
	}

	@Override
	public String getColumnName(int column)
	{
		String name = "???";
		switch (column)
		{
		case 0:
			name = "Ant Type";
			break;
		case 1:
			name = "Quantity";
			break;
		}
		return name;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		FoodCount data = this.nestCount.get(rowIndex);
		Object value = null;
		switch (columnIndex)
		{
		case 0:
			value = data.getType();
			break;
		case 1:
			value = data.getCount();
			break;
		}
		return value;
	}
}
