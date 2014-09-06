package antworld.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import antworld.data.AntType;

public class AntCellRenderer extends DefaultTableCellRenderer
{
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
																								 boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if (AntLive.activeAnts.getAllMyAnts().get(row).antData.health < 15)
		{
			setBackground(Color.RED);
		}
		else
		{
			setBackground(Color.WHITE);
		}
		return this;
	}
}