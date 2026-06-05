package org.example.gui.managers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Класс отвечающий за локализацию GUI в приложении
 */
public class ManagerLanguage {

    /// статические переменные со значением языка
    public static final String RU = "ru";
    public static final String EN = "en_US";
    public static final String IT = "it";
    public static final String SL = "sl";

    /// по умолячанию выставляется русский язык
    private static String current = RU;
    private static ResourceBundle bundle = load(RU);
    private static Runnable onLangChange;

    /**
     * Загружает соответствующую локаль из messages_**.properties
     *
     * @param lang - значение из выпадающего списка RU, EN, IT, SL
     * @return возвращает объект ResourceBundle который отвечает за локализацию
     */
    private static ResourceBundle load(String lang) {
        String path = "/org/example/i18n/messages_" + lang + ".properties";
        /// достоем
        try (InputStream is = ManagerLanguage.class.getResourceAsStream(path);
             InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return new PropertyResourceBundle(reader);
        } catch (Exception e) {
            System.err.println("Lang: cannot load " + path);
            return null;
        }
    }

    /**
     * Метод меняет текущую локаль
     *
     * @param lang - значение языка из выпадающего списка
     */
    public static void set(String lang) {
        current = lang;
        bundle = load(lang);
        if (onLangChange != null) {
            onLangChange.run();
        }
    }

    /**
     * Вызывает метод run, который выполняет смену языка в приложении
     *
     * @param r - Runnable задача, при которой меняеся язык
     */
    public static void setOnLangChange(Runnable r) {
        onLangChange = r;
    }


    /**
     * Получить сообщение по ключу на текущем языке
     *
     * @param key -
     * @return возвращает сообщения на соответствующем языке
     */
    public static String get(String key) {
        if (bundle == null) {
            return key;
        }
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    /**
     * Получить текущий класс локализации
     *
     * @return объект ResourceBundle по ссылке, то что сейчас отвечает за локализаци.
     */
    public static ResourceBundle getBundle() {
        return bundle;
    }

    /**
     * Получить текущую строчку текущего языка
     *
     * @return текущий язык
     */
    public static String getCurrent() {
        return current;
    }
}
