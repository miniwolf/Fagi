package com.fagi.model;
/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * FriendRequest.java
 *
 * Serializable object to send friend requests to server.
 */

import com.fagi.model.messages.message.TextMessage;

import java.io.Serializable;

public record FriendRequest(String friendUsername, TextMessage message)
        implements Serializable, Comparable<FriendRequest> {

    public String getSender() {
        return message
                .getMessageInfo()
                .getSender();
    }

    @Override
    public int compareTo(FriendRequest o) {
        return friendUsername.compareTo(o.friendUsername);
    }
}