package org.example.gui.commands;

import org.example.gui.managers.ManagerAuth;
import org.example.packet.CommandPacket;

import java.io.IOException;

import static org.example.gui.Main.server;
import static org.example.gui.Main.writeModule;

public class RemoveById {
    public void executeCommand(String[] args) {
        try {
            writeModule.writePacketForServer(server,
                    new CommandPacket("remove_by_id", args, null, ManagerAuth.getLogin(), ManagerAuth.getPassword()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() { return "remove_by_id id - удаляет элемент по id"; }
}
