/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.lists;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.util.List;

/**
 * FriendRequestList used as response object from server.
 * @author miniwolf
 */
public class FriendRequestList implements InGoingMessages<List<String>> {
    private final ListAccess access;

    public FriendRequestList(ListAccess access) {
        this.access = access;
    }

    @Override
    public Access<List<String>> getAccess() {
        return access;
    }
}