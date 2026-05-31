package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.packet.CommandPacket;
import org.example.packet.ResponsePacket;
import org.example.packet.enums.Codes;

import java.io.IOException;

import static org.example.gui.Main.*;

public class RegisterController {

    @FXML
    private TextField regLoginField;

    @FXML
    private PasswordField regPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private VBox registerBox;

    @FXML
    private void onRegisterClick() {
        String login = regLoginField.getText();
        String password = regPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (password.equals(confirmPassword)) {
            try {
                CommandPacket commandPacket = new CommandPacket("register", null, null, login, password);
                writeModule.writePacketForServer(server, commandPacket);
                ResponsePacket responsePacket = readModule.readResponseForClient(server);

                if (responsePacket.getStatusCode().equals(Codes.OK)) {
                    showMainWindow(login);
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        // showLoginWindow();

    }

    @FXML
    private void onLoginLinkClick() {
        showLoginWindow();
    }

    private void showMainWindow(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/fxml/main.fxml")
            );

            StackPane mainRoot = loader.load();

            MainController controller = loader.getController();
            controller.setUsername(username);

            StackPane root = (StackPane) registerBox.getParent();
            root.getChildren().clear();
            root.getChildren().add(mainRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
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