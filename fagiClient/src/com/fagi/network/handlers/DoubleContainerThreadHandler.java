/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import com.fagi.network.handlers.Handler;
import com.fagi.network.handlers.container.Container;

/**
 * A handler containing two containers will use the same handling function for both containers.
 *
 * @author miniwolf
 */
public class DoubleContainerThreadHandler implements Runnable {
    private Container con;
    private Container con2;
    private Handler handler;

    public DoubleContainerThreadHandler(Container con, Container con2, Handler handler) {
        this.con = con;
        this.con2 = con2;
        this.handler = handler;
    }

    @Override
    public void run() {
        while ( !Thread.interrupted() ) {
            while ( !con.getQueue().isEmpty() ) {
                handler.handle(con.getQueue().remove());
            }
            while ( !con2.getQueue().isEmpty() ) {
                handler.handle(con2.getQueue().remove());
            }
            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
