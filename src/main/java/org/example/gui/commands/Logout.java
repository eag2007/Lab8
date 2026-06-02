package org.example.gui.commands;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.example.gui.Main;
import org.example.gui.interfaces.Command;
import org.example.gui.managers.ManagerAuth;
import org.example.gui.managers.ManagerCommands;

import java.nio.channels.SocketChannel;

public class Logout implements Command {

    @Override
    public void executeCommand(String[] args, SocketChannel serverChannel, Object stage) {
        ManagerCommands.addToHistory("logout");
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
            ((Stage) stage).getScene().setRoot(loginRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        return "logout - выходит из текущего юзера";
    }
}

