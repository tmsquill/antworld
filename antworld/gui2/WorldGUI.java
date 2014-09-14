package antworld.gui2;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import antworld.ant.Ant;
import antworld.client.Client;
import antworld.info.AntManager;
import antworld.info.FoodManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;

public class WorldGUI extends Application
{
  private final BorderPane layout = new BorderPane();

  private WorldImage       image;
  private WorldTable       table;

  private AntManager       antManager;
  private FoodManager      foodManager;

  public WorldGUI()
  {
    // Bind the GUI to the data from the main program.
    this.antManager = Client.getActiveAntManager();
    this.foodManager = Client.getActiveFoodManager();
  }

  @Override
  public void start(Stage stage)
  {
    this.initializeGUI();

    // Add the ant command options to the BorderPane.TOP.
    layout.setTop(new AntCommand());

    this.image = new WorldImage();
    this.image.updateWorldData(antManager, foodManager);
    layout.setCenter(new ScrollPane(image));

    this.table = new WorldTable(new ArrayList<Ant>(antManager.getAllMyAnts().values()));
    layout.setRight(new ScrollPane(table));

    stage.setTitle("Live Ant Statistics");
    stage.setScene(new Scene(layout, 1280, 720));
    stage.show();

    /*
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask()
    {
      @Override
      public void run()
      {
        Platform.runLater(new Runnable()
        {
          @Override
          public void run()
          {
            updateGUI();
          }
        });
      }
    }, 0, 2000);
    */
  }

  public void initializeGUI()
  {
    // Bind the GUI to the data from the main program.
    this.antManager = Client.getActiveAntManager();
    this.foodManager = Client.getActiveFoodManager();
  }

  public void updateGUI()
  {
    this.image.updateWorldData(antManager, foodManager);
    this.table.updateWorldData();
  }

  // TODO - For testing the GUI.
  // public static void main(String[] args)
  // {
  // Client.initManagers();
  // WorldGUI gui = new WorldGUI();
  // Platform.runLater(new Runnable()
  // {
  // public void run()
  // {
  // gui.start(new Stage());
  // }
  // });
  // System.out.println("Starting Random Walk...");
  // Client.initializeActions(gui);
  // }
}