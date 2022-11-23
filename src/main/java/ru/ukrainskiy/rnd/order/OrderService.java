package ru.ukrainskiy.rnd.order;


import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
public class OrderService {

    /**
     * Сервис обработки order в многопоточной среде
     */
    private final ReentrantReadWriteLock.ReadLock readLock;
    private final ReentrantReadWriteLock.WriteLock writeLock;

    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final Deque<Order> latest = new ConcurrentLinkedDeque<>();

    public OrderService() {
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
    }

    /**
     * Вернет все order
     * @return List<Order>
     */
    public List<Order> findAllOrder() {
        try {
            readLock.lock();
            return new ArrayList<>(orders.values());
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Вернет order по его id
     * @param id
     * @return Optional<Order>
     */
    public Optional<Order> findOrderById(Long id) {
        try {
            readLock.lock();
            return Optional.ofNullable(orders.get(id));
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Добавить новый order
     * @param order
     */
    public void addOrder(Order order) {
        try {
            writeLock.lock();
            orders.put(order.getId(), order);
            if (latest.size() == 100) {
                latest.removeLast();
            }
            latest.addFirst(order);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Удалить order
     * @param order
     */
    public void removeOrder(Order order) {
        try {
            writeLock.lock();
            orders.remove(order.getId());
            latest.remove(order);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Вернуть последние 100 добавленных orders
     * @return
     */
    public List<Order> lastOrder100() {
        try {
            readLock.lock();
            return new ArrayList<>(latest);
        } finally {
            readLock.unlock();
        }
    }
}
