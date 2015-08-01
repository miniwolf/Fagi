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
public class FriendRequestList extends ResponseList implements Serializable {
    public FriendRequestList(List<String> friendRequestList) {
        super(friendRequestList);
    }
}
