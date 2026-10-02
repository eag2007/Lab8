package org.example.server.managers;

import org.example.packet.CommandPacket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ManagerDeserialize {
    /**
     * Десериализует пакет команды из массива байтов
     *
     * @param data - данные пакета
     * @return пакет команды
     * @throws IOException ошибка чтения данных
     * @throws ClassNotFoundException класс пакета не найден
     */
    public static CommandPacket deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (CommandPacket) ois.readObject();
    }
}
