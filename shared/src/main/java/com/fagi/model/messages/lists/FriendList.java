/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.lists;

import com.fagi.model.Friend;
import com.fagi.model.messages.InGoingMessages;

import java.util.List;

/**
 * FriendList used as response from server.
 *
 * @author miniwolf
 */
public record FriendList(ListAccess<Friend> access) implements InGoingMessages<List<Friend>> {
}
