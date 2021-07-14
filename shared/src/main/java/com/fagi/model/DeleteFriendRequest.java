/*
 * Copyright (c) 2015. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 * DeleteFriendRequest.java
 */

package com.fagi.model;

import java.io.Serializable;

/**
 * Serializable object to delete friend request from server request list.
 *
 * @author miniwolf
 */
public record DeleteFriendRequest(String friendUsername) implements Serializable {
}
