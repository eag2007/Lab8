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
        if (server == null || !server.isOpen()) {
            reconnectServer();
        }

        String login = regLoginField.getText();
        String password = regPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.equals(confirmPassword)) return;

        new Thread(() -> {

            if (Main.server == null || !Main.server.isOpen()) {
                Main.reconnectServer();
            }

            try {
                writeModule.writePacketForServer(server, new CommandPacket("register", null, null, login, password));
                ResponsePacket response = readModule.readResponseForClient(server);

                if (response != null && response.getStatusCode().equals(Codes.OK)) {
                    ManagerAuth.setLogin(login);
                    ManagerAuth.setPassword(password);
                    Main.startThreads();
                    SubscribeController.onSubscribe();
                    Platform.runLater(this::showMainWindow);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void onLoginLinkClick() {
        showLoginWindow();
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