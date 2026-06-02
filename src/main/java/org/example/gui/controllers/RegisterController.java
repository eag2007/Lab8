package org.example.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

public class RegisterController {

    @FXML private TextField regLoginField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private VBox registerBox;

    @FXML
    private void onRegisterClick() {
        String login = regLoginField.getText();
        String password = regPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (login.isBlank() || password.isBlank()) {
            alert("Заполните все поля");
            return;
        }
        if (!password.equals(confirmPassword)) {
            alert("Пароли не совпадают");
            return;
        }

        new Thread(() -> {
            if (!Main.connect()) {
                alertLater("Сервер недоступен. Попробуйте позже.");
                return;
            }
            try {
                writeModule.writePacketForServer(server,
                        new CommandPacket("register", null, null, login, password));
                ResponsePacket response = readModule.readResponseForClient(server);

                if (response != null && response.getStatusCode() == Codes.OK) {
                    ManagerAuth.setLogin(login);
                    ManagerAuth.setPassword(password);
                    Main.startThreads();
                    SubscribeController.onSubscribe();
                    Platform.runLater(this::showMainWindow);
                } else {
                    String msg = response != null ? response.getMessage() : "Нет ответа от сервера";
                    alertLater("Ошибка регистрации: " + msg);
                }
            } catch (Exception e) {
                alertLater("Сервер недоступен. Попробуйте позже.");
            }
        }).start();
    }

    @FXML
    private void onLoginLinkClick() {
        showLoginWindow();
    }

    private void alert(String msg) {
        AlertController.show("Регистрация", msg);
    }

    private void alertLater(String msg) {
        Platform.runLater(() -> alert(msg));
    }

    private void showMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/main.fxml"));
            Parent mainRoot = loader.load();
            MainController controller = loader.getController();
            controller.setUserLogin();
            Stage stage = (Stage) registerBox.getScene().getWindow();
            stage.getScene().setRoot(mainRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) registerBox.getScene().getWindow();
            stage.getScene().setRoot(loginRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}