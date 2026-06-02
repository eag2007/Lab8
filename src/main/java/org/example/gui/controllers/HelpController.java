package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.gui.managers.ManagerLanguage;

public class HelpController {

    @FXML private TextArea helpText;
    @FXML private Label helpHeaderTitle;

    public void setHelpText(String text) {
        helpText.setText(text);
    }

    @FXML
    private void onCloseClick() {
        Stage stage = (Stage) helpText.getScene().getWindow();
        stage.close();
    }

    public void setLocalizedTitle() {
        helpHeaderTitle.setText(ManagerLanguage.get("help.title"));
    }
}