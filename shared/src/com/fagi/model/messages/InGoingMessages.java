/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages;

import com.fagi.model.messages.message.Message;

import java.io.Serializable;

/**
 * @author miniwolf
 */
public interface InGoingMessages<T> extends Serializable {
    Access<T> getAccess();
}