package com.fagi.model;

/**
 * Created by costa on 11-12-2016.
 */
public class Friend implements Comparable<Friend> {
    private final String username;
    private final boolean online;

    public Friend(String username, boolean online) {
        this.username = username;
        this.online = online;
    }

    public String getUsername() {
        return username;
    }

    public boolean isOnline() {
        return online;
    }

    @Override
    public int compareTo(Friend o) {
        int res = Boolean.compare(online, o.isOnline());
        if (res == 0) return username.compareTo(o.getUsername());
        return res;
    }
}
