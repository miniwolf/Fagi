package com.fagi.conversation;

import com.fagi.model.messages.message.TextMessage;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by Marcus on 13-11-2016.
 */
public class ConversationDataUpdate implements Serializable {
    private final Set<TextMessage> data;
    private final long id;

    public ConversationDataUpdate(long id, Set<TextMessage> data) {
        this.id = id;
        this.data = data;
    }

    public Set<TextMessage> getData() {
        return data;
    }

    public long getId() {
        return id;
    }
}
