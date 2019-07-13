package com.fagi.conversation;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.message.TextMessage;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Marcus on 13-11-2016.
 */
public class ConversationDataUpdate implements Serializable, InGoingMessages<ConversationDataUpdate>, Access<ConversationDataUpdate> {
    private final List<TextMessage> data;
    private Timestamp lastMessageDate;
    private final TextMessage lastMessage;
    private final long id;

    public ConversationDataUpdate(long id, List<TextMessage> data, Timestamp lastMessageDate, TextMessage lastMessage) {
        this.id = id;
        this.data = data;
        this.lastMessageDate = lastMessageDate;
        this.lastMessage = lastMessage;
    }

    public List<TextMessage> getConversationData() {
        return data;
    }

    public long getId() {
        return id;
    }

    public Timestamp getLastMessageDate() {
        return lastMessageDate;
    }

    public TextMessage getLastMessage() {
        return lastMessage;
    }

    @Override
    public Access<ConversationDataUpdate> getAccess() {
        return this;
    }

    @Override
    public ConversationDataUpdate getData() {
        return this;
    }
}
