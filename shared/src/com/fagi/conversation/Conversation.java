package com.fagi.conversation;

import com.fagi.model.messages.Access;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.message.TextMessage;

import javax.xml.soap.Text;
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
    private List<TextMessage> messages = new ArrayList<>();
    private long id;
    private Date lastMessageDate = null;
    private ConversationType type;
    private TextMessage lastMessage;
    private String name;

    public Conversation(Conversation con) {
        this.participants = con.participants;
        this.messages = con.messages;
        this.id = con.getId();
        this.lastMessageDate = con.lastMessageDate;
        this.lastMessage = con.getLastMessage();
        this.type = con.getType();
        this.name = con.getName();
    }

    public Conversation(long id, String name, ConversationType type) {
        this.id = id;
        this.type = type;
        this.name = name;
        lastMessageDate = new Date();
    }

    public Conversation() {
        lastMessageDate = new Date();
    }

    public void addUser(String username) {
        participants.add(username);
    }

    public void removeUser(String username) {
        participants.remove(username);
    }

    public long getId() { return id; }

    public synchronized void addMessage(TextMessage message) {
        addMessageNoDate(message);
        lastMessageDate = new Date();
        lastMessage = message;
    }

    public synchronized void addMessageNoDate(TextMessage message) {
        if (this.messages.contains(message)) {
            return;
        }
        messages.add(message);
        Collections.sort(messages);
    }

    public synchronized void addMessagesNoDate(List<TextMessage> messages) {
        for(TextMessage message : messages) {
            if (!this.messages.contains(message)) {
                this.messages.add(message);
            }
        }
        Collections.sort(this.messages);
    }

    public synchronized List<TextMessage> getMessages() { return messages; }

    public List<String> getParticipants() {
        return participants;
    }

    public ConversationType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<TextMessage> getMessagesFromDate(Timestamp time) {
            return messages
                    .stream()
                    .filter(x -> x.getMessageInfo().getTimestamp().compareTo(time) > 0)
                    .sorted(Comparator.comparing(e -> e.getMessageInfo().getTimestamp()))
                    .collect(Collectors.toList());
    }

    public void setType(ConversationType type) {
        this.type = type;
    }

    public Conversation getPlaceholder() {
        Conversation placeholder = new Conversation(id, name, ConversationType.Placeholder);
        participants.forEach(placeholder::addUser);
        placeholder.lastMessageDate = lastMessageDate;
        placeholder.lastMessage = lastMessage;
        return placeholder;
    }

    public TextMessage getLastMessage() {
        return lastMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Conversation that = (Conversation) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
