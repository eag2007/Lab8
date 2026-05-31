package org.example.gui.commands;

import org.example.gui.managers.ManagerAuth;
import org.example.packet.CommandPacket;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import static org.example.gui.Main.server;
import static org.example.gui.Main.writeModule;

public class Info {

    public void execute() {
        try {
            writeModule.writePacketForServer(server,
                    new CommandPacket("info", new String[]{}, null, ManagerAuth.getLogin(), ManagerAuth.getPassword()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}