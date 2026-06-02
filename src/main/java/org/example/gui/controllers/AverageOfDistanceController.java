package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.gui.commands.AverageOfDistance;

import static org.example.gui.Main.server;

public class AverageOfDistanceController {

    @FXML private Label averageValue;

    public void setAverage(double average) {
        averageValue.setText(String.valueOf(average));
    }

    @FXML
    private void onCloseClick() {
        Stage stage = (Stage) averageValue.getScene().getWindow();
        stage.close();
    }

    public static void onAverageDistanceClick() {
        new AverageOfDistance().executeCommand(null, server, null);
    }
}