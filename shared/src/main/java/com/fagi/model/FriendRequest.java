package com.fagi.model;
/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * FriendRequest.java
 *
 * Serializable object to send friend requests to server.
 */

import com.fagi.model.messages.message.TextMessage;

import java.io.Serializable;
import java.util.Objects;

public class FriendRequest implements Serializable, Comparable<FriendRequest> {
    private final String friendUsername;
    private final TextMessage message;

    public FriendRequest(
            String friendUsername,
            TextMessage message) {
        this.friendUsername = friendUsername;
        this.message = message;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public String getSender() {
        return message
                .getMessageInfo()
                .getSender();
    }

    public TextMessage getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FriendRequest that = (FriendRequest) o;
        return Objects.equals(friendUsername, that.friendUsername) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(friendUsername, message);
    }

    @Override
    public int compareTo(FriendRequest o) {
        return friendUsername.compareTo(o.friendUsername);
    }
}