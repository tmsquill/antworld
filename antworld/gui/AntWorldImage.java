package antworld.gui;

import java.awt.Color;
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

import antworld.ant.Ant;
import antworld.constants.Constants;
import antworld.data.AntData;
import antworld.data.FoodData;

public class AntWorldImage extends JLabel
{
  private BufferedImage           image;
  private Graphics2D              graphics;

  private HashMap<Point, Integer> colorMap = new HashMap<Point, Integer>();

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

    this.graphics = image.createGraphics();
    this.setIcon(new ImageIcon(image));
  }

  public void clearAnts()
  {
    for (Entry<Point, Integer> entry : this.colorMap.entrySet())
    {
      this.graphics.setColor(new Color(entry.getValue()));
      this.graphics.fillRect(entry.getKey().x - 1, entry.getKey().y - 1, 3, 3);
    }

    this.colorMap.clear();
  }

  public void paintMyAnts(HashMap<Integer, Ant> myAnts)
  {
    this.graphics.setColor(Constants.MY_ANT_COLOR);
    AntData tmp = null;
    Iterator<Entry<Integer, Ant>> it = myAnts.entrySet().iterator();

    while (it.hasNext())
    {
      Map.Entry<Integer, Ant> pairs = it.next();
      tmp = pairs.getValue().getAntData();
      this.colorMap.put(new Point(tmp.gridX, tmp.gridY), this.image.getRGB(tmp.gridX, tmp.gridY));
      this.graphics.fillRect(tmp.gridX - 1, tmp.gridY - 1, 3, 3);
      it.remove();
    }
  }

  public void paintEnemyAnts(Set<AntData> enemyAnts)
  {
    this.graphics.setColor(Constants.ENEMY_ANT_COLOR);
    AntData tmp = null;
    Iterator<AntData> it = enemyAnts.iterator();

    while (it.hasNext())
    {
      tmp = it.next();
      this.colorMap.put(new Point(tmp.gridX, tmp.gridY), this.image.getRGB(tmp.gridX, tmp.gridY));
      this.graphics.fillRect(tmp.gridX - 1, tmp.gridY - 1, 3, 3);
    }
  }

  public void paintFood(Set<FoodData> foodData)
  {
    this.graphics.setColor(Constants.FOOD_COLOR);
    FoodData tmp = null;
    Iterator<FoodData> it = foodData.iterator();

    while (it.hasNext())
    {
      tmp = it.next();
      this.colorMap.put(new Point(tmp.gridX, tmp.gridY), this.image.getRGB(tmp.gridX, tmp.gridY));
      this.graphics.fillRect(tmp.gridX - 1, tmp.gridY - 1, 3, 3);
    }
  }
}
