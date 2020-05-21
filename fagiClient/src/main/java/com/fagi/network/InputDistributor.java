package com.fagi.network;

import com.fagi.network.handlers.container.Container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Marcus on 09-07-2016.
 */
public class InputDistributor<T> implements Runnable {
    private final Map<Class, Container<T>> containers = new ConcurrentHashMap<>();

    private LinkedBlockingQueue<T> messages = new LinkedBlockingQueue<>();
    private boolean running = true;

    @Override
    public void run() {
        try {
            while (running) {
                T input = messages.take();
                Container<T> container = containers.get(input.getClass());
                if (container == null) {
                    messages.put(input);
                    continue;
                }
                container.addObject(input);
            }
        } catch (InterruptedException e) {
            running = false;
        }
    }

    public void register(
            Class clazz,
            Container<T> handler) {
        containers.put(clazz, handler);
    }

    public void unregister(Class clazz) {
        containers.remove(clazz);
    }

    public void addMessage(T msg) {
        messages.add(msg);
    }

    public void stop() {
        this.running = false;
    }
}
