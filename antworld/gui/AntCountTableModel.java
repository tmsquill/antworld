package antworld.gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import antworld.info.AntManager;

public class AntCountTableModel extends AbstractTableModel
{
	private List<AntCount> antCount;

	public AntCountTableModel(List<AntCount> antCount)
	{
		this.antCount = antCount;
	}

	@Override
	public int getRowCount()
	{
		if (antCount == null) return 0;
		return this.antCount.size();
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
		AntCount data = antCount.get(rowIndex);
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
