package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.gui.managers.ManagerLanguage;

/**
 * Контроллер обрабатывающий кнопку информации
 */
public class InfoController {


    @FXML private TextArea infoText;
    @FXML private Label infoHeaderTitle;

    /**
     * Установить текст в label окна info
     *
     * @param text сообщение которое надо установить
     */
    public void setInfo(String text) {
        infoText.setText(text);
    }

    /**
     * Обработка кнопки закрыть, закрывает диалоговое окно
     */
    @FXML
    private void onCloseClick() {
        Stage stage = (Stage) infoText.getScene().getWindow();
        stage.close();
    }

    /**
     * Устанавливает title окна с локализацией
     */
    public void setLocalizedTitle() {
        infoHeaderTitle.setText(ManagerLanguage.get("info.title"));
    }
}