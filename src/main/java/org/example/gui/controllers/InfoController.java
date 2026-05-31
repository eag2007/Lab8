package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class InfoController {

    @FXML
    private TextArea infoText;

    public void setInfo(String text) {
        infoText.setText(text);
    }

    @FXML
    private void onCloseClick() {
        Stage stage = (Stage) infoText.getScene().getWindow();
        stage.close();
    }
}