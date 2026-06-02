package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.gui.managers.ManagerLanguage;

public class InfoController {


    @FXML private TextArea infoText;
    @FXML private Label infoHeaderTitle;

    public void setInfo(String text) {
        infoText.setText(text);
    }

    @FXML
    private void onCloseClick() {
        Stage stage = (Stage) infoText.getScene().getWindow();
        stage.close();
    }

    public void setLocalizedTitle() {
        infoHeaderTitle.setText(ManagerLanguage.get("info.title"));
    }
}