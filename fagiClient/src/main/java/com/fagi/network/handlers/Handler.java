/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import com.fagi.model.messages.InGoingMessages;

/**
 * @author miniwolf
 */
public interface Handler<T> {
    void handle(T object);

    Runnable getRunnable();
}
