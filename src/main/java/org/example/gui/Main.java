package org.example.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.example.gui.controllers.AlertController;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.client.managers.ManagerResponseQueue;
import org.example.gui.controllers.MainController;
import org.example.gui.controllers.SubscribeController;
import org.example.gui.managers.ManagerAuth;
import org.example.gui.modules.ReadModule;
import org.example.gui.modules.WriteModule;
import org.example.gui.threads.GUIPrinter;
import org.example.gui.threads.ReaderThread;
import org.example.packet.CommandPacket;
import org.example.packet.ResponsePacket;
import org.example.packet.enums.Codes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Main extends Application {
    public static ManagerResponseQueue managerResponseQueue = ManagerResponseQueue.getInstance();

    public static WriteModule writeModule = new WriteModule();
    public static ReadModule readModule = new ReadModule();

    public static volatile SocketChannel server = null;

    public static MainController mainController = null;

    private static GUIPrinter guiPrinter;
    private static ReaderThread readerThread;

    private static int currentPort = 8080;


    /**
     * Открывает соединение с сервером*
     *
     * @return true - если соединение установилось false - если нет
     */
    public static boolean connect() {
        closeServer();
        try {
            server = SocketChannel.open();
            server.configureBlocking(true);
            server.connect(new InetSocketAddress("localhost", currentPort));
            return true;
        } catch (IOException e) {
            server = null;
            return false;
        }
    }


    /**
     * Закрывает соединение с сервером
     */
    private static void closeServer() {
        try {
            if (server != null && server.isOpen()) {
                server.close();
            }
        } catch (IOException ignored) {
        }
        server = null;
    }


    /**
     * Проверяет соединение с помощью пинга
     *
     * @return true - если соединение живо, false - если мёртво
     */
    private static boolean isAlive() {
        try {
            if (server == null || !server.isOpen()) {
                return false;
            }
            server.socket().sendUrgentData(0);
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * Повторный вход с сохранёнными логином/паролем (пользователь ничего не вводит).
     *
     * @return true - если вошёл, false - если не удалось
     */
    private static boolean relogin() {
        try {
            writeModule.writePacketForServer(server,
                    new CommandPacket("login", null, null, ManagerAuth.getLogin(), ManagerAuth.getPassword()));
            ResponsePacket response = readModule.readResponseForClient(server);
            Platform.runLater(SubscribeController::onSubscribe);
            return response != null && response.getStatusCode() == Codes.OK;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Убедиться, что соединение живо. Если оно потеряно — переподключиться и
     * восстановить сессию (без участия пользователя). true — соединение готово.
     */
    public static synchronized boolean ensureConnection() {
        if (ManagerAuth.getLogin() == null) {
            return isAlive();
        }
        if (isAlive()) {
            return true;
        }
        stopThreads();
        if (!connect() || !relogin()) {
            return false;
        }
        startThreads();
        return true;
    }


    /**
     * Отправляет команду. При недоступности сервера показывает Alert.
     *
     * @param packet -  принимает объект типа CommandPacket
     * @return true - если отправка удалась успешно, false - если сервер недоступен
     */
    public static boolean send(CommandPacket packet) {
        if (!ensureConnection()) {
            showError("Сервер недоступен. Команда не выполнена.\n"
                    + "Соединение восстановится автоматически — повторный вход не требуется.");
            return false;
        }
        try {
            writeModule.writePacketForServer(server, packet);
            return true;
        } catch (IOException e) {
            showError("Сервер недоступен. Команда не выполнена.");
            return false;
        }
    }


    /**
     * Отправить команду молча (для системных/скриптовых вызовов).
     * Отправляет команду на сервер
     *
     * @param packet - объект типа CommandPacket
     *
     */
    public static boolean sendQuiet(CommandPacket packet) {
        if (!ensureConnection()) {
            return false;
        }
        try {
            writeModule.writePacketForServer(server, packet);
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * Выводит Alert - с описанием ошибки
     *
     * @param message - сообщение об ошибке
     */
    private static void showError(String message) {
        Runnable r = () -> AlertController.show("Ошибка", message);
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }


    /**
     * запуск потока приложения,
     * потока чтения данных с сервера (Reader),
     * потока отображения принятых данных
     */
    public static void startThreads() {
        stopThreads();
        readerThread = new ReaderThread(server, readModule);
        readerThread.start();
        guiPrinter = new GUIPrinter();
        guiPrinter.start();
    }


    /**
     * остановка потоков Reader, GUIPrinter, основного потока приложения
     *
     * @see #stopThreads()
     */
    public static void stopThreads() {
        if (readerThread != null) {
            readerThread.stopReader();
            readerThread = null;
        }
        if (guiPrinter != null) {
            guiPrinter.stopPrinter();
            guiPrinter = null;
        }
    }


    /**
     * Отрисовка стартого окна авторищации
     *
     * @param stage - страница на которой будем рисовать
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/login.fxml"));
        StackPane root = loader.load();
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();
    }


    /**
     * Остановка потоков и закрытие соединения
     */
    @Override
    public void stop() {
        stopThreads();
        closeServer();
    }

    /**
     * Старт приложения даже если сервер не доступен
     */
    public static void main(String[] args) {
        connect();
        launch(args);
    }

    public static ReaderThread getReaderThread() {
        return readerThread;
    }

    public static GUIPrinter getGuiPrinter() {
        return guiPrinter;
    }

    public static void setGUIPrinter(GUIPrinter value) {
        guiPrinter = value;
    }

    public static void setReader(ReaderThread value) {
        readerThread = value;
    }

    public static void reconnectServer() {
        connect();
    }
}