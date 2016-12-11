/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.lists;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.util.List;

/**
 * FriendList used as response from server.
 *
 * @author miniwolf
 */
public class FriendList implements InGoingMessages<List<String>> {
    private ListAccess<String> access;

    public FriendList(ListAccess<String> access) {
        this.access = access;
    }

    public Access<List<String>> getAccess() {
        return access;
    }
}
