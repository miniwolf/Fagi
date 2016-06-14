/*
 * Copyright (c) 2015. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model;

import java.io.Serializable;
import java.util.List;

/**
 * Handle response list from server containing server.
 * We distinguish between object by extending this class.
 * @see FriendList
 * @see FriendRequestList
 *
 * @author miniwolf
 */
public interface ResponseList extends Serializable {
    List<String> getData();
}
