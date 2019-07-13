/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers.container;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author miniwolf
 */
public class DefaultContainer<T> implements Container<T> {
    private Queue<T> queue = new LinkedBlockingQueue<>();
    private Runnable runnable;

    @Override
    public void addObject(T object) {
        queue.add(object);
        synchronized (runnable) {
            runnable.notify();
        }
    }

    @Override
    public Queue<T> getQueue() {
        return queue;
    }

    @Override
    public void setThread(Runnable runnable) {
        this.runnable = runnable;
    }
}
