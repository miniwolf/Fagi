/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import com.fagi.model.Conversation;
import com.fagi.network.ListCellRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author miniwolf
 */
public interface ListMessageHandler {
    List<Object> unread = new ArrayList<>();

    void update(List<Conversation> conversations);

    void setListCellRenderer(List<ListCellRenderer> listCellRenderer);
}
