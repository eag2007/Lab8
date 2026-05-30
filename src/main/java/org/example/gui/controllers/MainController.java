package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private Label welcomeLabel;

    public void setUsername(String name) {
        welcomeLabel.setText("Привет, " + name + "!");
    }

    @FXML
    private void onLogoutClick() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/fxml/login.fxml")
            );
            StackPane loginRoot = loader.load();

            StackPane root = (StackPane) welcomeLabel.getScene().getRoot();
            root.getChildren().clear();
            root.getChildren().add(loginRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}