package org.example.packet;

import org.example.packet.enums.Codes;
import org.example.packet.enums.ResponseType;

import java.io.Serializable;

/**
 * Класс пакета-ответа с сервера
 */
public class ResponsePacket implements Serializable {
    private final ResponseType type;
    private final Codes statusCode;
    private final String message;
    private final Object data;

    /**
     * Создаёт пакет ответа сервера
     *
     * @param type - тип ответа
     * @param statusCode - код выполнения
     * @param message - сообщение для клиента
     * @param data - данные ответа
     */
    public ResponsePacket(ResponseType type, Codes statusCode, String message, Object data) {
        this.type = type;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    /**
     * Получить код выполнения команды
     *
     * @return код ответа
     */
    public Codes getStatusCode() { return statusCode; }
    /**
     * Получить сообщение ответа
     *
     * @return текст сообщения
     */
    public String getMessage() { return message; }
    /**
     * Получить данные ответа
     *
     * @return объект с результатом команды
     */
    public Object getData() { return data; }
    /**
     * Получить тип ответа
     *
     * @return тип ответа
     */
    public ResponseType getType() { return type; };
}
