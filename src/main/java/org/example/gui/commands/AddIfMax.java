package org.example.gui.commands;

import org.example.gui.managers.ManagerAuth;
import org.example.packet.CommandPacket;
import org.example.packet.collection.RouteClient;

import java.io.IOException;

import static org.example.gui.Main.server;
import static org.example.gui.Main.writeModule;

public class AddIfMax {
    public void executeCommand(RouteClient route) {
        try {
            writeModule.writePacketForServer(server,
                    new CommandPacket("add_if_max", null, route, ManagerAuth.getLogin(), ManagerAuth.getPassword()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "add_if_max - добавляет новый элемент в коллекцию если он больше наибольшего";
    }
}
