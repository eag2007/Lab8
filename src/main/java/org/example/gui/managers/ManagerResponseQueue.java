package org.example.gui.managers;

import org.example.packet.ResponsePacket;
import org.example.packet.enums.Codes;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class ManagerResponseQueue {

    private static final ManagerResponseQueue INSTANCE = new ManagerResponseQueue();

    private final BlockingQueue<ResponsePacket> queue = new LinkedBlockingQueue<>();

    private final AtomicReference<CompletableFuture<ResponsePacket>> pendingUpdate =
            new AtomicReference<>(null);

    private ManagerResponseQueue() {
    }

    public static ManagerResponseQueue getInstance() {
        return INSTANCE;
    }


    /**
     * Кладет ответ с сервера в потокобезопасную очередь
     * @param packet - ответ с сервера
     * @throws InterruptedException - ошибка потока
     */
    public void put(ResponsePacket packet) throws InterruptedException {
        if (packet.getStatusCode() == Codes.PUSH) {
            queue.put(packet);
            return;
        }

        CompletableFuture<ResponsePacket> future = pendingUpdate.getAndSet(null);
        if (future != null) {
            future.complete(packet);
        } else {
            queue.put(packet);
        }
    }


    /**
     * Возвращает элемент с верхушки стека
     * @return ResponsePacket
     * @throws InterruptedException - ошибка потока
     */
    public ResponsePacket take() throws InterruptedException {
        return queue.take();
    }


    /**
     * Бронирует элемент в очереди
     * @return объект ResponsePacket
     */
    public CompletableFuture<ResponsePacket> expectResponse() {
        CompletableFuture<ResponsePacket> future = new CompletableFuture<>();
        pendingUpdate.set(future);
        return future;
    }


    /**
     * Отменяет бронь
     */
    public void cancelExpected() {
        CompletableFuture<ResponsePacket> future = pendingUpdate.getAndSet(null);
        if (future != null) {
            future.cancel(false);
        }
    }
}