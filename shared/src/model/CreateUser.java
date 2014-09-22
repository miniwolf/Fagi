package model;/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * CreateUser.java
 *
 * Serializable object to send create user to server.
 */

import java.io.Serializable;

public class CreateUser implements Serializable {
    private final String username;
    private final String password;

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
}