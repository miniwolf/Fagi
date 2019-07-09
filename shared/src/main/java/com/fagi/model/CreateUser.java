package com.fagi.model;
        /*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * CreateUser.java
 *
 * Serializable object to send create user to server.
 */

import java.io.Serializable;

public class CreateUser implements Serializable {
    private final String username;
    private final String password;
    private InviteCode inviteCode;

    public CreateUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public InviteCode getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(InviteCode inviteCode) {
        this.inviteCode = inviteCode;
    }
}