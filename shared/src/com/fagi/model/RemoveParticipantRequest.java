package com.fagi.model;

import java.io.Serializable;

/**
 * Created by Marcus on 04-07-2016.
 */
public class RemoveParticipantRequest implements Serializable {
    private final String participant;
    private final long id;

    public RemoveParticipantRequest(String username, long id) {
        this.participant = username;
        this.id = id;
    }

    public String getParticipant() {
        return participant;
    }

    public long getId() {
        return id;
    }
}
