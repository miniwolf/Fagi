/*
 * Copyright (c) 2015. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model;

import java.io.Serializable;
import java.util.List;

/**
 * FriendRequestList used as response object from server.
 * @author miniwolf
 */
public class FriendRequestList implements Serializable, ResponseList {
    private final List<String> friendRequestList;

    public FriendRequestList(List<String> friendRequestList) {
        this.friendRequestList = friendRequestList;
    }

    @Override
    public List<String> getData() {
        return friendRequestList;
    }
}
