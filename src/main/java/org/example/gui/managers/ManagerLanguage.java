package org.example.gui.managers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class ManagerLanguage {

    public static final String RU = "ru";
    public static final String EN = "en_US";
    public static final String IT = "it";
    public static final String SL = "sl";

    private static String current = RU;
    private static ResourceBundle bundle = load(RU);
    private static Runnable onLangChange;

    private static ResourceBundle load(String lang) {
        String path = "/org/example/i18n/messages_" + lang + ".properties";
        try (InputStream is = ManagerLanguage.class.getResourceAsStream(path);
             InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return new PropertyResourceBundle(reader);
        } catch (Exception e) {
            System.err.println("Lang: cannot load " + path);
            return null;
        }
    }

    public static void set(String lang) {
        current = lang;
        bundle = load(lang);
        if (onLangChange != null) onLangChange.run();
    }

    public static void setOnLangChange(Runnable r) {
        onLangChange = r;
    }

    public static String get(String key) {
        if (bundle == null) return key;
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static String getCurrent() {
        return current;
    }
}
