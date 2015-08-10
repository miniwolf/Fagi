/*
 * Copyright (c) 2015. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model;

import java.io.Serializable;

/**
 * Serializable object to delete friend from server user friend list.
 *
 * @author miniwolf
 */
public class DeleteFriend implements Serializable {
    private final String friendUsername;

    public DeleteFriend(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFriendUsername() {
        return friendUsername;
    }
}
