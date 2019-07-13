/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers.container;

import java.util.Queue;

/**
 * @author miniwolf
 */
public interface Container<T> {
    void addObject(T object);

    Queue<T> getQueue();

    void setThread(Runnable runnable);
}
