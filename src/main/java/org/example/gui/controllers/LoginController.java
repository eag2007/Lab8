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

/**
 * Контроллер обрабатыващий окно авторизации
 */
public class LoginController {

    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private VBox loginBox;
    @FXML
    private Label errorLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private Button loginBtn;
    @FXML
    private Hyperlink registerLink;
    @FXML
    private ProgressIndicator loadingSpinner;
    @FXML
    private ComboBox<String> langCombo;

    /**
     * Инитиализация и локализация на окне login
     */
    @FXML
    private void initialize() {
        langCombo.getItems().addAll("RU", "EN", "IT", "SL");
        langCombo.setValue(currentLangLabel());
        updateTexts();
        ManagerLanguage.setOnLangChange(this::updateTexts);
    }

    /**
     * Обработка кнопки смены языка в окне Login
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
     * Обработка смены текста на другой язык
     */
    private void updateTexts() {
        titleLabel.setText(ManagerLanguage.get("login.title"));
        subtitleLabel.setText(ManagerLanguage.get("login.subtitle"));
        loginBtn.setText(ManagerLanguage.get("login.btn"));
        registerLink.setText(ManagerLanguage.get("login.link"));
        loginField.setPromptText(ManagerLanguage.get("login.field.login"));
        passwordField.setPromptText(ManagerLanguage.get("login.field.password"));
    }


    /**
     * Узнать какой язык текущий
     *
     * @return локаль (Ru, En, It, Sl)
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
     * Обработка кнопки войти
     */
    @FXML
    private void onLoginClick() {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login.isBlank() || password.isBlank()) {
            setError(ManagerLanguage.get("login.error.empty"));
            return;
        }

        setLoading(true);


        /// Подсоединение к серверу
        new Thread(() -> {
            if (!Main.connect()) {
                Platform.runLater(() -> {
                    setLoading(false);
                    setError(ManagerLanguage.get("error.server"));
                });
                return;
            }
            try {
                writeModule.writePacketForServer(server,
                        new CommandPacket("login", null, null, login, password));
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
                    String msg = response != null ? response.getMessage() : ManagerLanguage.get("error.server");
                    Platform.runLater(() -> {
                        setLoading(false);
                        setError("Ошибка: " + msg);
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoading(false);
                    setError(ManagerLanguage.get("error.server"));
                });
            }
        }).start();
    }

    /**
     * Обработчик кнопки (ссылки) на окно регистрации
     */
    @FXML
    private void onRegisterLinkClick() {
        showRegisterWindow();
    }

    /**
     * Вывод ошибки на окно Login
     *
     * @param text текст ошибки
     */
    private void setError(String text) {
        if (errorLabel != null) errorLabel.setText(text);
    }

    /**
     * Загрузка анимации
     *
     * @param loading флаг для понимания начата анимации или нет
     */
    private void setLoading(boolean loading) {
        loadingSpinner.setVisible(loading);
        loginBtn.setDisable(loading);
    }

    /**
     * Загрузка и отображение главного окна
     */
    private void showMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/main.fxml"));
            Parent mainRoot = loader.load();
            MainController controller = loader.getController();
            controller.setUserLogin();
            Stage stage = (Stage) loginBox.getScene().getWindow();
            stage.getScene().setRoot(mainRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Загрузка и отображение окна регистрации
     */
    private void showRegisterWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/register.fxml"));
            Parent registerRoot = loader.load();
            Stage stage = (Stage) loginBox.getScene().getWindow();
            stage.getScene().setRoot(registerRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
