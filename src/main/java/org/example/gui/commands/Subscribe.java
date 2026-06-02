package org.example.gui.commands;

import org.example.gui.interfaces.Command;
import org.example.gui.managers.ManagerAuth;
import org.example.packet.CommandPacket;
import org.example.packet.collection.RouteClient;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import static org.example.gui.Main.server;
import static org.example.gui.Main.writeModule;

public class Subscribe implements Command {

    @Override
    public void executeCommand(String[] args, SocketChannel serverChannel, Object object) {
        try {
            writeModule.writePacketForServer(serverChannel,
                    new CommandPacket("subscribe", args, (RouteClient) object, ManagerAuth.getLogin(), ManagerAuth.getPassword()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() { return "subscribe - устанавливает подписку на уведомления"; }
}
