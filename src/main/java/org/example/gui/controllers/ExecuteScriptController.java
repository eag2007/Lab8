package org.example.gui.controllers;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.gui.Main;
import org.example.gui.commands.ExecuteScript;
import org.example.gui.commands.Show;

import java.io.File;
import java.util.List;

import static org.example.gui.Main.server;

public class ExecuteScriptController {

    private ExecuteScriptController() {
    }

    public static void execute(Stage owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл скрипта");
        File file = chooser.showOpenDialog(owner);
        if (file == null) {
            return;
        }

        new Thread(() -> {
            List<String> errors = ExecuteScript.run(file);
            Platform.runLater(() -> {
                if (!errors.isEmpty()) {
                    AlertController.show("Ошибки при выполнении скрипта",
                            "Скрипт '" + file.getName() + "':\n\n" + String.join("\n", errors));
                }
                if (Main.mainController != null) {
                    new Show().executeCommand(null, server, null);
                }
            });
        }, "execute-script-thread").start();
    }
}