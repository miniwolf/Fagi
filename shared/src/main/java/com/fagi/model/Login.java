package com.fagi.model;
/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Login.java
 *
 * Serializable object to send login requests to server.
 */

import java.io.Serializable;

public record Login(String username, String password) implements Serializable {
}