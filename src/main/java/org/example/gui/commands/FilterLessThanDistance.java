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
 * Класс исполнения команды filter_less_than_distance
 */
public class FilterLessThanDistance implements Command {

    @Override
    public String toString() {
        return ManagerLanguage.get("command.filter_less_than_distance");
    }

    @Override
    public void executeCommand(String[] args, SocketChannel serverChannel, Object object) {
        ManagerCommands.addToHistory("filter_less_than_distance");
        Main.send(new CommandPacket("filter_less_than_distance", args, (RouteClient) object,
                ManagerAuth.getLogin(), ManagerAuth.getPassword()));
    }
}