/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.lists;

import com.fagi.model.Friend;
import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.util.List;

/**
 * FriendList used as response from server.
 *
 * @author miniwolf
 */
public class FriendList implements InGoingMessages<List<Friend>> {
    private ListAccess<Friend> access;

    public FriendList(ListAccess<Friend> access) {
        this.access = access;
    }

    public Access<List<Friend>> getAccess() {
        return access;
    }
}
