package org.example.client.managers;

import org.example.packet.ResponsePacket;
import org.example.packet.enums.Codes;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Хранит и передаёт ответы сервера ожидающим обработчикам.
 */
public class ManagerResponseQueue {

    private static final ManagerResponseQueue INSTANCE = new ManagerResponseQueue();

    private final BlockingQueue<ResponsePacket> queue = new LinkedBlockingQueue<>();
    private final AtomicReference<CompletableFuture<ResponsePacket>> pendingUpdate =
            new AtomicReference<>(null);

    private ManagerResponseQueue() {
    }

    /**
     * Получить общий экземпляр очереди ответов
     *
     * @return менеджер очереди ответов
     */
    public static ManagerResponseQueue getInstance() {
        return INSTANCE;
    }

    /**
     * Сохраняет ответ или передаёт его ожидающей команде update
     *
     * @param packet - пакет ответа сервера
     * @throws InterruptedException поток прерван во время добавления в очередь
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
     * Получить следующий ответ из очереди
     *
     * @return пакет ответа
     * @throws InterruptedException поток прерван во время ожидания ответа
     */
    public ResponsePacket take() throws InterruptedException {
        return queue.take();
    }

    /**
     * Зарегистрировать ожидание ответа для команды update
     *
     * @return будущее с ответом сервера
     */
    public CompletableFuture<ResponsePacket> expectResponse() {
        CompletableFuture<ResponsePacket> future = new CompletableFuture<>();
        pendingUpdate.set(future);
        return future;
    }

    /**
     * Отменить зарегистрированное ожидание ответа
     */
    public void cancelExpected() {
        CompletableFuture<ResponsePacket> future = pendingUpdate.getAndSet(null);
        if (future != null) {
            future.cancel(false);
        }
    }
}
