package com.fagi.model.conversation;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Marcus on 05-07-2016.
 */
public class UpdateHistoryRequest
        implements Serializable, InGoingMessages<UpdateHistoryRequest>, Access<UpdateHistoryRequest> {

    private final Date dateLastMessageReceived;
    private String sender;
    private final long conversationID;

    public UpdateHistoryRequest(
            String sender,
            long conversationID,
            Date dateLastMessageReceived) {
        this.sender = sender;
        this.conversationID = conversationID;
        this.dateLastMessageReceived = dateLastMessageReceived;
    }

    public long getId() {
        return conversationID;
    }

    public String getSender() {
        return sender;
    }

    public Date getDateLastMessageReceived() {
        return dateLastMessageReceived;
    }

    @Override
    public UpdateHistoryRequest getData() {
        return this;
    }

    @Override
    public Access<UpdateHistoryRequest> getAccess() {
        return this;
    }
}
