/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.lists;

import java.util.List;

/**
 * @author miniwolf
 */
public record DefaultListAccess<T>(List<T> data) implements ListAccess<T> {
    @Override
    public void updateData(List<T> data) {
        this.data.clear();
        this.data.addAll(data);
    }
}
