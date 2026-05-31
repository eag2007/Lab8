package org.example.gui.managers;

public class ManagerAuth {
    private static String login = null;
    private static String password = null;

    public static void setLogin(String _login) {
        login = _login;
    }

    public static void setPassword(String _password) {
        password = _password;
    }

    public static String getLogin() {
        return login;
    }

    public static String getPassword() {
        return password;
    }
}