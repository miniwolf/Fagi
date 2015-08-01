package com.fagi.model;/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * FriendRequest.java
 *
 * Serializable object to send friend requests to server.
 */

import java.io.Serializable;

public class FriendRequest implements Serializable {
    private final String friendUsername;

    public FriendRequest(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFriendUsername() {
        return friendUsername;
    }
}