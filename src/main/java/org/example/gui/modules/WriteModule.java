package org.example.gui.modules;

import org.example.client.managers.ManagerSerialize;
import org.example.packet.CommandPacket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class WriteModule {
    /**
     * Отправка данных на сервер, запись в канал
     * @param server - адрес сервера SocketChannel
     * @param commandPacket - пакет, который отправляем CommandPacket
     * @throws IOException - ошибка, которая может возникнуть при отправке
     */
    public void writePacketForServer(SocketChannel server, CommandPacket commandPacket) throws IOException {
        byte[] data = ManagerSerialize.serialize(commandPacket);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        server.write(buffer);
    }
}