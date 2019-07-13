package com.fagi.network.handlers;

import com.fagi.model.messages.InGoingMessages;
import com.fagi.network.InputDistributor;
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
public class GeneralHandler<T> implements Handler<T> {
    private final Map<Class, Handler<T>> handlers = new ConcurrentHashMap<>();
    private final Container<T> container = new DefaultContainer<>();
    private final InputDistributor<T> inputDistributor;
    private final List<Object> unhandledObjects = new ArrayList<>();
    private final ThreadPool threadPool;
    private DefaultThreadHandler<T> runnable = new DefaultThreadHandler<T>(container, this);

    public GeneralHandler(InputDistributor<T> inputDistributor, ThreadPool threadPool) {
        this.inputDistributor = inputDistributor;
        this.threadPool = threadPool;
        container.setThread(runnable);
    }

    @Override
    public void handle(InGoingMessages object) {
        Handler handler = GeneralHandler.handlers.get(object.getClass());

        if ( handler == null ) {
            System.err.println("Missing handler: " + object.getClass());
            unhandledObjects.add(object);
            return;
        }

        threadPool.startThread(() -> {
            handler.handle(object);
        }, "GeneralHandler: " + object.getClass());
    }

    public static void registerHandler(Class clazz, Handler handler) {
        handlers.put(clazz, handler);
        inputDistributor.register(clazz, container);
    }

    @Override
    public DefaultThreadHandler getRunnable() {
        return runnable;
    }

    public void stop() {
        for (Class clazz : handlers.keySet()) {
            handlers.remove(clazz);
            inputDistributor.unregister(clazz);
        }
    }
}
