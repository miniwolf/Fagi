/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 * FriendDelete.java
 *
 * Serializable object to send delete request to server.
 */

package com.fagi.model;

import java.io.Serializable;

public class FriendDelete implements Serializable {
    private final String friendUsername;

    public FriendDelete(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFriendUsername() {
        return friendUsername;
    }
}
