package com.fagi.model;
/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * FriendRequest.java
 *
 * Serializable object to send friend requests to server.
 */

import com.fagi.model.messages.message.TextMessage;

import java.io.Serializable;

public class FriendRequest implements Serializable, Comparable<FriendRequest> {
    private final String friendUsername;
    private final TextMessage message;

    public FriendRequest(String friendUsername, TextMessage message) {
        this.friendUsername = friendUsername;
        this.message = message;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public String getSender() {
        return message.getMessageInfo().getSender();
    }

    public TextMessage getMessage() {
        return message;
    }

    @Override
    public int compareTo(FriendRequest o) {
        return friendUsername.compareTo(o.friendUsername);
    }
}