package com.fagi.model;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.io.Serializable;

/**
 * Created by costa on 11-12-2016.
 */
public record UserLoggedOut(String username) implements Serializable, InGoingMessages<UserLoggedOut>, Access<UserLoggedOut>, UserStatusUpdate {
    @Override
    public Access<UserLoggedOut> access() {
        return this;
    }

    @Override
    public UserLoggedOut data() {
        return this;
    }
}
