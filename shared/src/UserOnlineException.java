/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * UserOnlineException.java
 *
 * Object for handling user already logged in.
 */

class UserOnlineException extends Exception {
    public UserOnlineException(String s) {
        super(s);
    }

    public UserOnlineException() {
        super();
    }
}
