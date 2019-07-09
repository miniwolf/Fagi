/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.lists;

import java.util.List;

/**
 * @author miniwolf
 */
public class DefaultListAccess<T> implements ListAccess<T> {
    private List<T> data;

    public DefaultListAccess(List<T> data) {
        this.data = data;
    }

    @Override
    public List<T> getData() {
        return data;
    }

    @Override
    public void updateData(List<T> data) {
        this.data.clear();
        this.data.addAll(data);
    }
}
