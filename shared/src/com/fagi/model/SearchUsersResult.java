package com.fagi.model;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Marcus on 08-07-2016.
 */
public class SearchUsersResult implements Serializable, InGoingMessages, Access<List<String>> {
    private List<String> usernames;

    public SearchUsersResult(List<String> usernames) {
        this.usernames = usernames;
    }

    @Override
    public List<String> getData() {
        return usernames;
    }

    @Override
    public Access getAccess() {
        return this;
    }
}
