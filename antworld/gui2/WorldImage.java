package antworld.gui2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import antworld.ant.Ant;
import antworld.constants.Constants;
import antworld.data.AntData;
import antworld.data.FoodData;
import antworld.info.AntManager;
import antworld.info.FoodManager;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import antworld.data.FoodType;

;

public class WorldImage extends StackPane
{
  private final Image           image       = new Image(this.getClass().getResourceAsStream(
                                                Constants.WORLD_MAP_FILEPATH));
  private final Canvas          canvas      = new Canvas(this.image.getWidth(), this.image.getHeight());
  private final GraphicsContext graphics    = this.canvas.getGraphicsContext2D();

  final double                  SCALE_DELTA = 1.1;

  private double                pressedX, pressedY;

  public WorldImage()
  {
    this.setMinSize(600, 600);

    this.graphics.drawImage(image, 0, 0);

    this.getChildren().add(canvas);

    setOnMousePressed(new EventHandler<MouseEvent>()
    {
      public void handle(MouseEvent event)
      {
        pressedX = event.getX();
        pressedY = event.getY();
      }
    });

    setOnMouseDragged(new EventHandler<MouseEvent>()
    {
      public void handle(MouseEvent event)
      {
        setTranslateX(getTranslateX() + event.getX() - pressedX);
        setTranslateY(getTranslateY() + event.getY() - pressedY);

        event.consume();
      }
    });

    setOnScroll(new EventHandler<ScrollEvent>()
    {
      @Override
      public void handle(ScrollEvent event)
      {
        event.consume();

        if (event.getDeltaY() == 0) { return; }

        double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

        canvas.setScaleX(canvas.getScaleX() * scaleFactor);
        canvas.setScaleY(canvas.getScaleY() * scaleFactor);
      }
    });

  }

  public void updateWorldData(AntManager antData, FoodManager foodData)
  {
    this.graphics.drawImage(image, 0, 0);
    this.paintMyAnts(antData.getAllMyAnts());
    this.paintEnemyAnts(antData.getAllEnemyAnts());
    this.paintFood(foodData.getFoodData());
  }

  private void paintMyAnts(HashMap<Integer, Ant> ants)
  {
    this.graphics.setFill(Color.ORANGE);
    
    for (Ant value : ants.values())
    {
      //this.graphics.fillText(Integer.toString(value.getAntData().id), value.getAntData().gridX - 5, value.getAntData().gridY - 5);
      this.graphics.fillRect(value.getAntData().gridX - 1, value.getAntData().gridY - 1, 3, 3);
    }
  }

  private void paintEnemyAnts(Set<AntData> ants)
  {
    this.graphics.setFill(Color.BLUE);
    AntData tmp = null;
    Iterator<AntData> myAntsIterator = ants.iterator();

    while (myAntsIterator.hasNext())
    {
      tmp = myAntsIterator.next();
      //this.graphics.fillText(Integer.toString(tmp.health), tmp.gridX - 5, tmp.gridY - 5);
      this.graphics.fillRect(tmp.gridX - 1, tmp.gridY - 1, 3, 3);
    }
  }

  private void paintFood(Set<FoodData> ants)
  {
    FoodData tmp = null;
    Iterator<FoodData> myAntsIterator = ants.iterator();

    while (myAntsIterator.hasNext())
    {
      tmp = myAntsIterator.next();

      switch (tmp.foodType)
      {
        case UNKNOWN:
          this.graphics.setFill(Color.web("0x0000C0"));
          break;
        case WATER:
          this.graphics.setFill(Color.web("0x0000C8"));
          break;
        case DEFENCE:
          this.graphics.setFill(Color.web("0xAA00FF"));
          break;
        case ATTACK:
          this.graphics.setFill(Color.web("0xA300F4"));
          break;
        case SPEED:
          this.graphics.setFill(Color.web("0x9800E3"));
          break;
        case VISION:
          this.graphics.setFill(Color.web("0x8E00D5"));
          break;
        case CARRY:
          this.graphics.setFill(Color.web("0x8900CD"));
          break;
        case MEDIC:
          this.graphics.setFill(Color.web("0x7C00BA"));
          break;
        case BASIC:
          this.graphics.setFill(Color.web("0x7000A8"));
          break;
      }

      this.graphics.fillText(Integer.toString(tmp.getCount()), tmp.gridX - 5, tmp.gridY - 5);
      this.graphics.fillRect(tmp.gridX - 1, tmp.gridY - 1, 3, 3);
    }
  }
}
