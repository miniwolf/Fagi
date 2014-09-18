/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * FriendRequest.java
 *
 * Serializable object to send friend requests to server.
 */

import java.io.Serializable;

class FriendRequest implements Serializable {
    private final String friendUsername;

    public FriendRequest(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFriendUsername() {
        return friendUsername;
    }
}