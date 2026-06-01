package org.example.gui.commands;

import org.example.gui.managers.ManagerAuth;
import org.example.packet.CommandPacket;

import java.io.IOException;

import static org.example.gui.Main.server;
import static org.example.gui.Main.writeModule;

public class Info {

    public void executeCommand() {
        try {
            writeModule.writePacketForServer(server,
                    new CommandPacket("info", new String[]{}, null, ManagerAuth.getLogin(), ManagerAuth.getPassword()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() { return "info - выводит информацию о коллекции"; }
}