package org.example.server.managers;

import org.example.packet.collection.Route;
import org.example.server.logger.ServerLogger;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ManagerCollections {
    private PriorityQueue<Route> collectionsRoute;
    private ZonedDateTime timeInit;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Создаёт пустую коллекцию маршрутов
     */
    public ManagerCollections() {
        this.collectionsRoute = new PriorityQueue<>();
        this.timeInit = ZonedDateTime.now();
    }

    /**
     * Добавляет маршрут в коллекцию
     *
     * @param element - маршрут
     */
    public void addCollections(Route element) {
        lock.writeLock().lock();
        try {
            this.collectionsRoute.add(element);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Удаляет из коллекции маршруты пользователя
     *
     * @param login - логин владельца маршрутов
     */
    public void clearCollections(String login) {
        lock.writeLock().lock();
        try {
            this.collectionsRoute.removeIf(route -> route.getAuthor().equals(login));
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Заменяет коллекцию переданными маршрутами
     *
     * @param routes - новая коллекция маршрутов
     */
    public void removeAllByDistanceCollections(PriorityQueue<Route> routes) {
        lock.writeLock().lock();
        try {
            this.collectionsRoute = routes;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Получить маршруты в отсортированном виде
     *
     * @return отсортированный список маршрутов
     */
    public List<Route> getSortedCollections() {
        lock.readLock().lock();
        try {
            List<Route> sorted = new ArrayList<>(collectionsRoute);
            sorted.sort(Comparator.naturalOrder());
            return sorted;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Получить очередь маршрутов
     *
     * @return очередь маршрутов
     */
    public PriorityQueue<Route> getCollectionsRoute() {
        lock.readLock().lock();
        try {
            return this.collectionsRoute;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Получить количество маршрутов
     *
     * @return размер коллекции
     */
    public int getSizeCollections() {
        lock.readLock().lock();
        try {
            return this.collectionsRoute.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Получить время инициализации коллекции
     *
     * @return время создания коллекции
     */
    public ZonedDateTime getTimeInit() {
        lock.readLock().lock();
        try {
            return this.timeInit;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Удаляет маршрут с указанным идентификатором
     *
     * @param id - идентификатор маршрута
     * @return true если маршрут найден и удалён
     */
    public boolean removeRouteById(long id) {
        lock.writeLock().lock();
        try {
            return this.collectionsRoute.removeIf(route -> route.getId() == id);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Заменяет маршрут с таким же идентификатором
     *
     * @param newRoute - обновлённый маршрут
     * @return true если маршрут добавлен в коллекцию
     */
    public boolean updateRoute(Route newRoute) {
        lock.writeLock().lock();
        try {
            this.collectionsRoute.removeIf(route -> route.getId() == newRoute.getId());
            return this.collectionsRoute.add(newRoute);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Загружает маршруты из базы данных в коллекцию
     *
     * @param routes - маршруты из базы данных
     */
    public void loadAllRoutes(PriorityQueue<Route> routes) {
        lock.writeLock().lock();
        try {
            this.collectionsRoute.clear();
            this.collectionsRoute.addAll(routes);
            ServerLogger.info("Загружено {} маршрутов в коллекцию из БД", routes.size());
        } finally {
            lock.writeLock().unlock();
        }
    }
}
