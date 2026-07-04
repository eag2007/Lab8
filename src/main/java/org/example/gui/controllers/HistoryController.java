package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.gui.managers.ManagerLanguage;

/**
 * Контроллер обрабатывающий кнопку history
 */
public class HistoryController {


    @FXML
    private TextArea historyText;
    @FXML
    private Label historyHeaderTitle;

    /**
     * Установить историю команд
     *
     * @param text сообщение истории команд
     */
    public void setHistoryText(String text) {
        if (historyText != null && text != null) {
            historyText.setText(text);
        }
    }

    /**
     * Закрывает диалоговое окна по нажатию кнопки закрыть
     */
    @FXML
    private void onCloseClick() {
        Stage stage = (Stage) historyText.getScene().getWindow();
        stage.close();
    }

    /**
     * Ставит title согласный текущей локализации
     */
    public void setLocalizedTitle() {
        historyHeaderTitle.setText(ManagerLanguage.get("history.title"));
    }
}