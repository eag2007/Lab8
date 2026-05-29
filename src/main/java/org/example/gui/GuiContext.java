package org.example.gui;

import javafx.application.Platform;
import org.example.client.managers.ManagerResponseQueue;
import org.example.client.modules.ReadModule;
import org.example.client.modules.WriteModule;
import org.example.client.threads.ReaderThread;
import org.example.packet.CommandPacket;
import org.example.packet.ResponsePacket;
import org.example.packet.collection.RouteClient;
import org.example.packet.enums.Codes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public class GuiContext {

    private static GuiContext instance;

    private SocketChannel channel;
    private String login;
    private String passwordHash;

    private final WriteModule writeModule = new WriteModule();
    private final ReadModule  readModule  = new ReadModule();

    private ReaderThread readerThread;
    private Thread       dispatcherThread;

    private Consumer<ResponsePacket> pushHandler;

    private GuiContext() {}

    public static GuiContext get() {
        if (instance == null) instance = new GuiContext();
        return instance;
    }

    public void connect(String host, int port) throws IOException {
        if (channel != null && channel.isOpen()) {
            try { channel.close(); } catch (IOException ignored) {}
        }
        channel = SocketChannel.open();
        channel.configureBlocking(true);
        channel.connect(new InetSocketAddress(host, port));
    }

    public ResponsePacket sendAndWait(String cmd, String[] args, RouteClient values)
            throws IOException, ClassNotFoundException {
        writeModule.writePacketForServer(channel,
                new CommandPacket(cmd, args, values, login, passwordHash));
        return readModule.readResponseForClient(channel);
    }

    public void sendCommand(String cmd, String[] args, RouteClient values) {
        try {
            writeModule.writePacketForServer(channel,
                    new CommandPacket(cmd, args, values, login, passwordHash));
        } catch (IOException e) {
            System.err.println("[GuiContext] ошибка отправки: " + e.getMessage());
        }
    }

    public void startReader() {
        stopThreads();

        readerThread = new ReaderThread(channel, readModule);
        readerThread.setDaemon(true);
        readerThread.start();

        ManagerResponseQueue queue = ManagerResponseQueue.getInstance();
        dispatcherThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ResponsePacket p = queue.take();
                    if (p.getStatusCode() == Codes.PUSH && pushHandler != null) {
                        Platform.runLater(() -> pushHandler.accept(p));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "gui-dispatcher");
        dispatcherThread.setDaemon(true);
        dispatcherThread.start();
    }

    public void stopThreads() {
        if (readerThread != null)     { readerThread.stopReader(); readerThread = null; }
        if (dispatcherThread != null) { dispatcherThread.interrupt(); dispatcherThread = null; }
    }

    public void setPushHandler(Consumer<ResponsePacket> h) { this.pushHandler = h; }

    public String getLogin()                { return login; }
    public void   setLogin(String l)        { this.login = l; }
    public String getPasswordHash()         { return passwordHash; }
    public void   setPasswordHash(String p) { this.passwordHash = p; }
    public SocketChannel getChannel()       { return channel; }
}