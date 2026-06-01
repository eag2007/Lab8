package org.example.gui.commands;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.example.gui.Main;
import org.example.gui.managers.ManagerAuth;

public class Logout {

    public void executeCommand(Stage stage) {
        if (Main.getGuiPrinter() != null) {
            Main.getGuiPrinter().stopPrinter();
            Main.setGUIPrinter(null);
        }

        if (Main.getReaderThread() != null) {
            Main.getReaderThread().stopReader();
            Main.setReader(null);
        }

        ManagerAuth.setPassword(null);
        ManagerAuth.setLogin(null);

        Main.reconnectServer();

        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/org/example/fxml/login.fxml"));
            stage.getScene().setRoot(loginRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        return "logout - выходит из текущего юзера";
    }
}

