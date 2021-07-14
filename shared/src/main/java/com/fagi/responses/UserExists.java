package com.fagi.responses;
/*
 * Copyright (c) 2015. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 * UserExists.java
 */

import java.io.Serial;

/**
 * We send this response object when the user already exists and someone is trying to create a new
 * user with the same username.
 *
 * @author miniwolf
 */
public class UserExists implements Response {
    @Serial
    private static final long serialVersionUID = 7L;
}
