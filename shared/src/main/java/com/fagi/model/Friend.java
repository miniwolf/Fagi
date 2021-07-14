package com.fagi.model;

import java.io.Serializable;

/**
 * Created by costa on 11-12-2016.
 */
public record Friend(String username, boolean online) implements Serializable, Comparable<Friend> {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Friend friend)) {
            return false;
        }

        return username.equals(friend.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public int compareTo(Friend o) {
        int res = Boolean.compare(o.online(), online);
        if (res == 0) {
            return username.compareTo(o.username());
        }
        return res;
    }
}
