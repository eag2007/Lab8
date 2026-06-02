package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class HistoryController {

    @FXML
    private TextArea historyText;

    public void setHistoryText(String text) {
        if (historyText != null && text != null) {
            historyText.setText(text);
        }
    }

    @FXML
    private void onCloseClick() {
        Stage stage = (Stage) historyText.getScene().getWindow();
        stage.close();
    }
}