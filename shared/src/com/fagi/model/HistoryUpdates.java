package com.fagi.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Marcus on 05-07-2016.
 */
public class HistoryUpdates implements Serializable {

    private final List<TextMessage> updates;
    private final long id;

    public HistoryUpdates(List<TextMessage> updates, long id) {
        this.updates = updates;
        this.id = id;
    }

    public List<TextMessage> getUpdates() {
        return updates;
    }

    public long getId() {
        return id;
    }
}