/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages;

import java.io.Serializable;

/**
 * @author miniwolf
 */
public interface InGoingMessages<T> extends Serializable {
    Access<T> access();
}
