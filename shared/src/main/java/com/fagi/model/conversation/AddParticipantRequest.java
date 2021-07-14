package com.fagi.model.conversation;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.io.Serializable;

/**
 * Created by Marcus on 04-07-2016.
 */
public record AddParticipantRequest(String sender, String participant, long id)
        implements Serializable, InGoingMessages<AddParticipantRequest>, Access<AddParticipantRequest> {
    @Override
    public AddParticipantRequest data() {
        return this;
    }

    @Override
    public Access<AddParticipantRequest> access() {
        return this;
    }
}
