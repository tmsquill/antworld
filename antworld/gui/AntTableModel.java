package antworld.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import antworld.ant.Ant;
import antworld.data.AntData;

public class AntTableModel extends AbstractTableModel
{
  private List<Ant> ants;

  public AntTableModel(ArrayList<Ant> arrayList)
  {
    this.ants = arrayList;
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
    return 9;
  }

  @Override
  public String getColumnName(int column)
  {
    String name = "???";
    switch (column)
    {
      case 0:
        name = "ID";
        break;
      case 1:
        name = "Grid X";
        break;
      case 2:
        name = "Grid Y";
        break;
      case 3:
        name = "Alive";
        break;
      case 4:
        name = "Type";
        break;
      case 5:
        name = "Carry Type";
        break;
      case 6:
        name = "Carry Units";
        break;
      case 7:
        name = "Health";
        break;
      case 8:
        name = "Underground";
        break;
    }
    return name;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex)
  {
    AntData data = this.ants.get(rowIndex).getAntData();
    Object value = null;
    switch (columnIndex)
    {
      case 0:
        value = data.id;
        break;
      case 1:
        value = data.gridX;
        break;
      case 2:
        value = data.gridY;
        break;
      case 3:
        value = data.alive;
        break;
      case 4:
        value = data.antType;
        break;
      case 5:
        value = data.carryType;
        break;
      case 6:
        value = data.carryUnits;
        break;
      case 7:
        value = data.health;
        break;
      case 8:
        value = data.underground;
        break;
    }
    return value;
  }
}