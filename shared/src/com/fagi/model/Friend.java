package com.fagi.model;

import java.io.Serializable;

/**
 * Created by costa on 11-12-2016.
 */
public class Friend implements Serializable, Comparable<Friend> {
    private final String username;
    private boolean online;

    public Friend(String username, boolean online) {
        this.username = username;
        this.online = online;
    }

    public String getUsername() {
        return username;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friend)) return false;

        Friend friend = (Friend) o;

        return username.equals(friend.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public int compareTo(Friend o) {
        int res = Boolean.compare(o.isOnline(), online);
        if (res == 0) return username.compareTo(o.getUsername());
        return res;
    }
}
