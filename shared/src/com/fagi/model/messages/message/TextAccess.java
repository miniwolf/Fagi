/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.message;

import com.fagi.model.messages.Access;

/**
 * @author miniwolf
 */
public interface TextAccess extends Access<String> {
    @Override
    String getData();

    Message getMessage();
}
