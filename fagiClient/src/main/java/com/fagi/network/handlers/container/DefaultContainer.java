/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers.container;

import com.fagi.model.messages.InGoingMessages;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author miniwolf
 */
public class DefaultContainer implements Container {
    private Queue<InGoingMessages> queue = new LinkedBlockingQueue<>();
    private Runnable runnable;

    @Override
    public void addObject(InGoingMessages object) {
        queue.add(object);
        synchronized (runnable) {
            runnable.notify();
        }
    }

    @Override
    public Queue<InGoingMessages> getQueue() {
        return queue;
    }

    @Override
    public void setThread(Runnable runnable) {
        this.runnable = runnable;
    }
}
