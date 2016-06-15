/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import com.fagi.model.Conversation;
import com.fagi.model.Message;

import java.util.List;

/**
 * @author miniwolf
 */
public interface Handler<T> extends Runnable {
    void addObject(T t);
}
