package org.example.gui.commands;

import org.example.gui.Main;
import org.example.gui.interfaces.Command;
import org.example.gui.managers.ManagerCommands;
import org.example.gui.managers.ManagerLanguage;
import org.example.packet.ResponsePacket;
import org.example.packet.enums.Codes;
import org.example.packet.enums.ResponseType;

import java.nio.channels.SocketChannel;

/**
 * Класс исполнения команды help
 */
public class Help implements Command {
    @Override
    public String toString() {
        return ManagerLanguage.get("command.help");
    }

    @Override
    public void executeCommand(String[] args, SocketChannel serverChannel, Object object) {
        ManagerCommands.addToHistory("help");
        String helpText = ManagerCommands.getHelp();
        Main.getGuiPrinter().handleHelp(new ResponsePacket(
                ResponseType.HELP, Codes.OK, helpText, null));
    }
}