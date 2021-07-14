package com.fagi.model;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Marcus on 08-07-2016.
 */
public record SearchUsersResult(List<String> usernames) implements Serializable, InGoingMessages<SearchUsersResult>, Access<SearchUsersResult> {
    @Override
    public SearchUsersResult data() {
        return this;
    }

    @Override
    public Access<SearchUsersResult> access() {
        return this;
    }
}
