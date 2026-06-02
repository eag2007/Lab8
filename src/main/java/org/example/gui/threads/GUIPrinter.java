package org.example.gui.threads;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.example.gui.controllers.AlertController;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.gui.Main;
import org.example.gui.commands.Show;
import org.example.gui.controllers.AverageOfDistanceController;
import org.example.gui.controllers.HelpController;
import org.example.gui.controllers.HistoryController;
import org.example.gui.controllers.InfoController;
import org.example.gui.managers.ManagerCommands;
import org.example.packet.ResponsePacket;
import org.example.packet.collection.Route;
import org.example.packet.enums.Codes;
import org.example.packet.enums.ResponseType;

import java.util.List;
import java.util.Map;

import static org.example.gui.Main.managerResponseQueue;

public class GUIPrinter extends Thread {

    private volatile boolean running = true;


    /**
     * Инициализация gui-printer - потока отрисовки
     */
    public GUIPrinter() {
        super("gui-printer-thread");
        setDaemon(true);
    }


    /**
     * Запуск gui-printer - потока отрисовки
     */
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


    /**
     * Метод распределения ответов от сервера
     * @param packet - ответ с сервера
     */
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
        } else  if (packet.getType() == ResponseType.HELP) {
            handleHelp(packet);
        } else if (packet.getType() == ResponseType.HISTORY) {
            handleHistory(packet);
        } else {
            if (packet.getStatusCode() != Codes.OK
                    && packet.getStatusCode() != Codes.PUSH
                    && packet.getStatusCode() != Codes.PUSH_ERROR) {
                Platform.runLater(() -> AlertController.show("Ошибка", packet.getMessage()));
            }
        }
    }


    /**
     * Отрисовка команды filter_less_than_distance
     * @param packet - ответ с сервера
     */
    @SuppressWarnings("unchecked")
    private void handleFilterLessThanDistance(ResponsePacket packet) {
        if (packet.getStatusCode() != Codes.OK) {
            Platform.runLater(() -> AlertController.show("Ошибка", "Ошибка фильтрации: " + packet.getMessage()));
            return;
        }
        List<Route> routes = (List<Route>) packet.getData();
        Platform.runLater(() -> {
            if (Main.mainController != null) {
                Main.mainController.fillTable(routes);
            }
        });
    }


    /**
     * Отрисовка команды average_of_distance
     * @param packet - ответ с сервера
     */
    private void handleAverage(ResponsePacket packet) {
        if (packet.getStatusCode() != Codes.OK) {
            Platform.runLater(() -> showDialog("Ошибка", "Ошибка: " + packet.getMessage()));
            return;
        }
        double average = (double) packet.getData();
        Platform.runLater(() -> showAverageDialog(average));
    }


    /**
     * Отрисовка окна average_of_distance
     * @param average - среднее арифмитическое distance
     */
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


    /**
     * Обработчик push уведомлений, при push обновляет таблицу
     * @param packet - ответ с сервера
     */
    public void handlePush(ResponsePacket packet) {
        new Show().executeCommand(null, Main.server, null);
    }


    /**
     * Обработчик info
     * @param packet - ответ с сервера
     */
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


    /**
     * Обработчик show
     * @param packet - ответ с сервера
     */
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


    /**
     * Обработчик help команды
     * @param packet - заглушка
     */
    public void handleHelp(ResponsePacket packet) {
        String helpText = ManagerCommands.getHelp();
        Platform.runLater(() -> showHelpDialog("Справка по командам", helpText));
    }


    /**
     * Отрисовщик диалогового окна команды help
     * @param title - название окна
     * @param text - текст в окне
     */
    private void showHelpDialog(String title, String text) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/help.fxml"));
            Parent root = loader.load();
            HelpController controller = loader.getController();
            controller.setHelpText(text);

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


    /**
     * Отрисовщик диалогового окна команды history
     * @param title - название окна
     * @param text - текст в окне
     */
    private void showHistoryDialog(String title, String text) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/history.fxml"));
            Parent root = loader.load();
            HistoryController controller = loader.getController();
            controller.setHistoryText(text);

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


    /**
     * Обработчик команды history
     * @param packet - ответ с сервера
     */
    public void handleHistory(ResponsePacket packet) {
        String historyText = ManagerCommands.getHistory();
        Platform.runLater(() -> showHistoryDialog("История команд", historyText));
    }


    /**
     * Отрисовщик диалогового окна команды info
     * @param title - название окна
     * @param text - текст в окне
     */
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


    /**
     * Остановка gui-printer потока отрисовки
     */
    public void stopPrinter() {
        running = false;
        interrupt();
    }
}