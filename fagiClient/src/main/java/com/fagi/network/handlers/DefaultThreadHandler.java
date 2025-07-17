/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import com.fagi.network.handlers.container.Container;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * @author miniwolf
 */
public class DefaultThreadHandler<T> implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(DefaultThreadHandler.class.getName());
    private Container<T> container;
    private Handler<T> handler;
    private AtomicBoolean running = new AtomicBoolean(true);

    public DefaultThreadHandler(
            Container<T> container,
            Handler<T> handler) {
        this.container = container;
        this.handler = handler;
    }

    @Override
    public void run() {
        while (running.get()) {
            while (!container
                    .getQueue()
                    .isEmpty()) {
                handler.handle(container
                                       .getQueue()
                                       .remove());
            }
            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
                running.set(false);
                LOGGER.info(() -> "Stopped the thread handler");
            }
        }
    }

    public void stop() {
        running.set(false);
        synchronized (this) {
            notify();
        }
    }
}
