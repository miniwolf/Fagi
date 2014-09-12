/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * FriendRequest.java
 *
 * Serializable object to send friend requests to server.
 */

import java.io.Serializable;

class FriendRequest implements Serializable {
    private final String username;
    private final String friendUsername;

    public FriendRequest(String username, String friendUsername) {
        this.username = username;
        this.friendUsername = friendUsername;
    }

    // TODO: Refactor away
    @Deprecated
    public String getUsername() {
        return username;
    }

    public String getFriendUsername() {
        return friendUsername;
    }
}