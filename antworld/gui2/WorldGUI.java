package antworld.gui2;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import antworld.ant.Ant;
import antworld.client.Client;
import antworld.info.AntManager;
import antworld.info.FoodManager;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;

public class WorldGUI extends Application
{
  private final BorderPane layout = new BorderPane();

  private static WorldImage       image;
  private static WorldTable       table;

  private static AntManager       antManager;
  private static FoodManager      foodManager;

  public WorldGUI()
  {
    // Bind the GUI to the data from the main program.
	  WorldGUI.antManager = Client.getActiveAntManager();
	  WorldGUI.foodManager = Client.getActiveFoodManager();
  }

  @Override
  public void start(Stage stage)
  {
    WorldGUI.initializeGUI();

    // Add the ant command options to the BorderPane.TOP.
    layout.setTop(new AntCommand());

    WorldGUI.image = new WorldImage();
    WorldGUI.image.updateWorldData(antManager, foodManager);
    layout.setCenter(new ScrollPane(image));

    WorldGUI.table = new WorldTable(new ArrayList<Ant>(antManager.getAllMyAnts().values()));
    layout.setRight(new ScrollPane(table));
    
    final LongProperty lastUpdate = new SimpleLongProperty();

    final long minUpdateInterval = 10000 ; // nanoseconds. Set to higher number to slow output.

    AnimationTimer timer = new AnimationTimer() {

        @Override
        public void handle(long now) {
            if (now - lastUpdate.get() > minUpdateInterval) {
            	System.out.println("Updating the GUI...");
                WorldGUI.updateGUI();
                lastUpdate.set(now);
            }
        }

    };

    timer.start();

    stage.setTitle("Live Ant Statistics");
    stage.setScene(new Scene(layout, 1280, 720));
    stage.show();
    
    

//    Timer timer = new Timer();
//    timer.scheduleAtFixedRate(new TimerTask()
//    {
//      @Override
//      public void run()
//      {
//        Platform.runLater(new Runnable()
//        {
//          @Override
//          public void run()
//          {
//        	System.out.println("Updating GUI...");
//            updateGUI();
//          }
//        });
//      }
//    }, 0, 2000);
  }

  public static void initializeGUI()
  {
    // Bind the GUI to the data from the main program.
	WorldGUI.antManager = Client.getActiveAntManager();
    WorldGUI.foodManager = Client.getActiveFoodManager();
  }

  public static void updateGUI()
  {
	WorldGUI.image.updateWorldData(antManager, foodManager);
	WorldGUI.table.updateWorldData();
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