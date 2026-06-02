package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.List;

public class AlertController {

    @FXML private Label titleLabel;
    @FXML private Label messageLabel;

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    @FXML
    private void onOkClick() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }

    public static void show(String title, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(AlertController.class.getResource("/org/example/fxml/alert.fxml"));
            Parent root = loader.load();
            AlertController controller = loader.getController();
            controller.setTitle(title);
            controller.setMessage(message);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            List<Window> windows = Stage.getWindows().stream()
                    .filter(Window::isShowing)
                    .toList();
            if (!windows.isEmpty()) {
                stage.initOwner(windows.get(0));
            }
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
