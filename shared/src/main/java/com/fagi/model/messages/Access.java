/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages;

import java.io.Serializable;

/**
 * Getting access to data is done using extension of this interface.
 *
 * @author miniwolf
 */
public interface Access<T> extends Serializable {
    T data();
}
