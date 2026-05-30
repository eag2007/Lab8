package org.example.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.gui.managers.ManagerAuth;
import org.example.gui.modules.ReadModule;
import org.example.gui.modules.WriteModule;

public class Main extends Application {
    public static ManagerAuth managerAuth = new ManagerAuth();
    public static WriteModule writeModule = new WriteModule();
    public static ReadModule readModule = new ReadModule();

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/fxml/login.fxml")
        );

        StackPane root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}