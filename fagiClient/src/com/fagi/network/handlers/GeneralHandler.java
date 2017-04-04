package com.fagi.network.handlers;

import com.fagi.model.messages.InGoingMessages;
import com.fagi.network.InputHandler;
import com.fagi.network.handlers.container.Container;
import com.fagi.network.handlers.container.DefaultContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Marcus on 08-07-2016.
 */
public class GeneralHandler implements Handler {
    private static final Map<Class, Handler> handlers = new ConcurrentHashMap<>();
    private static final Container container = new DefaultContainer();

    private Runnable runnable = new DefaultThreadHandler(container, this);
    private final List<Object> unhandledObjects = new ArrayList<>();
    private final List<Thread> threads = new CopyOnWriteArrayList<>();

    public GeneralHandler() {
        GeneralHandler.container.setThread(runnable);
    }

    @Override
    public void handle(InGoingMessages object) {
        Handler handler = GeneralHandler.handlers.get(object.getClass());

        if ( handler == null ) {
            System.err.println("Missing handler: " + object.getClass());
            unhandledObjects.add(object);
            return;
        }

        Thread thread = new Thread(() -> {
            handler.handle(object);
            threads.remove(this);
        });

        thread.start();

        threads.add(thread);
    }

    public void stop() {
        threads.forEach(Thread::interrupt);
        for (Class clazz : handlers.keySet()) {
            handlers.remove(clazz);
            InputHandler.unregister(clazz);
        }

    }

    @Override
    public Runnable getRunnable() {
        return runnable;
    }

    public static void registerHandler(Class clazz, Handler handler) {
        handlers.put(clazz, handler);
        InputHandler.register(clazz, container);
    }
}
