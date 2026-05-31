package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.gui.managers.ManagerAuth;
import org.example.packet.CommandPacket;
import org.example.packet.ResponsePacket;
import org.example.packet.enums.Codes;

import java.io.IOException;

import static org.example.gui.Main.*;


public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private VBox loginBox;

    private StackPane root;

    @FXML
    private void onLoginClick() {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            CommandPacket packet = new CommandPacket(
                    "login",
                    null,
                    null,
                    login,
                    password
            );

            writeModule.writePacketForServer(server, packet);
            ResponsePacket responsePacket = readModule.readResponseForClient(server);

            if (responsePacket != null && responsePacket.getStatusCode().equals(Codes.OK)) {
                ManagerAuth.setLogin(login);
                ManagerAuth.setPassword(password);
                showMainWindow(login);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onRegisterLinkClick() {
        showRegisterWindow();
    }

    private void showMainWindow(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/fxml/main.fxml")
            );

            StackPane mainRoot = loader.load();

            MainController controller = loader.getController();
            controller.setUsername(username);

            StackPane root = (StackPane) loginBox.getParent();
            root.getChildren().clear();
            root.getChildren().add(mainRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRegisterWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/fxml/register.fxml")
            );
            StackPane registerRoot = loader.load();

            StackPane root = (StackPane) loginBox.getParent();
            root.getChildren().clear();
            root.getChildren().add(registerRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}