package com.fagi.model.conversation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Marcus on 04-07-2016.
 */
public class CreateConversationRequest implements Serializable {
    private final List<String> participants;

    public CreateConversationRequest(List<String> participants) {
        this.participants = participants;
    }

    public List<String> getParticipants() {
        return participants;
    }
}
