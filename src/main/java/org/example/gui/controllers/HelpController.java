package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.gui.managers.ManagerLanguage;

/**
 * Контроллер обрабатыващий окно help при нажатии кнопки help
 */
public class HelpController {

    @FXML private TextArea helpText;
    @FXML private Label helpHeaderTitle;
    @FXML private Button helpButton;

    /**
     * Устанавливает текст справки по командам
     *
     * @param text - сообщение со справкой
     */
    public void setHelpText(String text) {
        helpText.setText(text);
        helpButton.setText(ManagerLanguage.get("close"));
    }

    /**
     * Закрывает окно help
     */
    @FXML
    private void onCloseClick() {
        Stage stage = (Stage) helpText.getScene().getWindow();
        stage.close();
    }

    /**
     * Локализация окна
     */
    public void setLocalizedTitle() {
        helpHeaderTitle.setText(ManagerLanguage.get("help.title"));
    }
}