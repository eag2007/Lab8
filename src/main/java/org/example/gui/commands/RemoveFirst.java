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
 * Класс исполнения команды remove_first
 */
public class RemoveFirst implements Command {

    @Override
    public String toString() {
        return ManagerLanguage.get("command.remove_first");
    }

    @Override
    public void executeCommand(String[] args, SocketChannel serverChannel, Object object) {
        ManagerCommands.addToHistory("remove_first");
        Main.send(new CommandPacket("remove_first", args, (RouteClient) object,
                ManagerAuth.getLogin(), ManagerAuth.getPassword()));
    }
}