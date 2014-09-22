package model;/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel.
 * Logout.java
 *
 * Serializable object to send login requests to server.
 */

import java.io.Serializable;

public class Logout implements Serializable {
    private final String username;

    public Logout(String username) {
        this.username = username;
    }

    public String getUsername() {
            return username;
        }
}
