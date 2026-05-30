package org.example.packet;

import org.example.packet.enums.Codes;
import org.example.packet.enums.ResponseType;

import java.io.Serializable;

public class ResponsePacket implements Serializable {
    private final ResponseType type;
    private final Codes statusCode;
    private final String message;
    private final Object data;

    public ResponsePacket(ResponseType type, Codes statusCode, String message, Object data) {
        this.type = type;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public Codes getStatusCode() { return statusCode; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
    public ResponseType getType() { return type; };
}