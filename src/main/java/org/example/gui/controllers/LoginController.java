package org.example.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.gui.Main;
import org.example.gui.managers.ManagerAuth;
import org.example.packet.CommandPacket;
import org.example.packet.ResponsePacket;
import org.example.packet.enums.Codes;

import static org.example.gui.Main.*;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private VBox loginBox;
    @FXML private Label errorLabel;

    @FXML
    private void onLoginClick() {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login.isBlank() || password.isBlank()) {
            setError("Заполните все поля");
            return;
        }

        new Thread(() -> {
            if (!Main.connect()) {
                setErrorLater("Сервер недоступен. Попробуйте позже.");
                return;
            }
            try {
                writeModule.writePacketForServer(server,
                        new CommandPacket("login", null, null, login, password));
                ResponsePacket response = readModule.readResponseForClient(server);

                if (response != null && response.getStatusCode() == Codes.OK) {
                    ManagerAuth.setLogin(login);
                    ManagerAuth.setPassword(password);
                    Main.startThreads();
                    SubscribeController.onSubscribe();
                    Platform.runLater(this::showMainWindow);
                } else {
                    String msg = response != null ? response.getMessage() : "Нет ответа от сервера";
                    setErrorLater("Ошибка: " + msg);
                }
            } catch (Exception e) {
                setErrorLater("Сервер недоступен. Попробуйте позже.");
            }
        }).start();
    }

    @FXML
    private void onRegisterLinkClick() {
        showRegisterWindow();
    }

    private void setError(String text) {
        if (errorLabel != null) errorLabel.setText(text);
    }

    private void setErrorLater(String text) {
        Platform.runLater(() -> setError(text));
    }

    private void showMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/main.fxml"));
            Parent mainRoot = loader.load();
            MainController controller = loader.getController();
            controller.setUserLogin();
            Stage stage = (Stage) loginBox.getScene().getWindow();
            stage.getScene().setRoot(mainRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRegisterWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/register.fxml"));
            Parent registerRoot = loader.load();
            Stage stage = (Stage) loginBox.getScene().getWindow();
            stage.getScene().setRoot(registerRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}