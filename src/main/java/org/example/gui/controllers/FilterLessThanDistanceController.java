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
import org.example.gui.managers.ManagerLanguage;

import static org.example.gui.Main.server;

public class FilterLessThanDistanceController {

    @FXML private TextField distanceField;
    @FXML private Label errorLabel;

    public static void show(Stage owner) {
        try {
            FXMLLoader loader = new FXMLLoader(FilterLessThanDistanceController.class.getResource("/org/example/fxml/filter_less_than_distance.fxml"));
            loader.setResources(ManagerLanguage.getBundle());
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(ManagerLanguage.get("filter.dist.title"));
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
        String distanceText = distanceField.getText().trim();

        if (distanceText.isEmpty()) {
            errorLabel.setText(ManagerLanguage.get("error.distance.empty"));
            return;
        }

        try {
            int distance = Integer.parseInt(distanceText);
            if (distance <= 1) {
                errorLabel.setText(ManagerLanguage.get("error.distance.invalid"));
                return;
            }
            new FilterLessThanDistance().executeCommand(new String[]{distanceText}, server, null);
            closeWindow();
        } catch (NumberFormatException e) {
            errorLabel.setText(ManagerLanguage.get("error.distance.invalid"));
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
