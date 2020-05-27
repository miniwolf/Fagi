package com.fagi.model;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.io.Serializable;

/**
 * Created by costa on 11-12-2016.
 */
public class UserLoggedIn
        implements Serializable, InGoingMessages<UserLoggedIn>, Access<UserLoggedIn>, UserStatusUpdate {
    private final String username;

    public UserLoggedIn(String username) {
        this.username = username;
    }

    @Override
    public Access<UserLoggedIn> getAccess() {
        return this;
    }

    @Override
    public UserLoggedIn getData() {
        return this;
    }

    public String getUsername() {
        return username;
    }
}
