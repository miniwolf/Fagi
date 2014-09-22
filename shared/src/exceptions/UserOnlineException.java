package exceptions;/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * UserOnlineException.java
 *
 * Object for handling user already logged in.
 */

public class UserOnlineException extends Exception {
    public UserOnlineException(String s) {
        super(s);
    }

    public UserOnlineException() {
        super();
    }
}
