package org.example.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

        if (!password.equals(confirmPassword)) return;

        new Thread(() -> {
            try {
                writeModule.writePacketForServer(server, new CommandPacket("register", null, null, login, password));
                ResponsePacket response = readModule.readResponseForClient(server);

                if (response != null && response.getStatusCode().equals(Codes.OK)) {
                    ManagerAuth.setLogin(login);
                    ManagerAuth.setPassword(password);
                    Main.startThreads();
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
            StackPane mainRoot = loader.load();
            StackPane root = (StackPane) registerBox.getParent();
            root.getChildren().clear();
            root.getChildren().add(mainRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/login.fxml"));
            StackPane loginRoot = loader.load();
            StackPane root = (StackPane) regLoginField.getScene().getRoot();
            root.getChildren().clear();
            root.getChildren().add(loginRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}