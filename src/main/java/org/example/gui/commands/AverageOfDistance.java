package org.example.gui.commands;

import org.example.gui.Main;
import org.example.gui.interfaces.Command;
import org.example.gui.managers.ManagerAuth;
import org.example.gui.managers.ManagerCommands;
import org.example.packet.CommandPacket;
import org.example.packet.collection.RouteClient;

import java.nio.channels.SocketChannel;

public class AverageOfDistance implements Command {

    @Override
    public String toString() {
        return "average_of_distance - возвращает среднее арифметическое значения поля distance";
    }

    @Override
    public void executeCommand(String[] args, SocketChannel serverChannel, Object object) {
        ManagerCommands.addToHistory("average_of_distance");
        Main.send(new CommandPacket("average_of_distance", args, (RouteClient) object,
                ManagerAuth.getLogin(), ManagerAuth.getPassword()));
    }
}