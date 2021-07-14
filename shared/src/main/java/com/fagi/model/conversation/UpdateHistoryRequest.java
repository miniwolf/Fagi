package com.fagi.model.conversation;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Marcus on 05-07-2016.
 */
public record UpdateHistoryRequest(String sender, long conversationID, Date dateLastMessageReceived)
        implements Serializable, InGoingMessages<UpdateHistoryRequest>, Access<UpdateHistoryRequest> {
    @Override
    public UpdateHistoryRequest data() {
        return this;
    }

    @Override
    public Access<UpdateHistoryRequest> access() {
        return this;
    }
}
