package com.fagi.exceptions;/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * UserOnlineException.java
 *
 * Object for handling user already logged in.
 */

import java.io.Serializable;

public class UserOnlineException extends Exception implements Serializable {
    public UserOnlineException(String s) {
        super(s);
    }

    public UserOnlineException() {
        super();
    }
}
