package org.example.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.gui.GuiContext;
import org.example.packet.ResponsePacket;

public class MainController {

    @FXML private Label            userLabel;
    @FXML private Label            statusLabel;
    @FXML private ComboBox<String> langCombo;

    @FXML
    public void initialize() {
        userLabel.setText(GuiContext.get().getLogin());

        langCombo.getItems().addAll("RU", "EN", "IT", "SI");
        langCombo.setValue("RU");

        GuiContext.get().setPushHandler(this::handlePush);
        GuiContext.get().sendCommand("show", null, null);

        statusLabel.setText("Подключено как " + GuiContext.get().getLogin());
    }

    private void handlePush(ResponsePacket packet) {
        statusLabel.setText("Получено обновление от сервера");
    }

    @FXML private void handleAdd()    { statusLabel.setText("add — в разработке"); }
    @FXML private void handleDelete() { statusLabel.setText("delete — в разработке"); }
    @FXML private void handleClear()  { GuiContext.get().sendCommand("clear", null, null); }
}