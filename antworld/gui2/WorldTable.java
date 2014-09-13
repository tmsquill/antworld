package antworld.gui2;

import java.util.List;

import antworld.ant.Ant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class WorldTable extends VBox
{
  private final ObservableList<Ant> data;
  private final TableView<Ant>      table;

  public WorldTable(List<Ant> ants)
  {
    this.data = FXCollections.observableArrayList(ants);
    this.table = new TableView<Ant>();

    TableColumn<Ant, String> columnNest = new TableColumn<Ant, String>("Nest");
    columnNest.setCellValueFactory(new PropertyValueFactory<Ant, String>("nest"));

    TableColumn<Ant, String> columnTeam = new TableColumn<Ant, String>("Team");
    columnTeam.setCellValueFactory(new PropertyValueFactory<Ant, String>("team"));

    TableColumn<Ant, String> columnID = new TableColumn<Ant, String>("ID");
    columnID.setCellValueFactory(new PropertyValueFactory<Ant, String>("id"));

    TableColumn<Ant, String> columnX = new TableColumn<Ant, String>("X");
    columnX.setCellValueFactory(new PropertyValueFactory<Ant, String>("x"));

    TableColumn<Ant, String> columnY = new TableColumn<Ant, String>("Y");
    columnY.setCellValueFactory(new PropertyValueFactory<Ant, String>("y"));

    TableColumn<Ant, String> columnType = new TableColumn<Ant, String>("Type");
    columnType.setCellValueFactory(new PropertyValueFactory<Ant, String>("type"));

    TableColumn<Ant, String> columnCarry = new TableColumn<Ant, String>("Carry");
    columnCarry.setCellValueFactory(new PropertyValueFactory<Ant, String>("carry"));

    TableColumn<Ant, String> columnHealth = new TableColumn<Ant, String>("Health");
    columnHealth.setCellValueFactory(new PropertyValueFactory<Ant, String>("health"));

    TableColumn<Ant, String> columnUnderground = new TableColumn<Ant, String>("Underground");
    columnUnderground.setCellValueFactory(new PropertyValueFactory<Ant, String>("underground"));

    this.table.setItems(data);
    this.table.getColumns().addAll(columnNest, columnTeam, columnID, columnX, columnY, columnType, columnCarry,
        columnHealth, columnUnderground);
    this.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    this.setSpacing(5);
    this.setPadding(new Insets(10, 10, 10, 10));
    this.getChildren().addAll(this.table);
  }

  void updateWorldData()
  {
    this.table.getColumns().get(0).setVisible(false);
    this.table.getColumns().get(0).setVisible(true);
  }
}
