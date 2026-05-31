package org.example.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.client.managers.ManagerResponseQueue;
import org.example.gui.controllers.MainController;
import org.example.gui.managers.ManagerAuth;
import org.example.gui.modules.ReadModule;
import org.example.gui.modules.WriteModule;
import org.example.gui.threads.GUIPrinter;
import org.example.gui.threads.ReaderThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Main extends Application {
    public static ManagerAuth managerAuth = new ManagerAuth();
    public static WriteModule writeModule = new WriteModule();
    public static ReadModule readModule = new ReadModule();
    public static SocketChannel server = null;
    public static ManagerResponseQueue managerResponseQueue = ManagerResponseQueue.getInstance();
    public static MainController mainController = null;

    private static GUIPrinter guiPrinter;
    private static ReaderThread readerThread;

    public static void startThreads() {
        readerThread = new ReaderThread(server, readModule);
        readerThread.start();
        guiPrinter = new GUIPrinter();
        guiPrinter.start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/login.fxml"));
        StackPane root = loader.load();
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();
    }

    @Override
    public void stop() {
        if (readerThread != null) readerThread.stopReader();
        if (guiPrinter   != null) guiPrinter.stopPrinter();
    }

    public static void main(String[] args) throws IOException {
        server = SocketChannel.open();
        server.configureBlocking(true);
        server.connect(new InetSocketAddress("localhost", 8080));
        launch(args);
    }
}