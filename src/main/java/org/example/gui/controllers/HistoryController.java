package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.gui.managers.ManagerLanguage;

public class HistoryController {


    @FXML private TextArea historyText;
    @FXML private Label historyHeaderTitle;

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

    public void setLocalizedTitle() {
        historyHeaderTitle.setText(ManagerLanguage.get("history.title"));
    }
}