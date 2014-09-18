/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * Login.java
 *
 * Serializable object to send login requests to server.
 */

import java.io.Serializable;

class Logout implements Serializable {
    private final String username;

    public Logout(String username) {
        this.username = username;
    }

    public String getUsername() {
            return username;
        }
}
