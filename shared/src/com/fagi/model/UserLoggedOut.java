package com.fagi.model;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.io.Serializable;

/**
 * Created by costa on 11-12-2016.
 */
public class UserLoggedOut implements Serializable, InGoingMessages, Access<UserLoggedOut> {
    private final String username;

    public UserLoggedOut(String username) {
        this.username = username;
    }

    @Override
    public Access getAccess() {
        return this;
    }

    @Override
    public UserLoggedOut getData() {
        return this;
    }

    public String getUsername() {
        return username;
    }
}
