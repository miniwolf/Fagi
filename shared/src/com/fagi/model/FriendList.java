/*
 * Copyright (c) 2015. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model;

import java.io.Serializable;
import java.util.List;

/**
 * FriendList used as response from server.
 *
 * @author miniwolf
 */
public class FriendList implements Serializable, ResponseList {
    private List<String> friendList;

    public FriendList(List<String> friendList) {
        this.friendList = friendList;
    }

    @Override
    public List<String> getData() {
        return friendList;
    }
}
