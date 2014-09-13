package antworld.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import antworld.constants.AggressionEnum;
import antworld.data.AntType;
import antworld.data.FoodType;
import antworld.data.NestNameEnum;
import antworld.info.AntManager;
import antworld.info.FoodManager;

public class AntControl extends JTabbedPane
{
  public AntControl(AntManager activeAnts, FoodManager activeFood)
  {
    this.addTab("Ant Commands", new AntCommand(activeAnts));
    this.addTab("Nest Commands", new NestCommand(activeFood));
  }

  public class AntCommand extends JPanel implements ActionListener
  {
    private JButton                   returnToNestButton  = this.createJButton("Return To Nest");
    private JButton                   goLocationButton    = this.createJButton("Go to Location");
    private JButton                   goNestButton        = this.createJButton("Go to Nest");
    private JButton                   setAggressionButton = this.createJButton("Set Aggression");

    private JTextField                goLocationX         = new JTextField();
    private JTextField                goLocationY         = new JTextField();
    private JComboBox<NestNameEnum>   goNestMenu          = this.createJComboBox(NestNameEnum.values());
    private JComboBox<AggressionEnum> setAggressionMenu   = this.createJComboBox(AggressionEnum.values());

    private AntCountTableModel        model;
    private JTable                    table;

    public AntCommand(AntManager activeAnts)
    {
      this.setLayout(new GridBagLayout());

      GridBagConstraints c = new GridBagConstraints();

      c.fill = GridBagConstraints.HORIZONTAL;
      c.anchor = GridBagConstraints.PAGE_START;
      c.weightx = 1;
      c.weighty = 1;

      c.gridx = 0;
      c.gridy = 0;
      c.gridwidth = 3;
      this.add(this.returnToNestButton, c);

      c.gridx = 0;
      c.gridy = 1;
      c.gridwidth = 1;
      this.add(this.goLocationX, c);

      c.gridx = 1;
      c.gridy = 1;
      c.gridwidth = 1;
      this.add(this.goLocationY, c);

      c.gridx = 2;
      c.gridy = 1;
      c.gridwidth = 1;
      this.add(this.goLocationButton, c);

      c.gridx = 0;
      c.gridy = 2;
      c.gridwidth = 2;
      this.add(this.goNestMenu, c);

      c.gridx = 2;
      c.gridy = 2;
      c.gridwidth = 1;
      this.add(this.goNestButton, c);

      c.gridx = 0;
      c.gridy = 3;
      c.gridwidth = 2;
      this.add(this.setAggressionMenu, c);

      c.gridx = 2;
      c.gridy = 3;
      c.gridwidth = 1;
      this.add(this.setAggressionButton, c);

      c.gridx = 3;
      c.gridy = 0;
      c.gridwidth = 1;
      c.gridheight = 4;
      this.model = new AntCountTableModel(activeAnts.getAntCounts());
      this.table = new JTable(model);
      this.table.setRowHeight(25);
      this.table.setGridColor(Color.BLACK);
      this.table.setEnabled(false);
      this.table.setPreferredScrollableViewportSize(table.getPreferredSize());
      this.add(new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
          ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), c);
    }

    private JButton createJButton(String buttonText)
    {
      final JButton button = new JButton(buttonText);
      button.addActionListener(this);
      return button;
    }

    private <E extends Enum<E>> JComboBox<E> createJComboBox(E[] values)
    {
      final JComboBox<E> combo = new JComboBox<E>(values);
      combo.setSelectedIndex(0);
      combo.addActionListener(this);
      return combo;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      Object event = e.getSource();
      if (event == this.goLocationButton)
      {
        Point location = new Point(Integer.parseInt(this.goLocationX.getText()), Integer.parseInt(this.goLocationY
            .getText()));
        // TODO Tell ants to go to the specified location.
      }
      else if (event == this.goNestButton)
      {
        // TODO Get the location of the selected nest and tell the ants to go
        // there.
      }
      else if (event == this.setAggressionButton)
      {
        // TODO Set the aggression level of the ants.
      }
    }
  }

  public class NestCommand extends JPanel implements ActionListener
  {
    private JButton             setResourcePriorityButton = this.createJButton("Set Resource Priority");
    private JButton             setAntPriorityButton      = this.createJButton("Set Ant Priority");

    private JComboBox<FoodType> setResourcePriorityMenu   = this.createJComboBox(FoodType.values());
    private JComboBox<AntType>  setAntPriorityMenu        = this.createJComboBox(AntType.values());

    private FoodCountTableModel model;
    private JTable              table;

    public NestCommand(FoodManager activeFood)
    {
      this.setLayout(new GridBagLayout());

      GridBagConstraints c = new GridBagConstraints();

      c.fill = GridBagConstraints.HORIZONTAL;
      c.anchor = GridBagConstraints.PAGE_START;
      c.weightx = 1;
      c.weighty = 1;

      c.gridx = 0;
      c.gridy = 0;
      c.gridwidth = 1;
      this.add(this.setResourcePriorityMenu, c);

      c.gridx = 1;
      c.gridy = 0;
      c.gridwidth = 1;
      this.add(this.setResourcePriorityButton, c);

      c.gridx = 0;
      c.gridy = 1;
      c.gridwidth = 1;
      this.add(this.setAntPriorityMenu, c);

      c.gridx = 1;
      c.gridy = 1;
      c.gridwidth = 1;
      this.add(this.setAntPriorityButton, c);

      c.gridx = 2;
      c.gridy = 0;
      c.gridwidth = 1;
      c.gridheight = 2;
      this.model = new FoodCountTableModel(activeFood.getFoodCounts());
      this.table = new JTable(model);
      this.table.setRowHeight(25);
      this.table.setGridColor(Color.BLACK);
      this.table.setEnabled(false);
      this.table.setPreferredScrollableViewportSize(table.getPreferredSize());
      this.add(new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
          ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), c);
    }

    private JButton createJButton(String buttonText)
    {
      final JButton button = new JButton(buttonText);
      button.addActionListener(this);
      return button;
    }

    private <E extends Enum<E>> JComboBox<E> createJComboBox(E[] values)
    {
      final JComboBox<E> combo = new JComboBox<E>(values);
      combo.setSelectedIndex(0);
      combo.addActionListener(this);
      return combo;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      Object event = e.getSource();
      if (event == this.setResourcePriorityButton)
      {
        // TODO Tell ants to go to the specified location.
      }
      else if (event == this.setAntPriorityButton)
      {
        // TODO Get the location of the selected nest and tell the ants to go
        // there.
      }
    }
  }
}
