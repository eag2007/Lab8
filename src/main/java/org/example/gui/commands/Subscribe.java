package org.example.gui.commands;

import org.example.gui.Main;
import org.example.gui.interfaces.Command;
import org.example.gui.managers.ManagerAuth;
import org.example.gui.managers.ManagerCommands;
import org.example.gui.managers.ManagerLanguage;
import org.example.packet.CommandPacket;
import org.example.packet.collection.RouteClient;

import java.nio.channels.SocketChannel;

/**
 * Класс исполнения команды subscribe
 */
public class Subscribe implements Command {

    @Override
    public String toString() {
        return ManagerLanguage.get("command.subscribe");
    }

    @Override
    public void executeCommand(String[] args, SocketChannel serverChannel, Object object) {
        Main.sendQuiet(new CommandPacket("subscribe", new String[]{"true"}, (RouteClient) object,
                ManagerAuth.getLogin(), ManagerAuth.getPassword()));
    }
}