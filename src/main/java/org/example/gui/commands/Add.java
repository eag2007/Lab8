package org.example.gui.commands;

import org.example.gui.managers.ManagerAuth;
import org.example.packet.CommandPacket;
import org.example.packet.collection.RouteClient;

import java.io.IOException;

import static org.example.gui.Main.server;
import static org.example.gui.Main.writeModule;

public class Add {

    public void executeCommand(RouteClient route) {
        try {
            writeModule.writePacketForServer(server,
                    new CommandPacket("add", null, route, ManagerAuth.getLogin(), ManagerAuth.getPassword()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "add - добавляет новый элемент в коллекцию";
    }
}