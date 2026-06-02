package org.example.gui.controllers;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.gui.Main;
import org.example.gui.commands.*;
import org.example.gui.managers.ManagerLanguage;
import org.example.gui.managers.ManagerAuth;
import org.example.gui.managers.ManagerValidation;
import org.example.gui.threads.GUIPrinter;
import org.example.packet.collection.Route;
import org.example.packet.collection.RouteClient;

import java.util.ArrayList;
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
    @FXML private Pane vizPane;

    // Toolbar buttons
    @FXML private Label mainTitle;
    @FXML private Button showBtn;
    @FXML private Button infoBtn;
    @FXML private Button addBtn;
    @FXML private Button addIfMaxBtn;
    @FXML private Button updateBtn;
    @FXML private Button removeIdBtn;
    @FXML private Button removeFirstBtn;
    @FXML private Button removeAllDistBtn;
    @FXML private Button clearBtn;
    @FXML private Button filterDistBtn;
    @FXML private Button avgDistBtn;
    @FXML private Button execScriptBtn;
    @FXML private Button helpBtn;
    @FXML private Button historyBtn;
    @FXML private Button logoutBtn;
    @FXML private Tab tableTab;
    @FXML private Tab vizTab;
    @FXML private Label infoPanelTitle;
    @FXML private Label statusLabel;
    @FXML private Label tablePlaceholder;
    @FXML private ComboBox<String> langCombo;

    private final Canvas vizCanvas = new Canvas();
    private List<Route> vizRoutes = new ArrayList<>();
    private final List<double[]> vizPoints = new ArrayList<>();

    private int hoveredRouteIndex = -1;
    private double pulsePhase = 0;
    private AnimationTimer pulseTimer;

    private final ManagerValidation validator = new ManagerValidation();

    @FXML
    private void initialize() {
        vizCanvas.widthProperty().bind(vizPane.widthProperty());
        vizCanvas.heightProperty().bind(vizPane.heightProperty());
        vizPane.getChildren().add(vizCanvas);
        vizCanvas.widthProperty().addListener(obs -> redrawViz());
        vizCanvas.heightProperty().addListener(obs -> redrawViz());
        vizCanvas.setOnMouseClicked(event -> onVizClick(event.getX(), event.getY()));
        vizCanvas.setOnMouseMoved(event -> onVizHover(event.getX(), event.getY()));
        vizCanvas.setOnMouseExited(event -> { stopPulse(); hoveredRouteIndex = -1; redrawViz(); });

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
            if (selected != null) showRouteDetails(selected);
        });

        langCombo.getItems().addAll("RU", "EN", "IT", "SL");
        langCombo.setValue(currentLangLabel());
        updateTexts();
        ManagerLanguage.setOnLangChange(this::updateTexts);

        Main.mainController = this;
        new Show().executeCommand(null, server, null);
    }

    @FXML
    private void onLangChange() {
        String val = langCombo.getValue();
        if (val == null) return;
        switch (val) {
            case "EN" -> ManagerLanguage.set(ManagerLanguage.EN);
            case "IT" -> ManagerLanguage.set(ManagerLanguage.IT);
            case "SL" -> ManagerLanguage.set(ManagerLanguage.SL);
            default   -> ManagerLanguage.set(ManagerLanguage.RU);
        }
    }

    private void updateTexts() {
        mainTitle.setText(ManagerLanguage.get("main.topbar.title"));
        showBtn.setText(ManagerLanguage.get("main.btn.show"));
        infoBtn.setText(ManagerLanguage.get("main.btn.info"));
        addBtn.setText(ManagerLanguage.get("main.btn.add"));
        addIfMaxBtn.setText(ManagerLanguage.get("main.btn.add_if_max"));
        updateBtn.setText(ManagerLanguage.get("main.btn.update"));
        removeIdBtn.setText(ManagerLanguage.get("main.btn.remove_id"));
        removeFirstBtn.setText(ManagerLanguage.get("main.btn.remove_first"));
        removeAllDistBtn.setText(ManagerLanguage.get("main.btn.remove_all_dist"));
        clearBtn.setText(ManagerLanguage.get("main.btn.clear"));
        filterDistBtn.setText(ManagerLanguage.get("main.btn.filter_dist"));
        avgDistBtn.setText(ManagerLanguage.get("main.btn.avg_dist"));
        execScriptBtn.setText(ManagerLanguage.get("main.btn.exec_script"));
        helpBtn.setText(ManagerLanguage.get("main.btn.help"));
        historyBtn.setText(ManagerLanguage.get("main.btn.history"));
        logoutBtn.setText(ManagerLanguage.get("main.btn.logout"));
        tableTab.setText(ManagerLanguage.get("main.tab.table"));
        vizTab.setText(ManagerLanguage.get("main.tab.viz"));
        infoPanelTitle.setText(ManagerLanguage.get("main.panel.title"));
        statusLabel.setText(ManagerLanguage.get("main.status.connected"));
        tablePlaceholder.setText(ManagerLanguage.get("main.placeholder"));
    }

    private String currentLangLabel() {
        return switch (ManagerLanguage.getCurrent()) {
            case ManagerLanguage.EN -> "EN";
            case ManagerLanguage.IT -> "IT";
            case ManagerLanguage.SL -> "SL";
            default      -> "RU";
        };
    }

    // ---- Visualization ----

    public void fillTableAndCanvas(List<Route> routes) {
        routeTable.setItems(FXCollections.observableArrayList(routes));
        vizRoutes = routes;
        redrawViz();
    }

    private void redrawViz() {
        double w = vizCanvas.getWidth();
        double h = vizCanvas.getHeight();
        GraphicsContext gc = vizCanvas.getGraphicsContext2D();

        gc.setFill(Color.web("#1a1a1a"));
        gc.fillRect(0, 0, w, h);

        if (w == 0 || h == 0) return;

        vizPoints.clear();

        if (vizRoutes.isEmpty()) {
            gc.setFill(Color.web("#555555"));
            gc.setFont(Font.font("Courier New", 14));
            gc.fillText(ManagerLanguage.get("main.placeholder"), w / 2 - 160, h / 2);
            return;
        }

        double margin = 60;

        long minX = vizRoutes.stream().mapToLong(r -> r.getCoordinates().getX()).min().orElse(0);
        long maxX = vizRoutes.stream().mapToLong(r -> r.getCoordinates().getX()).max().orElse(1);
        long minY = vizRoutes.stream().mapToLong(r -> r.getCoordinates().getY()).min().orElse(0);
        long maxY = vizRoutes.stream().mapToLong(r -> r.getCoordinates().getY()).max().orElse(1);

        if (minX == maxX) { minX -= 1; maxX += 1; }
        if (minY == maxY) { minY -= 1; maxY += 1; }

        double rangeX = maxX - minX;
        double rangeY = maxY - minY;

        int minDist = vizRoutes.stream().filter(r -> r.getDistance() != null).mapToInt(Route::getDistance).min().orElse(1);
        int maxDist = vizRoutes.stream().filter(r -> r.getDistance() != null).mapToInt(Route::getDistance).max().orElse(1);

        gc.setStroke(Color.web("#2a2a2a"));
        gc.setLineWidth(1);
        int gridLines = 8;
        for (int i = 0; i <= gridLines; i++) {
            double gx = margin + i * (w - 2 * margin) / gridLines;
            double gy = margin + i * (h - 2 * margin) / gridLines;
            gc.strokeLine(gx, margin, gx, h - margin);
            gc.strokeLine(margin, gy, w - margin, gy);
        }

        gc.setStroke(Color.web("#3a3a3a"));
        gc.setLineWidth(1.5);
        gc.strokeLine(margin, margin, margin, h - margin);
        gc.strokeLine(margin, h - margin, w - margin, h - margin);

        gc.setFill(Color.web("#555555"));
        gc.setFont(Font.font("Courier New", 10));
        gc.fillText(String.valueOf(minX), margin, h - margin + 14);
        gc.fillText(String.valueOf(maxX), w - margin - 20, h - margin + 14);
        gc.fillText(String.valueOf(minY), margin - 50, h - margin);
        gc.fillText(String.valueOf(maxY), margin - 50, margin + 4);
        gc.fillText("X", w - margin + 6, h - margin + 4);
        gc.fillText("Y", margin - 4, margin - 8);

        for (int i = 0; i < vizRoutes.size(); i++) {
            Route route = vizRoutes.get(i);
            long cx = route.getCoordinates().getX();
            long cy = route.getCoordinates().getY();

            double px = margin + (cx - minX) / rangeX * (w - 2 * margin);
            double py = (h - margin) - (cy - minY) / rangeY * (h - 2 * margin);

            int dist = route.getDistance() != null ? route.getDistance() : minDist;
            double radius = (maxDist == minDist) ? 10 : 6 + 20.0 * (dist - minDist) / (maxDist - minDist);

            vizPoints.add(new double[]{px, py, radius});

            boolean hovered = (i == hoveredRouteIndex);
            double pulseScale = hovered ? 1.0 + 0.35 * Math.sin(pulsePhase) : 1.0;
            double drawRadius = radius * pulseScale;

            if (hovered) {
                gc.setFill(Color.web("#5865f2", 0.12));
                gc.fillOval(px - drawRadius * 2.2, py - drawRadius * 2.2, drawRadius * 4.4, drawRadius * 4.4);
                gc.setFill(Color.web("#5865f2", 0.22));
                gc.fillOval(px - drawRadius * 1.5, py - drawRadius * 1.5, drawRadius * 3.0, drawRadius * 3.0);
            }

            gc.setFill(hovered ? Color.web("#7c87f5", 0.95) : Color.web("#5865f2", 0.85));
            gc.fillOval(px - drawRadius, py - drawRadius, drawRadius * 2, drawRadius * 2);
            gc.setStroke(hovered ? Color.web("#a0aaff") : Color.web("#7c87f5"));
            gc.setLineWidth(hovered ? 2.5 : 1.5);
            gc.strokeOval(px - drawRadius, py - drawRadius, drawRadius * 2, drawRadius * 2);

            gc.setFill(Color.web("#cccccc"));
            gc.setFont(Font.font("Courier New", 11));
            gc.fillText(route.getName(), px - 20, py + drawRadius + 14);
        }
    }

    private void onVizClick(double mx, double my) {
        for (int i = 0; i < vizPoints.size(); i++) {
            double[] pt = vizPoints.get(i);
            double dx = mx - pt[0];
            double dy = my - pt[1];
            if (Math.sqrt(dx * dx + dy * dy) <= pt[2] + 5) {
                GUIPrinter.showRouteInfo(vizRoutes.get(i));
                break;
            }
        }
    }

    private void onVizHover(double mx, double my) {
        int prev = hoveredRouteIndex;
        hoveredRouteIndex = -1;
        for (int i = 0; i < vizPoints.size(); i++) {
            double[] pt = vizPoints.get(i);
            double dx = mx - pt[0];
            double dy = my - pt[1];
            if (Math.sqrt(dx * dx + dy * dy) <= pt[2] + 8) {
                hoveredRouteIndex = i;
                break;
            }
        }
        if (hoveredRouteIndex != prev) {
            if (hoveredRouteIndex >= 0) {
                vizCanvas.setCursor(Cursor.HAND);
                startPulse();
            } else {
                vizCanvas.setCursor(Cursor.DEFAULT);
                stopPulse();
                redrawViz();
            }
        }
    }

    private void startPulse() {
        if (pulseTimer != null) return;
        pulseTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                pulsePhase += 0.07;
                if (pulsePhase > 2 * Math.PI) pulsePhase -= 2 * Math.PI;
                redrawViz();
            }
        };
        pulseTimer.start();
    }

    private void stopPulse() {
        if (pulseTimer != null) {
            pulseTimer.stop();
            pulseTimer = null;
        }
        pulsePhase = 0;
    }

    // ---- FXML actions ----

    @FXML
    private void onShowClick() {
        clearDetails();
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

    private void clearDetails() {
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
