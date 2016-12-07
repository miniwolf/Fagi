package com.fagi.model;

import java.io.Serializable;

/**
 * Created by costa on 07-12-2016.
 */
public class UserNameAvailableRequest implements Serializable {
    private String username;

    public UserNameAvailableRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
