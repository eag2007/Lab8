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

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private VBox loginBox;

    @FXML
    private void onLoginClick() {
        String login = loginField.getText();
        String password = passwordField.getText();

        new Thread(() -> {
            try {
                writeModule.writePacketForServer(server, new CommandPacket("login", null, null, login, password));
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
    private void onRegisterLinkClick() {
        showRegisterWindow();
    }

    private void showMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/main.fxml"));
            StackPane mainRoot = loader.load();
            StackPane root = (StackPane) loginBox.getParent();
            root.getChildren().clear();
            root.getChildren().add(mainRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRegisterWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/register.fxml"));
            StackPane registerRoot = loader.load();
            StackPane root = (StackPane) loginBox.getParent();
            root.getChildren().clear();
            root.getChildren().add(registerRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}