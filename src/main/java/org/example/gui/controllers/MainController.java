package org.example.gui.controllers;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static org.example.gui.Main.server;

public class MainController {

    /// Колонки
    @FXML
    private StackPane mainRoot;
    @FXML
    private TableView<Route> routeTable;
    @FXML
    private TableColumn<Route, Long> colId;
    @FXML
    private TableColumn<Route, String> colName;
    @FXML
    private TableColumn<Route, String> colCoordX;
    @FXML
    private TableColumn<Route, String> colCoordY;
    @FXML
    private TableColumn<Route, String> colFromX;
    @FXML
    private TableColumn<Route, String> colFromY;
    @FXML
    private TableColumn<Route, String> colFromZ;
    @FXML
    private TableColumn<Route, String> colToX;
    @FXML
    private TableColumn<Route, String> colToY;
    @FXML
    private TableColumn<Route, String> colToZ;
    @FXML
    private TableColumn<Route, Integer> colDistance;
    @FXML
    private TableColumn<Route, Integer> colPrice;
    @FXML
    private TableColumn<Route, String> colAuthor;
    @FXML
    private TableColumn<Route, String> colDate;

    /// Детали маршрута
    @FXML
    private Label routeIdLabel;
    @FXML
    private TextField routeNameField;
    @FXML
    private TextField routeCoordXField;
    @FXML
    private TextField routeCoordYField;
    @FXML
    private TextField routeFromXField;
    @FXML
    private TextField routeFromYField;
    @FXML
    private TextField routeFromZField;
    @FXML
    private TextField routeToXField;
    @FXML
    private TextField routeToYField;
    @FXML
    private TextField routeToZField;
    @FXML
    private TextField routeDistanceField;
    @FXML
    private TextField routePriceField;
    @FXML
    private Label routeAuthorLabel;
    @FXML
    private Label routeDateLabel;

    /// Визуализация
    @FXML
    private Label userLoginLabel;
    @FXML
    private Pane vizPane;


    /// Кнопки
    @FXML
    private Label mainTitle;
    @FXML
    private Button showBtn;
    @FXML
    private Button infoBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button addIfMaxBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private Button removeIdBtn;
    @FXML
    private Button removeFirstBtn;
    @FXML
    private Button removeAllDistBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private Button filterDistBtn;
    @FXML
    private Button avgDistBtn;
    @FXML
    private Button execScriptBtn;
    @FXML
    private Button helpBtn;
    @FXML
    private Button historyBtn;
    @FXML
    private Button logoutBtn;
    @FXML
    private Tab tableTab;
    @FXML
    private Tab vizTab;
    @FXML
    private Label infoPanelTitle;
    @FXML
    private Label statusLabel;
    @FXML
    private Label tablePlaceholder;
    @FXML
    private ComboBox<String> langCombo;

    private final Canvas vizCanvas = new Canvas();
    private List<Route> allRoutes = new ArrayList<>();
    private List<Route> vizRoutes = new ArrayList<>();
    private final List<double[]> vizPoints = new ArrayList<>();
    private final Map<String, TextField> columnFilters = new LinkedHashMap<>();

    private int hoveredRouteIndex = -1;
    private double pulsePhase = 0;
    private AnimationTimer pulseTimer;

    private final ManagerValidation validator = new ManagerValidation();

