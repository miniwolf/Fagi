package com.fagi.conversation;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.message.TextMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Marcus on 04-07-2016.
 */
public class Conversation implements Serializable, InGoingMessages, Access<Conversation> {
    private List<String> participants = new ArrayList<>();
    private List<TextMessage> messages = new ArrayList<>();
    private long id;
    private Date lastMessageDate = null;

    public Conversation(long id) {
        this.id = id;
    }

    public Conversation() {
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
        lastMessageDate = new Date();
    }

    public List<TextMessage> getMessages() { return messages; }

    public List<String> getParticipants() {
        return participants;
    }

    @Override
    public Conversation getData() {
        return this;
    }

    @Override
    public Access getAccess() {
        return this;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }
}
