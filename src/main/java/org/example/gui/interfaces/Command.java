package org.example.gui.interfaces;

import java.nio.channels.SocketChannel;

/**
 * Единыый интерфейс всех команд
 */
public interface Command {
    String toString();

    void executeCommand(String[] args, SocketChannel serverChannel, Object object);
}