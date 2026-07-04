package org.example.gui.managers;

public class ManagerAuth {
    private static String login = null;
    private static String password = null;

    /**
     * Установить логин
     *
     * @param _login - логин
     */
    public static void setLogin(String _login) {
        login = _login;
    }

    /**
     * Установить пароль
     *
     * @param _password - пароль
     */
    public static void setPassword(String _password) {
        password = _password;
    }

    /**
     * Получить логин
     *
     * @return логин
     */
    public static String getLogin() {
        return login;
    }

    /**
     * Получить пароль
     *
     * @return пароль
     */
    public static String getPassword() {
        return password;
    }
}