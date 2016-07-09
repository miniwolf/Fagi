/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import com.fagi.network.handlers.container.Container;

/**
 * @author miniwolf
 */
public class DefaultThreadHandler implements Runnable {
    private Container container;
    private Handler handler;
    private boolean running = true;

    public DefaultThreadHandler(Container container, Handler handler) {
        this.container = container;
        this.handler = handler;
    }

    @Override
    public void run() {
        while ( running ) {
            System.out.println("Handling messages");
            while ( !container.getQueue().isEmpty() ) {
                handler.handle(container.getQueue().remove());
            }
            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
                running = false;
                System.out.println("Stopped the thread handler");
            }
        }
    }
}
