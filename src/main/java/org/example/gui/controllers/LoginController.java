package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


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
        System.out.println("КНОПКА НАЖАТА!!!!!");  // ЭТО ДОЛЖНО ВЫВЕСТИСЬ
        String login = loginField.getText();
        String password = passwordField.getText();

//        try {
//            CommandPacket packet = new CommandPacket("login", null, null, login, password);
//            writeModule.writePacketForServer(server, packet);
//            ResponsePacket response = readModule.readResponseForClient(server);
//
////            if (response != null && response.getStatusCode() == Codes.OK) {
////                Client.login = inputLogin;
////                Client.password_hash = inputPassword;
////                managerInputOutput.writeLineIO("Вы вошли в аккаунт\n", Colors.GREEN);
////                return true;
////            }
//            //managerInputOutput.writeLineIO("Ошибка входа: " + (response != null ? response.getMessage() : "нет ответа") + "\n", Colors.RED);
//        } catch (Exception e) {
//            //managerInputOutput.writeLineIO("Соединение потеряно, переподключение...\n", Colors.YELLOW);
//            //closeServer();
//            //connect(currentPort);
//        }

        System.out.println(login);
        System.out.println(password);

        showMainWindow(login);
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