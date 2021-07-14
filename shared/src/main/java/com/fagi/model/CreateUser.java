package com.fagi.model;
/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * CreateUser.java
 *
 * Serializable object to send create user to server.
 */

import java.io.Serializable;

public record CreateUser(String username, String password, int inviteCode) implements Serializable {
}