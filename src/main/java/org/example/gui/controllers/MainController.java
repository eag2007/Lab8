package org.example.gui.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.gui.Main;
import org.example.gui.commands.Info;
import org.example.gui.commands.Show;
import org.example.packet.collection.Route;

import java.util.List;

public class MainController {

    @FXML private StackPane mainRoot;
    @FXML private TableView<Route> routeTable;
    @FXML private TableColumn<Route, Long>    colId;
    @FXML private TableColumn<Route, String>  colName;
    @FXML private TableColumn<Route, Integer> colDistance;
    @FXML private TableColumn<Route, String>  colPrice;
    @FXML private TableColumn<Route, String>  colAuthor;
    @FXML private TableColumn<Route, String>  colCoords;
    @FXML private TableColumn<Route, String>  colFrom;
    @FXML private TableColumn<Route, String>  colTo;
    @FXML private TableColumn<Route, String>  colDate;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDistance.setCellValueFactory(new PropertyValueFactory<>("distance"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCoords.setCellValueFactory(d -> {
            var c = d.getValue().getCoordinates();
            return new SimpleStringProperty(c != null ? "(" + c.getX() + ", " + c.getY() + ")" : "—");
        });
        colFrom.setCellValueFactory(d -> {
            var f = d.getValue().getFrom();
            return new SimpleStringProperty(f != null ? "(" + f.getX() + ", " + f.getY() + ", " + f.getZ() + ")" : "—");
        });
        colTo.setCellValueFactory(d -> {
            var t = d.getValue().getTo();
            return new SimpleStringProperty(t != null ? "(" + t.getX() + ", " + t.getY() + ", " + t.getZ() + ")" : "—");
        });
        colDate.setCellValueFactory(d -> {
            var dt = d.getValue().getCreationDate();
            return new SimpleStringProperty(dt != null ? dt.toLocalDateTime().toString() : "—");
        });

        Main.mainController = this;
        refreshTable();
    }

    public void refreshTable() {
        new Show().execute();
    }

    public void fillTable(List<Route> routes) {
        routeTable.setItems(FXCollections.observableArrayList(routes));
    }

    @FXML
    private void onShowClick() {
        refreshTable();
    }

    @FXML
    private void onInfoClick() {
        new Info().execute();
    }

    @FXML
    private void onLogoutClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) mainRoot.getScene().getWindow();
            stage.getScene().setRoot(loginRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}