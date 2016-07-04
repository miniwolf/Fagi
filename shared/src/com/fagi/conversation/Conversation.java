package com.fagi.conversation;

import com.fagi.model.TextMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcus on 04-07-2016.
 */
public class Conversation {
    private List<String> participants;
    private List<TextMessage> messages;
    private long id;

    public Conversation(long id) {
        this.id = id;
        participants = new ArrayList<>();
        messages = new ArrayList<>();
    }

    public void addUser(String username) {
        participants.add(username);
    }

    public void removeUser(String username) {
        participants.remove(username);
    }

    public long getId() { return id; }

    public void addMessage(TextMessage message) {
        messages.add(message);
    }

    public List<TextMessage> getMessages() { return messages; }

    public List<String> getParticipants() {
        return participants;
    }
}
