package antworld.gui2;

import antworld.data.NestNameEnum;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class AntCommand extends HBox
{
  Button buttonRetreat    = createButton("Retreat To Nest");
  Button buttonGoLocation = createButton("Go To Location");
  Button buttonGoNest     = createButton("Go To Nest");
  
  TextField textGoX = new TextField();
  TextField textGoY = new TextField();
  
  ComboBox comboGoNest = createComboBox(NestNameEnum.values());

  public AntCommand()
  {
    this.setPadding(new Insets(10, 10, 10, 10));
    this.setSpacing(10);
    this.setStyle("-fx-background-color: #336699;");

    buttonRetreat.setPrefSize(100, 20);
    buttonGoLocation.setPrefSize(100, 20);
    buttonGoNest.setPrefSize(100, 20);

    this.getChildren().addAll(buttonRetreat, textGoX, textGoY, buttonGoLocation, comboGoNest, buttonGoNest);
  }

  private Button createButton(String text)
  {
    final Button button = new Button(text);

    button.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent e)
      {
        button.setEffect(new DropShadow());
      }
    });

    button.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent e)
      {
        button.setEffect(null);
      }
    });

    return button;
  }
  
  private <E extends Enum<E>> ComboBox<E> createComboBox(E[] values)
  {
    ObservableList<Enum> options = FXCollections.observableArrayList(values);
    final ComboBox<E> combo = new ComboBox(options);
    return combo;
  }
}
