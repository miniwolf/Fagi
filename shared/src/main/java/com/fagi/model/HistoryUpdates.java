package com.fagi.model;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.message.TextMessage;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Marcus on 05-07-2016.
 */
public record HistoryUpdates(List<TextMessage> updates, long id)
        implements Serializable, InGoingMessages<HistoryUpdates>, Access<HistoryUpdates> {
    @Override
    public HistoryUpdates data() {
        return this;
    }

    @Override
    public Access<HistoryUpdates> access() {
        return this;
    }
}
