package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class RegisterController {

    @FXML
    private TextField regLoginField;

    @FXML
    private PasswordField regPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private void onRegisterClick() {
        String login = regLoginField.getText();
        String password = regPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();

        System.out.println("Регистрация:");
        System.out.println("Логин: " + login);
        System.out.println("Пароль: " + password);
        System.out.println("Email: " + email);


        showLoginWindow();

    }

    @FXML
    private void onLoginLinkClick() {
        showLoginWindow();
    }

    private void showLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/fxml/main.fxml")
            );
            StackPane loginRoot = loader.load();

            StackPane root = (StackPane) regLoginField.getScene().getRoot();
            root.getChildren().clear();
            root.getChildren().add(loginRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}