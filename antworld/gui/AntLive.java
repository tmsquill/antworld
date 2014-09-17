package antworld.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import antworld.client.Client;
import antworld.constants.Constants;
import antworld.ant.Ant;

public class AntLive extends JPanel
{
  private AntWorldImage image;
  private JScrollPane   imageScroll;
  private JTable        antTable;
  private JScrollPane   tableScroll;
  private AntTableModel model;
  private AntControl    antControl;

  public AntLive()
  {
    this.setLayout(new BorderLayout());

    this.image = new AntWorldImage(Constants.WORLD_MAP_FILEPATH);
    this.imageScroll = new JScrollPane(image, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    this.model = new AntTableModel(new ArrayList<Ant>(Client.getActiveAntManager().getAllMyAnts().values()));
    this.antTable = new JTable(model);
    this.tableScroll = new JScrollPane(antTable);
    this.antControl = new AntControl(Client.getActiveAntManager(), Client.getActiveFoodManager());

    //Add the AntImage
    this.imageScroll.setPreferredSize(new Dimension(1000, 1000));
    this.imageScroll.setMinimumSize(new Dimension(1000, 1000));
    this.imageScroll.setMaximumSize(new Dimension(1000, 1000));
    this.add(imageScroll, BorderLayout.LINE_START);

    //Add the AntTable
    this.antTable.setRowHeight(25);
    this.antTable.setGridColor(Color.BLACK);
    this.antTable.setEnabled(false);
    this.antTable.setPreferredSize(this.antTable.getPreferredScrollableViewportSize());

    this.add(tableScroll, BorderLayout.LINE_END);

    //Add the AntControl
    this.add(antControl, BorderLayout.PAGE_END);
    
    //Update the GUI on another thread every so often.
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask()
    {
      @Override
      public void run()
      {
        SwingUtilities.invokeLater(new Runnable()
        {
          @Override
          public void run()
          {
            // Update the world image.
            image.repaint();

            // Update the ant table.
            model.fireTableDataChanged();
          }
        });
      }
    }, 0, 2000);
  }
}
