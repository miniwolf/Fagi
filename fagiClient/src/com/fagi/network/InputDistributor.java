package com.fagi.network;

import com.fagi.model.messages.InGoingMessages;
import com.fagi.network.handlers.container.Container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Marcus on 09-07-2016.
 */
public class InputDistributor implements Runnable {
    private static final Map<Class, Container> containers = new ConcurrentHashMap<>();

    private LinkedBlockingQueue<InGoingMessages> messages = new LinkedBlockingQueue<>();
    private boolean running = true;

    @Override
    public void run() {
        try {
            while (running) {
                InGoingMessages input = messages.take();
                Container container = containers.get(input.getClass());
                if ( container == null ) {
                    messages.put(input);
                    continue;
                }
                container.addObject(input);
            }
        } catch(InterruptedException e) {
            running = false;
        }
    }

    public static void register(Class clazz, Container handler) {
        containers.put(clazz, handler);
    }

    public static void unregister(Class clazz) {
        containers.remove(clazz);
    }

    public void addMessage(InGoingMessages msg) { messages.add(msg); }

    public void stop() { this.running = false; }
}
