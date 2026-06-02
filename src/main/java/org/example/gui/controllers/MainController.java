package org.example.gui.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.gui.Main;
import org.example.gui.commands.*;
import org.example.gui.managers.ManagerAuth;
import org.example.gui.managers.ManagerValidation;
import org.example.packet.collection.Route;
import org.example.packet.collection.RouteClient;

import java.util.List;

import static org.example.gui.Main.server;

public class MainController {

    @FXML private StackPane mainRoot;
    @FXML private TableView<Route> routeTable;
    @FXML private TableColumn<Route, Long> colId;
    @FXML private TableColumn<Route, String> colName;
    @FXML private TableColumn<Route, String> colCoordX;
    @FXML private TableColumn<Route, String> colCoordY;
    @FXML private TableColumn<Route, String> colFromX;
    @FXML private TableColumn<Route, String> colFromY;
    @FXML private TableColumn<Route, String> colFromZ;
    @FXML private TableColumn<Route, String> colToX;
    @FXML private TableColumn<Route, String> colToY;
    @FXML private TableColumn<Route, String> colToZ;
    @FXML private TableColumn<Route, Integer> colDistance;
    @FXML private TableColumn<Route, Integer> colPrice;
    @FXML private TableColumn<Route, String> colAuthor;
    @FXML private TableColumn<Route, String> colDate;

    @FXML private Label routeIdLabel;
    @FXML private TextField routeNameField;
    @FXML private TextField routeCoordXField;
    @FXML private TextField routeCoordYField;
    @FXML private TextField routeFromXField;
    @FXML private TextField routeFromYField;
    @FXML private TextField routeFromZField;
    @FXML private TextField routeToXField;
    @FXML private TextField routeToYField;
    @FXML private TextField routeToZField;
    @FXML private TextField routeDistanceField;
    @FXML private TextField routePriceField;
    @FXML private Label routeAuthorLabel;
    @FXML private Label routeDateLabel;

    @FXML private Label userLoginLabel;

    private final ManagerValidation validator = new ManagerValidation();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDistance.setCellValueFactory(new PropertyValueFactory<>("distance"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));

        colCoordX.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getCoordinates().getX())));
        colCoordY.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getCoordinates().getY())));
        colFromX.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getFrom().getX())));
        colFromY.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getFrom().getY())));
        colFromZ.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getFrom().getZ())));
        colToX.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTo().getX())));
        colToY.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTo().getY())));
        colToZ.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTo().getZ())));
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreationDate().toLocalDateTime().toString()));


        routeTable.setOnMouseClicked(event -> {
            Route selected = routeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showRouteDetails(selected);
            }
        });

        Main.mainController = this;
        new Show().executeCommand(null, server, null);
    }

    public void fillTable(List<Route> routes) {
        routeTable.setItems(FXCollections.observableArrayList(routes));
    }

    @FXML
    private void onShowClick() {
        routeIdLabel.setText("");
        routeNameField.setText("");
        routeCoordXField.setText("");
        routeCoordYField.setText("");
        routeFromXField.setText("");
        routeFromYField.setText("");
        routeFromZField.setText("");
        routeToXField.setText("");
        routeToYField.setText("");
        routeToZField.setText("");
        routeDistanceField.setText("");
        routePriceField.setText("");
        routeAuthorLabel.setText("");
        routeDateLabel.setText("");

        new Show().executeCommand(null, server, null);
    }

    @FXML
    private void onInfoClick() {
        new Info().executeCommand(null, server, null);
    }

    @FXML
    private void onLogoutClick() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        new Logout().executeCommand(null, server, stage);
    }

    private void showRouteDetails(Route route) {
        routeIdLabel.setText(String.valueOf(route.getId()));
        routeNameField.setText(route.getName());
        routeCoordXField.setText(String.valueOf(route.getCoordinates().getX()));
        routeCoordYField.setText(String.valueOf(route.getCoordinates().getY()));
        routeFromXField.setText(String.valueOf(route.getFrom().getX()));
        routeFromYField.setText(String.valueOf(route.getFrom().getY()));
        routeFromZField.setText(String.valueOf(route.getFrom().getZ()));
        routeToXField.setText(String.valueOf(route.getTo().getX()));
        routeToYField.setText(String.valueOf(route.getTo().getY()));
        routeToZField.setText(String.valueOf(route.getTo().getZ()));
        routeDistanceField.setText(String.valueOf(route.getDistance()));
        routePriceField.setText(String.valueOf(route.getPrice()));
        routeAuthorLabel.setText(route.getAuthor());
        routeDateLabel.setText(route.getCreationDate().toLocalDateTime().toString());
    }

    @FXML
    private void onAddClick() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        AddController.show(stage);
    }

    @FXML
    private void onAddIfMaxClick() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        AddIfMaxController.show(stage);
    }

    @FXML
    private void onClearClick() {
        ClearController.onClearCLick();
        onShowClick();
    }

    @FXML
    private void onRemoveFirstClick() {
        RemoveFirstController.onRemoveFirstClick();
        onShowClick();
    }

    @FXML
    private void onRemoveByIdClick() {
        Route selected = routeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            long id = selected.getId();
            RemoveByIdController.onRemoveByIdControllerClick(id);
            onShowClick();
        }
    }

    @FXML
    private void onRemoveAllDistClick() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        RemoveAllByDistanceController.show(stage);
        onShowClick();
    }

    @FXML
    private void onAverageDistanceClick() {
        AverageOfDistanceController.onAverageDistanceClick();
    }

    @FXML
    private void onFilterDistClick() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        FilterLessThanDistanceController.show(stage);
    }

    @FXML
    private void onExecuteScriptClick() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        ExecuteScriptController.execute(stage);
    }

    @FXML
    private void onUpdateClick() {
        Route selected = routeTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AlertController.show("Предупреждение", "Выберите маршрут для обновления");
            return;
        }

        long id = selected.getId();

        try {
            RouteClient routeClient = validator.validateFromFields(
                    routeNameField.getText(),
                    routeCoordXField.getText(),
                    routeCoordYField.getText(),
                    routeFromXField.getText(),
                    routeFromYField.getText(),
                    routeFromZField.getText(),
                    routeToXField.getText(),
                    routeToYField.getText(),
                    routeToZField.getText(),
                    routeDistanceField.getText(),
                    routePriceField.getText()
            );

            new Update().executeCommand(new String[]{String.valueOf(id)}, server, routeClient);
            onShowClick();
        } catch (IllegalArgumentException e) {
            AlertController.show("Ошибка валидации", e.getMessage());
        }
    }

    @FXML
    private void onHelpClick() {
        new Help().executeCommand(null, server, null);
    }

    @FXML
    private void onHistoryClick() {
        new History().executeCommand(null, server, null);
    }

    public void setUserLogin() {
        userLoginLabel.setText(ManagerAuth.getLogin());
    }
}