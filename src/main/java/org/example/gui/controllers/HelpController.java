package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class HelpController {

    @FXML
    private TextArea helpText;

    public void setHelpText(String text) {
        helpText.setText(text);
    }

    @FXML
    private void onCloseClick() {
        Stage stage = (Stage) helpText.getScene().getWindow();
        stage.close();
    }
}