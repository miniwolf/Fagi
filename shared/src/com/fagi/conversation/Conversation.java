package com.fagi.conversation;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.message.TextMessage;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * Created by Marcus on 04-07-2016.
 */
public class Conversation implements Serializable, InGoingMessages, Access<Conversation> {
    private List<String> participants = new ArrayList<>();
    private Set<TextMessage> messages = new ConcurrentSkipListSet<>();
    private long id;
    private Date dateLastMessageDate = null;
    private ConversationType type;

    public Conversation(Conversation con) {
        this.participants = con.participants;
        this.messages = con.messages;
        this.id = con.getId();
        this.dateLastMessageDate = con.dateLastMessageDate;
        this.type = con.getType();
    }

    public Conversation(long id, ConversationType type) {
        this.id = id;
        this.type = type;
    }

    public Conversation(long id) {
        this.id = id;
        this.type = ConversationType.Real;
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
        dateLastMessageDate = new Date();
    }

    public void addMessageNoDate(TextMessage message) {
        messages.add(message);
    }

    public Set<TextMessage> getMessages() { return messages; }

    public List<String> getParticipants() {
        return participants;
    }

    public ConversationType getType() {
        return type;
    }

    @Override
    public Conversation getData() {
        return this;
    }

    @Override
    public Access getAccess() {
        return this;
    }

    public Date getDateLastMessageDate() {
        return dateLastMessageDate;
    }

    public Set<TextMessage> getMessagesFromDate(Timestamp time) {
            return messages
                    .stream()
                    .filter(x -> x.getMessageInfo().getTimestamp().compareTo(time) > 0)
                    .sorted(Comparator.comparing(e -> e.getMessageInfo().getTimestamp()))
                    .collect(Collectors.toSet());
    }
}
