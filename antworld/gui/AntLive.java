package antworld.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import antworld.client.Client;
import antworld.constants.ActivityEnum;
import antworld.constants.Constants;
import antworld.data.AntData;
import antworld.data.AntType;
import antworld.data.CommData;
import antworld.data.NestNameEnum;
import antworld.data.TeamNameEnum;
import antworld.ant.Ant;
import antworld.astar.Location;

public class AntLive extends JPanel
{
  private AntWorldImage image;
  private JScrollPane imageScroll;
  private JTable antTable;
  private JScrollPane tableScroll;
  private AntTableModel model;
  private AntControl antControl;

  public AntLive()
  {
    this.setLayout(new BorderLayout());

    this.image = new AntWorldImage(Constants.WORLD_MAP_FILEPATH);
    this.imageScroll = new JScrollPane(image, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    this.model = new AntTableModel(new ArrayList<Ant>(Client.getActiveAntManager().getAllMyAnts().values()));
    this.antTable = new JTable(model);
    this.tableScroll = new JScrollPane(antTable);
    this.antControl = new AntControl(Client.getActiveAntManager());

    // Add the AntImage
    this.add(imageScroll, BorderLayout.CENTER);

    // Add the AntTable
    this.antTable.setRowHeight(25);
    this.antTable.setGridColor(Color.BLACK);
    this.antTable.setEnabled(false);

    this.add(tableScroll, BorderLayout.EAST);

    // Add the AntControl
    this.add(antControl, BorderLayout.SOUTH);

    
    
    // Update the GUI on another thread every so often.
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask()
    {
      @Override
      public void run()
      {
        SwingUtilities.invokeLater(new Runnable()
        {
          int returnToNestAfter = 0;
          @Override
          public void run()
          {
            // Update the world image.
            image.repaint();

            // Update the ant table.
            model.fireTableDataChanged();
            
            if (++returnToNestAfter % 1350 == 0)
            {
              System.out.println("Returning all ants to the nest...");
              for (Ant ant : Client.getActiveAntManager().getAllMyAnts().values())
              {
                ant.setActivity(ActivityEnum.RETREATING);
                ant.getDirections().clear();
                ant.setDestination(new Location(Client.centerX, Client.centerY));
                ant.setDirections(Ant.astar.dispatchAStar(ant.getCurrentLocation(), ant.getDestination()));
              }
            }
          }
        });
      }
    }, 0, 2000);
  }
}
