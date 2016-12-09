package com.fagi.model;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.message.TextMessage;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by Marcus on 05-07-2016.
 */
public class HistoryUpdates implements Serializable, InGoingMessages, Access<HistoryUpdates> {

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

    @Override
    public HistoryUpdates getData() {
        return this;
    }

    @Override
    public Access getAccess() {
        return this;
    }
}
