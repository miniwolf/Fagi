package com.fagi.conversation;

import com.fagi.model.messages.message.TextMessage;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Marcus on 13-11-2016.
 */
public class ConversationDataUpdate implements Serializable {
    private final List<TextMessage> data;
    private final long id;

    public ConversationDataUpdate(long id, List<TextMessage> data) {
        this.id = id;
        this.data = data;
    }

    public List<TextMessage> getData() {
        return data;
    }

    public long getId() {
        return id;
    }
}
