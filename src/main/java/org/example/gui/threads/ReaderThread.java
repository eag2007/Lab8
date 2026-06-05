package org.example.gui.threads;

import org.example.gui.modules.ReadModule;
import org.example.packet.ResponsePacket;
import org.example.packet.enums.Codes;
import org.example.packet.enums.ResponseType;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import static org.example.gui.Main.managerResponseQueue;


public class ReaderThread extends Thread {

    private final SocketChannel serverChannel;
    private final ReadModule readModule;

    private volatile boolean running = true;


    /**
     * Инициализация reader - потока чтения
     *
     * @param serverChannel - адрес сервера, к которому будем подключаться
     * @param readModule    - модуль для чтения
     */
    public ReaderThread(SocketChannel serverChannel, ReadModule readModule) {
        super("reader-thread");
        this.serverChannel = serverChannel;
        this.readModule = readModule;
        setDaemon(true);
    }


    /**
     * Основное действие постоянного чтения
     */
    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                ResponsePacket packet = readModule.readResponseForClient(serverChannel);

                if (packet == null) {
                    break;
                }

                managerResponseQueue.put(packet);

            } catch (ClosedChannelException e) {
                break;
            } catch (IOException e) {
                if (running) {
                    try {
                        managerResponseQueue.put(new ResponsePacket(
                                ResponseType.ERROR,
                                Codes.ERROR,
                                "Потеряно соединение с сервером: " + e.getMessage(),
                                null
                        ));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
                break;
            } catch (ClassNotFoundException e) {
                try {
                    managerResponseQueue.put(new ResponsePacket(
                            ResponseType.ERROR,
                            Codes.ERROR,
                            "Ошибка десериализации ответа: " + e.getMessage(),
                            null
                    ));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


    /**
     * Остановка reader - потока чтения
     */
    public void stopReader() {
        running = false;
        interrupt();
    }
}