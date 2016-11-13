package com.fagi.model.conversation;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.io.Serializable;

/**
 * Created by Marcus on 05-07-2016.
 */
public class UpdateHistoryRequest implements Serializable, InGoingMessages, Access<UpdateHistoryRequest> {

    private String sender;
    private final long conversationID;
    private final int index;

    public UpdateHistoryRequest(String sender, long conversationID, int index) {
        this.sender = sender;
        this.conversationID = conversationID;
        this.index = index;
    }

    public long getId() {
        return conversationID;
    }

    public int getIndex() {
        return index;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public UpdateHistoryRequest getData() {
        return this;
    }

    @Override
    public Access getAccess() {
        return this;
    }
}
