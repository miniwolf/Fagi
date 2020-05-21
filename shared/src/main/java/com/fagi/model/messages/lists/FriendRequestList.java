/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.lists;

import com.fagi.model.FriendRequest;
import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.util.List;

/**
 * FriendRequestList used as response object from server.
 *
 * @author miniwolf
 */
public class FriendRequestList implements InGoingMessages<List<FriendRequest>> {
    private final ListAccess<FriendRequest> access;

    public FriendRequestList(ListAccess<FriendRequest> access) {
        this.access = access;
    }

    @Override
    public Access<List<FriendRequest>> getAccess() {
        return access;
    }
}
