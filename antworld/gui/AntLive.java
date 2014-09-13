package antworld.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import antworld.ai.AntUtilities;
import antworld.client.Client;
import antworld.constants.Constants;
import antworld.ant.Ant;
import antworld.data.AntData;
import antworld.data.AntType;
import antworld.data.CommData;
import antworld.data.FoodData;
import antworld.data.FoodType;
import antworld.data.NestNameEnum;
import antworld.data.TeamNameEnum;
import antworld.info.AntManager;
import antworld.info.FoodManager;

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
    this.imageScroll = new JScrollPane(image);
    this.model = new AntTableModel(new ArrayList<Ant>(Client.getActiveAntManager().getAllMyAnts().values()));
    this.antTable = new JTable(model);
    this.tableScroll = new JScrollPane(antTable);
    this.antControl = new AntControl(Client.getActiveAntManager(), Client.getActiveFoodManager());

    // Add the AntImage
    this.imageScroll.setPreferredSize(new Dimension(700, 500));
    this.add(imageScroll, BorderLayout.LINE_START);

    // Add the AntTable
    this.antTable.setRowHeight(25);
    this.antTable.setGridColor(Color.BLACK);
    this.antTable.setEnabled(false);
    this.antTable.setPreferredSize(this.antTable.getPreferredScrollableViewportSize());
    this.antTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    // TODO this.antTable.setDefaultRenderer(Object.class, new
    // AntCellRenderer());
    this.add(tableScroll, BorderLayout.LINE_END);

    // Add the AntControl
    this.add(antControl, BorderLayout.PAGE_END);
  }

  public void update()
  {
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
            image.clearAnts();
            image.paintMyAnts(Client.getActiveAntManager().getAllMyAnts());
            image.paintEnemyAnts(Client.getActiveAntManager().getAllEnemyAnts());
            image.paintFood(Client.getActiveFoodManager().getFoodData());
            image.repaint();

            // Update the ant table.
            model.fireTableDataChanged();
          }
        });
      }
    }, 0, 2000);
  }

  public void centerViewPort(List<AntData> antData)
  {
    Point averageLocation = AntUtilities.getAverageLocation(antData);
    averageLocation.x = averageLocation.x - 300;
    averageLocation.y = averageLocation.y - 200;

    this.imageScroll.getViewport().setViewPosition(averageLocation);
  }

  public static void main(String[] args)
  {
    // My Ants
    ArrayList<AntData> antMyData = new ArrayList<AntData>();
    AntData a1 = new AntData(213, AntType.ATTACK, NestNameEnum.ACORN, TeamNameEnum.BlueGrama);
    a1.gridX = 450;
    a1.gridY = 346;
    antMyData.add(a1);

    AntData a2 = new AntData(214, AntType.BASIC, NestNameEnum.ACORN, TeamNameEnum.BlueGrama);
    a1.gridX = 454;
    a1.gridY = 349;
    antMyData.add(a2);

    // Enemy Ants
    HashSet<AntData> antEnemyData = new HashSet<AntData>();
    AntData b1 = new AntData(213, AntType.ATTACK, NestNameEnum.ACORN, TeamNameEnum.BlueGrama);
    b1.gridX = 555;
    b1.gridY = 451;
    antEnemyData.add(b1);

    AntData b2 = new AntData(214, AntType.BASIC, NestNameEnum.ACORN, TeamNameEnum.BlueGrama);
    b2.gridX = 572;
    b2.gridY = 454;
    antEnemyData.add(b2);

    // Food
    HashSet<FoodData> foodData = new HashSet<FoodData>();
    FoodData f1 = new FoodData(FoodType.ATTACK, 3400, 700, 34);
    foodData.add(f1);

    FoodData f2 = new FoodData(FoodType.ATTACK, 1234, 900, 50);
    foodData.add(f2);

    FoodData f3 = new FoodData(FoodType.ATTACK, 2450, 1700, 12);
    foodData.add(f3);

    FoodData f4 = new FoodData(FoodType.ATTACK, 1790, 480, 31);
    foodData.add(f4);

    CommData data = new CommData(NestNameEnum.ACORN, TeamNameEnum.BlueGrama);
    data.myAntList = antMyData;
    data.enemyAntSet = antEnemyData;
    data.foodSet = foodData;

    AntManager antManager = new AntManager(data);
    FoodManager foodManager = new FoodManager(data);

    AntLive live = new AntLive();

    JFrame frame = new JFrame("Live Ant Statistics");
    frame.add(live);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    live.update();
  }
}
