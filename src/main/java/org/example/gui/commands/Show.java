package org.example.gui.commands;

import org.example.gui.Main;
import org.example.gui.interfaces.Command;
import org.example.gui.managers.ManagerAuth;
import org.example.gui.managers.ManagerCommands;
import org.example.packet.CommandPacket;
import org.example.packet.collection.RouteClient;

import java.nio.channels.SocketChannel;

public class Show implements Command {

    @Override
    public String toString() {
        return "show - выводит все элементы коллекции";
    }

    @Override
    public void executeCommand(String[] args, SocketChannel serverChannel, Object object) {
        ManagerCommands.addToHistory("show");
        Main.send(new CommandPacket("show", args, (RouteClient) object,
                ManagerAuth.getLogin(), ManagerAuth.getPassword()));
    }
}