package org.example.gui.commands;

import org.example.gui.Main;
import org.example.gui.interfaces.Command;
import org.example.gui.managers.ManagerAuth;
import org.example.gui.managers.ManagerCommands;
import org.example.packet.CommandPacket;
import org.example.packet.collection.RouteClient;

import java.nio.channels.SocketChannel;

public class Add implements Command {

    @Override
    public String toString() {
        return "add - добавляет новый элемент в коллекцию";
    }

    @Override
    public void executeCommand(String[] args, SocketChannel serverChannel, Object object) {
        ManagerCommands.addToHistory("add");
        Main.send(new CommandPacket("add", args, (RouteClient) object,
                ManagerAuth.getLogin(), ManagerAuth.getPassword()));
    }
}