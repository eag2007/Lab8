package org.example.packet;

import org.example.packet.collection.RouteClient;

import java.io.Serializable;

/**
 * Класс пакета-команды для отправки на сервер
 */
public class CommandPacket implements Serializable {
    private final String type;
    private final String[] args;
    private final RouteClient values;
    private final String login;
    private final String password;

    /**
     * Создаёт пакет команды для отправки на сервер
     *
     * @param type - название команды
     * @param args - аргументы команды
     * @param values - данные маршрута
     * @param login - логин пользователя
     * @param password - хэш пароля пользователя
     */
    public CommandPacket(String type, String[] args, RouteClient values, String login, String password) {
        this.type = type;
        this.args = args;
        this.values = values;
        this.login = login;
        this.password = password;
    }

    /**
     * Получить название команды
     *
     * @return название команды
     */
    public String getType() { return this.type; }
    /**
     * Получить аргументы команды
     *
     * @return массив аргументов
     */
    public String[] getArgs() { return this.args; }
    /**
     * Получить данные маршрута
     *
     * @return маршрут без серверных полей
     */
    public RouteClient getValues() { return this.values; }
    /**
     * Получить логин пользователя
     *
     * @return логин
     */
    public String getLogin() { return this.login; }
    /**
     * Получить хэш пароля пользователя
     *
     * @return хэш пароля
     */
    public String getPassword() { return this.password; }
}
