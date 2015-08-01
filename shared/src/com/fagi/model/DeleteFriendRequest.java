/*
 * Copyright (c) 2015. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 * DeleteFriendRequest.java
 */

package com.fagi.model;

import java.io.Serializable;

/**
 * Serializable object to delete friend request from server request list.
 *
 * @author miniwolf
 */
public class DeleteFriendRequest implements Serializable {
    private final String friendUsername;

    public DeleteFriendRequest(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFriendUsername() {
        return friendUsername;
    }
}
