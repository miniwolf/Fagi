/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model.messages.lists;

import java.util.ArrayList;
import java.util.List;

/**
 * @author miniwolf
 */
public class DefaultListAccess implements ListAccess {
    private List<String> data = new ArrayList<>();

    public DefaultListAccess(List<String> data) {
        this.data = data;
    }

    @Override
    public List<String> getData() {
        return data;
    }

    @Override
    public void updateData(List<String> data) {
        this.data = data;
    }
}
