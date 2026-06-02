package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.gui.commands.FilterLessThanDistance;

import static org.example.gui.Main.server;

public class FilterLessThanDistanceController {

    @FXML
    private TextField distanceField;
    @FXML
    private Label errorLabel;

    public static void show(Stage owner) {
        try {
            FXMLLoader loader = new FXMLLoader(FilterLessThanDistanceController.class.getResource("/org/example/fxml/filter_less_than_distance.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Фильтрация по distance");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(owner);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onFilterClick() {
        try {
            String distanceText = distanceField.getText().trim();

            if (distanceText.isEmpty()) {
                errorLabel.setText("Введите значение distance");
                return;
            }

            int distance = Integer.parseInt(distanceText);

            if (distance <= 1) {
                errorLabel.setText("Distance должен быть больше 1 (минимальное значение 2)");
                return;
            }

            new FilterLessThanDistance().executeCommand(new String[]{distanceText}, server, null);
            closeWindow();

        } catch (NumberFormatException e) {
            errorLabel.setText("Distance должен быть целым положительным числом");
        }
    }

    @FXML
    private void onCancelClick() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) distanceField.getScene().getWindow();
        stage.close();
    }
}