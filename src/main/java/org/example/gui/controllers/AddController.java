package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.gui.commands.Add;
import org.example.gui.managers.ManagerValidation;
import org.example.packet.collection.RouteClient;

import org.example.gui.managers.ManagerLanguage;

import static org.example.gui.Main.server;

/**
 * Контроллер обработки кнопки add
 */
public class AddController {

    @FXML private TextField nameField;
    @FXML private TextField coordXField;
    @FXML private TextField coordYField;
    @FXML private TextField fromXField;
    @FXML private TextField fromYField;
    @FXML private TextField fromZField;
    @FXML private TextField toXField;
    @FXML private TextField toYField;
    @FXML private TextField toZField;
    @FXML private TextField distanceField;
    @FXML private TextField priceField;
    @FXML private Label errorLabel;

    private final ManagerValidation validator = new ManagerValidation();

    /**
     * Показывает диалоговое окно
     * @param owner - родительское окно
     */
    public static void show(Stage owner) {
        try {
            FXMLLoader loader = new FXMLLoader(AddController.class.getResource("/org/example/fxml/add.fxml"));
            loader.setResources(ManagerLanguage.getBundle());
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(ManagerLanguage.get("add.title"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(owner);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Обработчик нажатия на кнопку добавить
     */
    @FXML
    private void onAddClick() {
        try {
            RouteClient route = validator.validateFromFields(
                    nameField.getText(),
                    coordXField.getText(),
                    coordYField.getText(),
                    fromXField.getText(),
                    fromYField.getText(),
                    fromZField.getText(),
                    toXField.getText(),
                    toYField.getText(),
                    toZField.getText(),
                    distanceField.getText(),
                    priceField.getText()
            );

            new Add().executeCommand(null, server, route);
            closeWindow();

        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    /**
     * Обработчик кнопки отмены
     */
    @FXML
    private void onCancelClick() {
        closeWindow();
    }

    /**
     * Обработчик кнопки закрыть
     */
    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}