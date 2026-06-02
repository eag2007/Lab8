package org.example.gui.threads;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.gui.Main;
import org.example.gui.controllers.AverageOfDistanceController;
import org.example.gui.controllers.InfoController;
import org.example.gui.controllers.SubscribeController;
import org.example.packet.ResponsePacket;
import org.example.packet.collection.Route;
import org.example.packet.enums.Codes;
import org.example.packet.enums.ResponseType;

import java.util.List;
import java.util.Map;

import static org.example.gui.Main.managerResponseQueue;

public class GUIPrinter extends Thread {

    private volatile boolean running = true;

    public GUIPrinter() {
        super("gui-printer-thread");
        setDaemon(true);
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                ResponsePacket packet = managerResponseQueue.take();
                if (packet == null) {
                    continue;
                }
                handle(packet);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void handle(ResponsePacket packet) {
        if (packet.getType() == ResponseType.INFO) {
            handleInfo(packet);
        } else if (packet.getType() == ResponseType.SHOW) {
            handleShow(packet);
        } else if (packet.getType() == ResponseType.PUSH) {
            handlePush(packet);
        } else if (packet.getType() == ResponseType.AVERAGE_OF_DISTANCE) {
            handleAverage(packet);
        } else if (packet.getType() == ResponseType.FILTER_LESS_THAN_DISTANCE) {
            handleFilterLessThanDistance(packet);
        } else {
            return;
        }
    }

    @SuppressWarnings("unchecked")
    private void handleFilterLessThanDistance(ResponsePacket packet) {
        if (packet.getStatusCode() != Codes.OK) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Ошибка фильтрации: " + packet.getMessage());
                alert.showAndWait();
            });
            return;
        }
        List<Route> routes = (List<Route>) packet.getData();
        Platform.runLater(() -> {
            if (Main.mainController != null) {
                Main.mainController.fillTable(routes);
            }
        });
    }

    private void handleAverage(ResponsePacket packet) {
        if (packet.getStatusCode() != Codes.OK) {
            Platform.runLater(() -> showDialog("Ошибка", "Ошибка: " + packet.getMessage()));
            return;
        }
        double average = (double) packet.getData();
        Platform.runLater(() -> showAverageDialog(average));
    }

    private void showAverageDialog(double average) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/average_of_distance.fxml"));
            Parent root = loader.load();
            AverageOfDistanceController controller = loader.getController();
            controller.setAverage(average);

            Stage stage = new Stage();
            stage.setTitle("Среднее значение distance");
            stage.initModality(Modality.APPLICATION_MODAL);
            List<Window> windows = Stage.getWindows().stream()
                    .filter(Window::isShowing)
                    .toList();
            if (!windows.isEmpty()) {
                stage.initOwner(windows.get(0));
            }
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePush(ResponsePacket packet) {
        SubscribeController.execute();
    }

    private void handleInfo(ResponsePacket packet) {
        if (packet.getStatusCode() != Codes.OK) {
            Platform.runLater(() -> showDialog("Ошибка", "Ошибка команды info: " + packet.getMessage()));
            return;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) packet.getData();
        String text = "Количество элементов: " + map.get("size") + "\n" +
                "Время инициализации: " + map.get("initTime") + "\n" +
                "Тип данных: Route";
        Platform.runLater(() -> showDialog("Информация о коллекции", text));
    }

    @SuppressWarnings("unchecked")
    private void handleShow(ResponsePacket packet) {
        if (packet.getStatusCode() != Codes.OK) {
            Platform.runLater(() -> showDialog("Ошибка", "Ошибка команды show: " + packet.getMessage()));
            return;
        }
        List<Route> routes = (List<Route>) packet.getData();
        Platform.runLater(() -> {
            if (Main.mainController != null) {
                Main.mainController.fillTable(routes);
            }
        });
    }

    private void showDialog(String title, String text) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/info.fxml"));
            VBox root = loader.load();
            InfoController controller = loader.getController();
            controller.setInfo(text);

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            List<Window> windows = Stage.getWindows().stream()
                    .filter(Window::isShowing)
                    .toList();
            if (!windows.isEmpty()) {
                stage.initOwner(windows.get(0));
            }

            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPrinter() {
        running = false;
        interrupt();
    }
}