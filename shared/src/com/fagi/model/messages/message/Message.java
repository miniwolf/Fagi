/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.message;/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Message.java
 *
 * Serializable object to send messages to server.
 */

import java.io.Serializable;

public interface Message extends Serializable {
    String getSender();

    String getReceiver();
}