package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private StackPane mainRoot;

    public void setUsername(String name) {
        System.out.println("pop");
    }

    @FXML
    private void onLogoutClick() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/fxml/login.fxml")
            );
            StackPane loginRoot = loader.load();

            StackPane root = (StackPane) mainRoot.getScene().getRoot();
            root.getChildren().clear();
            root.getChildren().add(loginRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}