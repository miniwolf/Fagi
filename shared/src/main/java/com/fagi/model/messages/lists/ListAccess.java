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
public interface ListAccess<T> extends Access<List<T>> {
    @Override
    List<T> getData();

    void updateData(List<T> data);
}