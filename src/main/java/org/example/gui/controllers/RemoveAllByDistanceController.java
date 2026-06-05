package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.gui.commands.RemoveAllByDistance;
import org.example.gui.managers.ManagerLanguage;

import static org.example.gui.Main.server;

/**
 * Контроллер кнопки удаления по дистанции
 */
public class RemoveAllByDistanceController {

    @FXML
    private TextField distanceField;
    @FXML
    private Label errorLabel;

    /**
     * Диалоговое окно для ввода distance
     *
     * @param owner - принимает владельца (родительское окно)
     */
    public static void show(Stage owner) {
        try {
            FXMLLoader loader = new FXMLLoader(RemoveAllByDistanceController.class.getResource("/org/example/fxml/remove_all_by_distance.fxml"));
            loader.setResources(ManagerLanguage.getBundle());
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(ManagerLanguage.get("remove_all.title"));
            stage.initModality(Modality.APPLICATION_MODAL);
            /// установка владельца
            stage.initOwner(owner);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Кнопка удалить в диалоговом окне, считывает данные вызывает соответствующую команду
     */
    @FXML
    private void onRemoveClick() {
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
            new RemoveAllByDistance().executeCommand(new String[]{distanceText}, server, null);
            closeWindow();
        } catch (NumberFormatException e) {
            errorLabel.setText(ManagerLanguage.get("error.distance.invalid"));
        }
    }

    /**
     * Кнопка отмемы диалогового окна
     */
    @FXML
    private void onCancelClick() {
        closeWindow();
    }

    /**
     * Закрывает диалоговое окно
     */
    private void closeWindow() {
        Stage stage = (Stage) distanceField.getScene().getWindow();
        stage.close();
    }
}
