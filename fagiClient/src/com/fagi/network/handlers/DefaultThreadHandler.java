/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import com.fagi.model.messages.InGoingMessages;

/**
 * @author miniwolf
 */
public class DefaultThreadHandler implements Runnable {
    private Container container;
    private Handler handler;

    public DefaultThreadHandler(Container container, Handler handler) {
        this.container = container;
        this.handler = handler;
    }

    @Override
    public void run() {
        while ( Thread.interrupted() ) {
            while ( !container.getQueue().isEmpty() ) {
                handler.handle(container.getQueue().remove());
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
