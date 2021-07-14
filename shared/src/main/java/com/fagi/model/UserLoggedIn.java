package com.fagi.model;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.io.Serializable;

/**
 * Created by costa on 11-12-2016.
 */
public record UserLoggedIn(String username) implements Serializable, InGoingMessages<UserLoggedIn>, Access<UserLoggedIn>, UserStatusUpdate {
    @Override
    public Access<UserLoggedIn> access() {
        return this;
    }

    @Override
    public UserLoggedIn data() {
        return this;
    }
}
