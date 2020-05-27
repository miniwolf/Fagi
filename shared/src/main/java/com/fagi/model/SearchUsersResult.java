package com.fagi.model;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Marcus on 08-07-2016.
 */
public class SearchUsersResult implements Serializable, InGoingMessages<SearchUsersResult>, Access<SearchUsersResult> {
    private List<String> usernames;
    private List<String> friends;

    public SearchUsersResult(
            List<String> usernames,
            List<String> friends) {
        this.usernames = usernames;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public List<String> getFriends() {
        return friends;
    }

    @Override
    public SearchUsersResult getData() {
        return this;
    }

    @Override
    public Access<SearchUsersResult> getAccess() {
        return this;
    }
}
