package org.example.gui.commands;

import org.example.gui.interfaces.Command;
import org.example.gui.managers.ManagerAuth;
import org.example.packet.CommandPacket;
import org.example.packet.collection.RouteClient;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import static org.example.gui.Main.server;
import static org.example.gui.Main.writeModule;

public class RemoveById implements Command {

    @Override
    public void executeCommand(String[] args, SocketChannel serverChannel, Object object) {
        try {
            writeModule.writePacketForServer(serverChannel,
                    new CommandPacket("remove_by_id", args, (RouteClient) object, ManagerAuth.getLogin(), ManagerAuth.getPassword()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() { return "remove_by_id id - удаляет элемент по id"; }
}
