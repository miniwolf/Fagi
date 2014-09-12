/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * Login.java
 *
 * Serializable object to send login requests to server.
 */

import java.io.Serializable;

class Login implements Serializable {
    private final String username;
    private final String password;

    public Login(String username, String password) {
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