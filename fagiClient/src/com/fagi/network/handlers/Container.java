/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import com.fagi.model.messages.InGoingMessages;

import java.util.Queue;

/**
 * @author miniwolf
 */
public interface Container {
    void addObject(InGoingMessages object);

    Queue<InGoingMessages> getQueue();
}
