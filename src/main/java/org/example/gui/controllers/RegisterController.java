package org.example.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.gui.Main;
import org.example.gui.managers.ManagerLanguage;
import org.example.gui.managers.ManagerAuth;
import org.example.packet.CommandPacket;
import org.example.packet.ResponsePacket;
import org.example.packet.enums.Codes;

import static org.example.gui.Main.*;

public class RegisterController {

    @FXML
    private TextField regLoginField;
    @FXML
    private PasswordField regPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private VBox registerBox;
    @FXML
    private Label titleLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private Button registerBtn;
    @FXML
    private Hyperlink loginLink;
    @FXML
    private ProgressIndicator loadingSpinner;
    @FXML
    private ComboBox<String> langCombo;

    /**
     * Инициализирует локаль на окне регистрации
     */
    @FXML
    private void initialize() {
        langCombo.getItems().addAll("RU", "EN", "IT", "SL");
        langCombo.setValue(currentLangLabel());
        updateTexts();
        ManagerLanguage.setOnLangChange(this::updateTexts);
    }

    /**
     * Обрабатывает нажатие смены языка в выпадающем списке
     */
    @FXML
    private void onLangChange() {
        String val = langCombo.getValue();
        if (val == null) return;
        switch (val) {
            case "EN" -> ManagerLanguage.set(ManagerLanguage.EN);
            case "IT" -> ManagerLanguage.set(ManagerLanguage.IT);
            case "SL" -> ManagerLanguage.set(ManagerLanguage.SL);
            default -> ManagerLanguage.set(ManagerLanguage.RU);
        }
    }

    /**
     * Обнволяет текст страницы под выбранную локаль
     */
    private void updateTexts() {
        titleLabel.setText(ManagerLanguage.get("register.title"));
        subtitleLabel.setText(ManagerLanguage.get("register.subtitle"));
        registerBtn.setText(ManagerLanguage.get("register.btn"));
        loginLink.setText(ManagerLanguage.get("register.link"));
        regLoginField.setPromptText(ManagerLanguage.get("register.field.login"));
        regPasswordField.setPromptText(ManagerLanguage.get("register.field.password"));
        confirmPasswordField.setPromptText(ManagerLanguage.get("register.field.confirm"));
    }

    /**
     * Возвращает текущую выбранную локаль
     *
     * @return строку локали
     */
    private String currentLangLabel() {
        return switch (ManagerLanguage.getCurrent()) {
            case ManagerLanguage.EN -> "EN";
            case ManagerLanguage.IT -> "IT";
            case ManagerLanguage.SL -> "SL";
            default -> "RU";
        };
    }

    /**
     * Обработчик кнопки зарегистрироваться, если все ок пользователь регистрируется и проходит в приложение
     */
    @FXML
    private void onRegisterClick() {
        String login = regLoginField.getText();
        String password = regPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (login.isBlank() || password.isBlank()) {
            alert(ManagerLanguage.get("register.error.empty"));
            return;
        }
        if (!password.equals(confirmPassword)) {
            alert(ManagerLanguage.get("register.error.passwords"));
            return;
        }

        setLoading(true);

        new Thread(() -> {
            if (!Main.connect()) {
                Platform.runLater(() -> {
                    setLoading(false);
                    alert("Сервер недоступен.");
                });
                return;
            }
            try {
                writeModule.writePacketForServer(server,
                        new CommandPacket("register", null, null, login, password));
                ResponsePacket response = readModule.readResponseForClient(server);

                if (response != null && response.getStatusCode() == Codes.OK) {
                    ManagerAuth.setLogin(login);
                    ManagerAuth.setPassword(password);
                    Main.startThreads();
                    SubscribeController.onSubscribe();
                    Platform.runLater(() -> {
                        setLoading(false);
                        showMainWindow();
                    });
                } else {
                    String msg = response != null ? response.getMessage() : "Нет ответа от сервера";
                    Platform.runLater(() -> {
                        setLoading(false);
                        alert("Ошибка регистрации: " + msg);
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    alert("Сервер недоступен.");
                });
            }
        }).start();
    }

    /**
     * Ссылка на окно авторизации
     */
    @FXML
    private void onLoginLinkClick() {
        showLoginWindow();
    }

    /**
     * Вызывает окно с ошибкой предупреждением
     *
     * @param msg = сообщения с ошибкой
     */
    private void alert(String msg) {
        AlertController.show("Регистрация", msg);
    }

    /**
     * Переключает состояние анимации
     *
     * @param loading - флаг уставноки или неустанвоки анимации
     */
    private void setLoading(boolean loading) {
        loadingSpinner.setVisible(loading);
        registerBtn.setDisable(loading);
    }

    /**
     * Загружает главное окно
     */
    private void showMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/main.fxml"));
            Parent mainRoot = loader.load();
            MainController controller = loader.getController();
            controller.setUserLogin();
            Stage stage = (Stage) registerBox.getScene().getWindow();
            stage.getScene().setRoot(mainRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Загружает окно авторизации
     */
    private void showLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) registerBox.getScene().getWindow();
            stage.getScene().setRoot(loginRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
