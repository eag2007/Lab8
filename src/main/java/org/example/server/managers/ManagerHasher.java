package org.example.server.managers;

import javax.imageio.IIOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

public class ManagerHasher {
    private static final String PEPER = peper();

    /**
     * Загружает перец из файла настроек
     *
     * @return значение перца или значение по умолчанию
     */
    private static String peper() {
        try {
            InputStream input = new FileInputStream("peper.properties");

            Properties properties = new Properties();

            properties.load(input);

            return properties.getProperty("auth.host", "Здесь_могла_бы_быть_ваша_реклама");
        } catch (Exception e) {
            return "Здесь_могла_бы_быть_ваша_реклама";
        }
    }

    /**
     * Хэширует пароль вместе с солью и перцем
     *
     * @param password - пароль пользователя
     * @param SALT - соль пользователя
     * @return хэш пароля в Base64
     */
    public static String hash(String password, String SALT) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((password + SALT + PEPER).getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка хэширования", e);
        }
    }

    /**
     * Создаёт случайную соль
     *
     * @return соль в Base64
     */
    public static String salt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
