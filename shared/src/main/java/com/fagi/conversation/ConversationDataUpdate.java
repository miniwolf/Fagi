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
public record ConversationDataUpdate(long id,
                                     List<TextMessage> conversationData,
                                     Timestamp lastMessageDate,
                                     TextMessage lastMessage)
        implements Serializable, InGoingMessages<ConversationDataUpdate>, Access<ConversationDataUpdate> {
    @Override
    public Access<ConversationDataUpdate> access() {
        return this;
    }

    @Override
    public ConversationDataUpdate data() {
        return this;
    }
}
