package antworld.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import antworld.ant.Ant;
import antworld.client.Client;
import antworld.constants.ActivityEnum;
import antworld.constants.Constants;
import antworld.constants.GatheringEnum;
import antworld.data.AntData;
import antworld.data.FoodData;

public class AntWorldImage extends JPanel
{
  private BufferedImage image;

  public AntWorldImage(String filename)
  {
    try
    {
      this.image = ImageIO.read(this.getClass().getResourceAsStream(filename));
    }
    catch (IOException e)
    {
      System.out.println("AntWorldImage Error: Unable to read the image file specified by " + filename);
      e.printStackTrace();
    }

    this.setPreferredSize(new Dimension(5000, 2500));
  }

  @Override
  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);

    this.paintImage(g);
    this.paintMyAnts(g);
    this.paintEnemyAnts(g);
    this.paintFood(g);
  }

  public void paintImage(Graphics g)
  {
    g.drawImage(image, 0, 0, null);
  }

  public void paintMyAnts(Graphics g)
  {
    for (Ant value : Client.getActiveAntManager().getAllMyAnts().values())
    {
      if (value.isInjured()) g.setColor(Color.BLACK);
      else if (value.getActivity() == ActivityEnum.APPROACHING_FOOD) g.setColor(new Color(0x00CCFF));
      else if (value.getAntData().carryUnits > 0 && value.getGathering() == GatheringEnum.FOOD) g.setColor(new Color(
          0x00FF00));
      else if (value.getAntData().carryUnits > 0 && value.getGathering() == GatheringEnum.WATER) g.setColor(new Color(
          0xFF0000));
      else if (value.getGathering() == GatheringEnum.FOOD) g.setColor(Constants.MY_FOOD_ANT_COLOR);
      else if (value.getGathering() == GatheringEnum.WATER) g.setColor(Constants.MY_WATER_ANT_COLOR);
      else g.setColor(new Color(0xFFFFFF));
      g.fillRect(value.getAntData().gridX - 1, value.getAntData().gridY - 1, 3, 3);
    }
  }

  public void paintEnemyAnts(Graphics g)
  {
    g.setColor(Constants.ENEMY_ANT_COLOR);
    AntData tmp = null;
    Iterator<AntData> it = Client.getActiveAntManager().getAllEnemyAnts().iterator();

    while (it.hasNext())
    {
      tmp = it.next();
      g.fillRect(tmp.gridX - 1, tmp.gridY - 1, 3, 3);
    }
  }

  public void paintFood(Graphics g)
  {
    g.setColor(Constants.FOOD_COLOR);
    FoodData tmp = null;
    Iterator<FoodData> it = Client.getActiveFoodManager().getFoodData().iterator();

    while (it.hasNext())
    {
      tmp = it.next();
      g.fillRect(tmp.gridX - 1, tmp.gridY - 1, 3, 3);
    }
  }
}
