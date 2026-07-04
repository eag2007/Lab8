package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.gui.commands.AverageOfDistance;

import static org.example.gui.Main.server;

/**
 * Контроллер обработки кнопки average_of_distance
 */
public class AverageOfDistanceController {

    @FXML private Label averageValue;

    /**
     * Устанавливает значение в label
     * @param average - среднее арифметическое поля distannce
     */
    public void setAverage(double average) {
        averageValue.setText(String.valueOf(average));
    }

    /**
     * Закрывает диалоговое окно
     */
    @FXML
    private void onCloseClick() {
        Stage stage = (Stage) averageValue.getScene().getWindow();
        stage.close();
    }

    public static void onAverageDistanceClick() {
        new AverageOfDistance().executeCommand(null, server, null);
    }
}