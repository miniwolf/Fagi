package com.fagi.model.conversation;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;

import java.io.Serializable;

/**
 * Created by Marcus on 04-07-2016.
 */
public record RemoveParticipantRequest(String sender, String participant, long id)
        implements Serializable, InGoingMessages<RemoveParticipantRequest>, Access<RemoveParticipantRequest> {
    @Override
    public RemoveParticipantRequest data() {
        return this;
    }

    @Override
    public Access<RemoveParticipantRequest> access() {
        return this;
    }
}
