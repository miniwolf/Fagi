package com.fagi.model;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.io.Serializable;

/**
 * Created by Marcus on 04-07-2016.
 */
public class RemoveParticipantRequest implements Serializable, InGoingMessages, Access<RemoveParticipantRequest> {
    private String sender;
    private final String participant;
    private final long id;

    public RemoveParticipantRequest(String sender, String username, long id) {
        this.sender = sender;
        this.participant = username;
        this.id = id;
    }

    public String getParticipant() {
        return participant;
    }

    public long getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public RemoveParticipantRequest getData() {
        return this;
    }

    @Override
    public Access getAccess() {
        return this;
    }
}