    /**
     * Инициализация канваса и таблицы
     */
    @FXML
    private void initialize() {
        vizCanvas.widthProperty().bind(vizPane.widthProperty());
        vizCanvas.heightProperty().bind(vizPane.heightProperty());
        vizPane.getChildren().add(vizCanvas);
        vizCanvas.widthProperty().addListener(obs -> redrawViz());
        vizCanvas.heightProperty().addListener(obs -> redrawViz());
        vizCanvas.setOnMouseClicked(event -> onVizClick(event.getX(), event.getY()));
        vizCanvas.setOnMouseMoved(event -> onVizHover(event.getX(), event.getY()));
        vizCanvas.setOnMouseExited(event -> {
            stopPulse();
            hoveredRouteIndex = -1;
            redrawViz();
        });

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
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                formatDate(cellData.getValue().getCreationDate().toLocalDateTime())));
        initColumnFilters();
        routeTable.setOnMouseClicked(event -> {
            Route selected = routeTable.getSelectionModel().getSelectedItem();
            if (selected != null) showRouteDetails(selected);
        });

        langCombo.getItems().addAll("RU", "EN", "IT", "SL");
        langCombo.setValue(currentLangLabel());
        updateTexts();
        ManagerLanguage.setOnLangChange(this::updateTexts);

        Main.mainController = this;

        colAuthor.setCellFactory(column -> new TableCell<Route, String>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle(empty || item == null ? "" : "-fx-text-fill: " + getAuthorColor(item) + ";");
            }
        });

        new Show().executeCommand(null, server, null);
    }

    /**
     * Обработчик смены языка локали
     */
    @FXML
    private void onLangChange() {
        String val = langCombo.getValue();
        if (val == null) {
            return;
        }
        switch (val) {
            case "EN" -> ManagerLanguage.set(ManagerLanguage.EN);
            case "IT" -> ManagerLanguage.set(ManagerLanguage.IT);
            case "SL" -> ManagerLanguage.set(ManagerLanguage.SL);
            default -> ManagerLanguage.set(ManagerLanguage.RU);
        }
        updateTexts();
        refreshDateColumn();
    }

    /**
     * Обновляет GUI под локаль кнопки
     */
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
        updateColumnFilterTexts();
    }

    /**
     * Текущая локализация выбор
     *
     * @return возвращает текущую локализацию
     */
    private String currentLangLabel() {
        return switch (ManagerLanguage.getCurrent()) {
            case ManagerLanguage.EN -> "EN";
            case ManagerLanguage.IT -> "IT";
            case ManagerLanguage.SL -> "SL";
            default -> "RU";
        };
    }

    /**
     * Рисует таблицу и канвас
     *
     * @param routes - список маршрутов
     */
    public void fillTableAndCanvas(List<Route> routes) {
        allRoutes = new ArrayList<>(routes);
        applyTableFilters();
    }

    /**
     * Инициализирует фильтры над каждой колонкой таблицы.
     */
    private void initColumnFilters() {
        addColumnFilter("id", colId);
        addColumnFilter("name", colName);
        addColumnFilter("coord_x", colCoordX);
        addColumnFilter("coord_y", colCoordY);
        addColumnFilter("from_x", colFromX);
        addColumnFilter("from_y", colFromY);
        addColumnFilter("from_z", colFromZ);
        addColumnFilter("to_x", colToX);
        addColumnFilter("to_y", colToY);
        addColumnFilter("to_z", colToZ);
        addColumnFilter("distance", colDistance);
        addColumnFilter("price", colPrice);
        addColumnFilter("author", colAuthor);
        addColumnFilter("date", colDate);
        updateColumnFilterTexts();
    }

    /**
     * Добавляет фильтр к колонке.
     *
     * @param key    - ключ колонки
     * @param column - колонка таблицы
     */
    private void addColumnFilter(String key, TableColumn<Route, ?> column) {
        Label title = new Label();
        title.getStyleClass().add("table-filter-title");

        TextField filter = new TextField();
        filter.getStyleClass().add("table-filter-field");
        filter.setMinWidth(45);
        filter.setPrefWidth(column.getPrefWidth() - 10);
        filter.textProperty().addListener((obs, oldValue, newValue) -> applyTableFilters());
        filter.setOnMouseClicked(Event::consume);

        VBox header = new VBox(4, title, filter);
        header.getStyleClass().add("table-filter-header");
        column.setGraphic(header);
        column.setText(null);
        columnFilters.put(key, filter);
    }

    /**
     * Обновляет локализацию заголовков и подсказок фильтров.
     */
    private void updateColumnFilterTexts() {
        for (Map.Entry<String, TextField> entry : columnFilters.entrySet()) {
            VBox header = (VBox) entry.getValue().getParent();
            Label title = (Label) header.getChildren().get(0);
            title.setText(ManagerLanguage.get("table.column." + entry.getKey()));
            entry.getValue().setPromptText(ManagerLanguage.get("table.filter.prompt"));
        }
    }

    /**
     * Фильтрует таблицу через Stream API.
     */
    private void applyTableFilters() {
        /// фильтруем по соответсвующим фильтрам
        List<Route> filteredRoutes = allRoutes.stream()
                .filter(route -> columnFilters.entrySet().stream()
                        .allMatch(filter -> matchesColumnFilter(route, filter.getKey(), filter.getValue().getText())))
                .toList();

        /// устанавливаем в таблицу и обновляет детали маршрута
        routeTable.setItems(FXCollections.observableArrayList(filteredRoutes));
        vizRoutes = filteredRoutes;
        Route selected = routeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            clearDetails();
        }
        /// перерисовываем визуалицию
        redrawViz();
    }

    /**
     * Проверяет совпадение значения колонки с фильтром.
     *
     * @param route  - маршрут
     * @param column - ключ колонки
     * @param filter - значение фильтра
     * @return true если строка подходит
     */
    private boolean matchesColumnFilter(Route route, String column, String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }
        String value = getColumnValue(route, column).toLowerCase(Locale.ROOT);
        String needle = filter.trim().toLowerCase(Locale.ROOT);
        return value.contains(needle);
    }

    /**
     * Достаёт строковое значение колонки.
     *
     * @param route  - маршрут
     * @param column - ключ колонки
     * @return значение колонки
     */
    private String getColumnValue(Route route, String column) {
        return switch (column) {
            case "id" -> String.valueOf(route.getId());
            case "name" -> Objects.toString(route.getName(), "");
            case "coord_x" -> String.valueOf(route.getCoordinates().getX());
            case "coord_y" -> String.valueOf(route.getCoordinates().getY());
            case "from_x" -> String.valueOf(route.getFrom().getX());
            case "from_y" -> String.valueOf(route.getFrom().getY());
            case "from_z" -> String.valueOf(route.getFrom().getZ());
            case "to_x" -> String.valueOf(route.getTo().getX());
            case "to_y" -> String.valueOf(route.getTo().getY());
            case "to_z" -> String.valueOf(route.getTo().getZ());
            case "distance" -> Objects.toString(route.getDistance(), "");
            case "price" -> Objects.toString(route.getPrice(), "");
            case "author" -> Objects.toString(route.getAuthor(), "");
            case "date" -> formatDate(route.getCreationDate().toLocalDateTime());
            default -> "";
        };
    }

    /**
     * Отрисовывает график с точками маршрутов
     */
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

        if (minX == maxX) {
            minX -= 1;
            maxX += 1;
        }
        if (minY == maxY) {
            minY -= 1;
            maxY += 1;
        }

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

            String authorHex = getAuthorColor(route.getAuthor());
            Color authorColor = Color.web(authorHex, 0.85);
            Color hoverColor = Color.web(authorHex, 1.0);
            gc.setFill(hovered ? hoverColor : authorColor);
            gc.fillOval(px - drawRadius, py - drawRadius, drawRadius * 2, drawRadius * 2);
            gc.setStroke(hovered ? hoverColor : authorColor);
            gc.setLineWidth(hovered ? 2.5 : 1.5);
            gc.setLineWidth(hovered ? 2.5 : 1.5);
            gc.strokeOval(px - drawRadius, py - drawRadius, drawRadius * 2, drawRadius * 2);

            gc.setFill(Color.web("#cccccc"));
            gc.setFont(Font.font("Courier New", 11));
            gc.fillText(route.getName(), px - 20, py + drawRadius + 14);
        }
    }

    /**
     * Обработка нажатия на элемент визуализации
     *
     * @param mx - координата мыши x
     * @param my - координата мыши y
     */
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

    /**
     * Обработка наведения мыши на элемент визуализации
     *
     * @param mx - координата мыши x
     * @param my - координата мыши y
     */
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

    /**
     * Включение анимации визуализации
     */
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

    /**
     * Остановка анимации визуализации
     */
    private void stopPulse() {
        if (pulseTimer != null) {
            pulseTimer.stop();
            pulseTimer = null;
        }
        pulsePhase = 0;
    }

    /**
     * Обработчик кнопки показать
     */
    @FXML
    private void onShowClick() {
        clearDetails();
        new Show().executeCommand(null, server, null);
    }

    /**
     * Обработчик кнопки информации
     */
    @FXML
    private void onInfoClick() {
        new Info().executeCommand(null, server, null);
    }

    /**
     * Обработчик кнопки выхода
     */
    @FXML
    private void onLogoutClick() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        new Logout().executeCommand(null, server, stage);
    }

    /**
     * Отрисовывает детали маршрута при его выборе
     *
     * @param route - Маршрут
     */
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
        routeDateLabel.setText(formatDate(route.getCreationDate().toLocalDateTime()));
    }

    /**
     * Очищает все поля части: Детали маршрута
     */
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

    /**
     * Обработчик кнопки добавить
     */
    @FXML
    private void onAddClick() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        AddController.show(stage);
    }

    /**
     * Обработчик кнопки add_if_max
     */
    @FXML
    private void onAddIfMaxClick() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        AddIfMaxController.show(stage);
    }

    /**
     * Обработчик кнопки очистки коллекции
     */
    @FXML
    private void onClearClick() {
        ClearController.onClearCLick();
        onShowClick();
    }

    /**
     * Обработчик кнопки удаления первого элемента
     */
    @FXML
    private void onRemoveFirstClick() {
        if (allRoutes.isEmpty()) {
            AlertController.show("warning.title", ManagerLanguage.get("error.collection.empty"));
            return;
        }
        Route firstRoute = allRoutes.get(0);
        if (!isRouteUser(firstRoute)) {
            AlertController.show("error.title", ManagerLanguage.get("error.route.not_owner"));
            return;
        }
        RemoveFirstController.onRemoveFirstClick();
        onShowClick();
    }

    /**
     * Обработчик кнопки удаления объекта по id
     */
    @FXML
    private void onRemoveByIdClick() {
        Route selected = routeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (!isRouteUser(selected)) {
                AlertController.show("error.title", ManagerLanguage.get("error.route.not_owner"));
                return;
            }
            long id = selected.getId();
            RemoveByIdController.onRemoveByIdControllerClick(id);
            onShowClick();
        } else {
            AlertController.show("warning.title", ManagerLanguage.get("error.route.not_selected.remove"));
        }
    }

    /**
     * Обработчик кнопки удаления всех объектов
     */
    @FXML
    private void onRemoveAllDistClick() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        RemoveAllByDistanceController.show(stage);
        onShowClick();
    }

    /**
     * Обработчик кнопки подсчёта среднего арифмит. дистанции
     */
    @FXML
    private void onAverageDistanceClick() {
        AverageOfDistanceController.onAverageDistanceClick();
    }

    /**
     * Обработчик кнопки filter_less_than_distance
     */
    @FXML
    private void onFilterDistClick() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        FilterLessThanDistanceController.show(stage);
    }

    /**
     * Обработчик кнопки выполнения скрипта
     */
    @FXML
    private void onExecuteScriptClick() {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        ExecuteScriptController.execute(stage);
    }

    /**
     * Обработчик кнопки обновить
     */
    @FXML
    private void onUpdateClick() {
        Route selected = routeTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AlertController.show("warning.title", ManagerLanguage.get("error.route.not_selected.update"));
            return;
        }

        if (!isRouteUser(selected)) {
            AlertController.show("error.title", ManagerLanguage.get("error.route.not_owner"));
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
            AlertController.show("error.title", e.getMessage());
        }
    }

    @FXML
    private void onHelpClick() {
        new Help().executeCommand(null, server, null);
    }

    /**
     * Обработчик кнопки истории
     */
    @FXML
    private void onHistoryClick() {
        new History().executeCommand(null, server, null);
    }

    /**
     * Обновляет колонку с датой в таблице
     */
    private void refreshDateColumn() {
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                formatDate(cellData.getValue().getCreationDate().toLocalDateTime())
        ));
        applyTableFilters();
    }

    /**
     * Устанавливает название аккаунта в правом верхнем углу
     */
    public void setUserLogin() {
        userLoginLabel.setText(ManagerAuth.getLogin());
    }

    /**
     * Форматирует дату под локальный формат
     *
     * @param dateTime - текущее время
     * @return строку с датой в соответсвующей локали
     */
    public String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        String pattern = ManagerLanguage.get("date.format");
        if (pattern == null || pattern.isEmpty()) {
            pattern = "yyyy-MM-dd HH:mm";
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Генерирует разные цвета для разных авторов
     *
     * @param author - значения поля author в колонке
     * @return вовзращает цвет
     */
    private String getAuthorColor(String author) {
        int hash = Math.abs(author.hashCode());
        int r = 100 + (hash % 156);
        int g = 100 + ((hash / 100) % 156);
        int b = 100 + ((hash / 10000) % 156);
        return String.format("#%02X%02X%02X", r, g, b);
    }

    /**
     * Проверяет, принадлежит ли маршрут текущему пользователю.
     *
     * @param route - маршрут
     * @return true если текущий пользователь владелец маршрута
     */
    private boolean isRouteUser(Route route) {
        return route == null || ManagerAuth.getLogin().equals(route.getAuthor());
    }
}
