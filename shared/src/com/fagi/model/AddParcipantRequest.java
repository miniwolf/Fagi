package com.fagi.model;

/**
 * Created by Marcus on 04-07-2016.
 */
public class AddParcipantRequest {
    private final String participant;
    private final long id;

    public  AddParcipantRequest(String username, long id) {
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
