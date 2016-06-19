/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.lists;

import com.fagi.model.messages.Access;

import java.util.List;

/**
 * Handle response list access.
 *
 * @see FriendList
 * @see FriendRequestList
 *
 * @author miniwolf
 */
public interface ListAccess extends Access<List<String>> {
    @Override
    List<String> getData();

    void updateData(List<String> data);
}
