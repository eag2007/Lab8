package org.example.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.gui.managers.ManagerAuth;
import org.example.gui.modules.ReadModule;
import org.example.gui.modules.WriteModule;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Main extends Application {
    public static ManagerAuth managerAuth = new ManagerAuth();
    public static WriteModule writeModule = new WriteModule();
    public static ReadModule readModule = new ReadModule();
    public static SocketChannel server = null;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/fxml/login.fxml")
        );

        StackPane root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        server = SocketChannel.open();
        server.configureBlocking(true);
        server.connect(new InetSocketAddress("localhost", 8080));
        launch(args);
    }

}