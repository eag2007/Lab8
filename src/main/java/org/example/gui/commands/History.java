package org.example.gui.commands;

import org.example.gui.Main;
import org.example.gui.interfaces.Command;
import org.example.gui.managers.ManagerCommands;
import org.example.packet.ResponsePacket;
import org.example.packet.enums.Codes;
import org.example.packet.enums.ResponseType;

import java.nio.channels.SocketChannel;

public class History implements Command {
    @Override
    public String toString() {
        return "history - последние 14 команд";
    }

    @Override
    public void executeCommand(String[] args, SocketChannel serverChannel, Object object) {
        ManagerCommands.addToHistory("history");
        Main.getGuiPrinter().handleHistory(new ResponsePacket(
                ResponseType.HISTORY, Codes.OK, ManagerCommands.getHistory(), null));
    }
}