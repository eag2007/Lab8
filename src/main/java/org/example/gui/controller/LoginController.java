package org.example.gui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.gui.GuiContext;
import org.example.packet.ResponsePacket;
import org.example.packet.enums.Codes;

public class LoginController {

    @FXML private TextField     hostField;
    @FXML private TextField     portField;
    @FXML private TextField     loginField;
    @FXML private PasswordField passField;
    @FXML private Label         errorLabel;
    @FXML private Button        loginBtn;
    @FXML private Button        registerBtn;

    @FXML private void handleLogin()    { doAuth("login"); }
    @FXML private void handleRegister() { doAuth("register"); }

    private void doAuth(String cmd) {
        String host     = hostField.getText().isBlank() ? "localhost" : hostField.getText().trim();
        String portText = portField.getText().isBlank() ? "8080"      : portField.getText().trim();
        String login    = loginField.getText().trim();
        String password = passField.getText().trim();

        errorLabel.setText("");
        if (login.isEmpty())    { errorLabel.setText("Введите логин");  return; }
        if (password.isEmpty()) { errorLabel.setText("Введите пароль"); return; }
        if (cmd.equals("register") && password.length() < 4) {
            errorLabel.setText("Пароль минимум 4 символа"); return;
        }

        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Неверный порт"); return;
        }

        setButtonsDisabled(true);
        errorLabel.setText("Подключение...");

        final int finalPort = port;
        Thread authThread = new Thread(() -> {
            try {
                GuiContext ctx = GuiContext.get();
                ctx.connect(host, finalPort);
                ctx.setLogin(login);
                ctx.setPasswordHash(password);

                ResponsePacket r = ctx.sendAndWait(cmd, null, null);

                Platform.runLater(() -> {
                    if (r != null && r.getStatusCode() == Codes.OK) {
                        openMain();
                    } else {
                        String msg = r != null ? r.getMessage() : "Нет ответа от сервера";
                        errorLabel.setText(msg);
                        GuiContext.get().setLogin(null);
                        GuiContext.get().setPasswordHash(null);
                        setButtonsDisabled(false);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    errorLabel.setText("Ошибка: " + e.getMessage());
                    setButtonsDisabled(false);
                });
            }
        }, "auth-thread");
        authThread.setDaemon(true);
        authThread.start();
    }

    private void openMain() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/main.fxml"));
            BorderPane root = loader.load();

            Stage stage = (Stage) loginField.getScene().getWindow();
            Scene scene = new Scene(root, 960, 680);
            scene.getStylesheets().add(
                    getClass().getResource("/css/style.css").toExternalForm());

            stage.setResizable(true);
            stage.setScene(scene);
            stage.setTitle("Route Manager — " + GuiContext.get().getLogin());

            GuiContext.get().startReader();

        } catch (Exception e) {
            errorLabel.setText("Ошибка: " + e.getMessage());
            setButtonsDisabled(false);
        }
    }

    private void setButtonsDisabled(boolean disabled) {
        loginBtn.setDisable(disabled);
        registerBtn.setDisable(disabled);
    }
}