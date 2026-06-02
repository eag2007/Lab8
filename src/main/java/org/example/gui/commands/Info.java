package org.example.gui.commands;

import org.example.gui.Main;
import org.example.gui.interfaces.Command;
import org.example.gui.managers.ManagerAuth;
import org.example.gui.managers.ManagerCommands;
import org.example.packet.CommandPacket;
import org.example.packet.collection.RouteClient;

import java.nio.channels.SocketChannel;

public class Info implements Command {

    @Override
    public String toString() {
        return "info - выводит информацию о коллекции";
    }

    @Override
    public void executeCommand(String[] args, SocketChannel serverChannel, Object object) {
        ManagerCommands.addToHistory("info");
        Main.send(new CommandPacket("info", args, (RouteClient) object,
                ManagerAuth.getLogin(), ManagerAuth.getPassword()));
    }
}