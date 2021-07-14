/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.lists;

import com.fagi.model.FriendRequest;
import com.fagi.model.messages.InGoingMessages;

import java.util.List;

/**
 * FriendRequestList used as response object from server.
 *
 * @author miniwolf
 */
public record FriendRequestList(ListAccess<FriendRequest> access) implements InGoingMessages<List<FriendRequest>> {
}
