package com.fagi.network.handlers;

import com.fagi.network.InputDistributor;
import com.fagi.network.handlers.container.Container;
import com.fagi.network.handlers.container.DefaultContainer;
import com.fagi.threads.ThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Marcus on 08-07-2016.
 */
public class GeneralHandler<T> implements Handler<T> {
    private final Map<Class, Handler<T>> handlers = new ConcurrentHashMap<>();
    private final Container<T> container = new DefaultContainer<>();
    private final InputDistributor<T> inputDistributor;
    private final List<Object> unhandledObjects = new ArrayList<>();
    private final ThreadPool threadPool;
    private DefaultThreadHandler<T> runnable = new DefaultThreadHandler<>(container, this);

    public GeneralHandler(
            InputDistributor<T> inputDistributor,
            ThreadPool threadPool) {
        this.inputDistributor = inputDistributor;
        this.threadPool = threadPool;
        container.setThread(runnable);
    }

    @Override
    public void handle(T object) {
        Handler<T> handler = handlers.get(object.getClass());

        if (handler == null) {
            System.err.println("Missing handler: " + object.getClass());
            unhandledObjects.add(object);
            return;
        }

        threadPool.startThread(() -> handler.handle(object), "GeneralHandler: " + object.getClass());
    }

    public void registerHandler(
            Class clazz,
            Handler<T> handler) {
        handlers.put(clazz, handler);
        inputDistributor.register(clazz, container);
    }

    @Override
    public DefaultThreadHandler<T> getRunnable() {
        return runnable;
    }

    public void stop() {
        for (Class clazz : handlers.keySet()) {
            handlers.remove(clazz);
            inputDistributor.unregister(clazz);
        }
    }
}
