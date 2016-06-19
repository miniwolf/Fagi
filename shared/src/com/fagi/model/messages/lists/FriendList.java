/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.lists;

import com.fagi.model.messages.InGoingMessages;

import java.util.List;

/**
 * FriendList used as response from server.
 *
 * @author miniwolf
 */
public class FriendList implements InGoingMessages {
    private ListAccess access;

    public FriendList(ListAccess access) {
        this.access = access;
    }

    public ListAccess getAccess() {
        return access;
    }
}
