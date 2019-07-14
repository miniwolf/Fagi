package com.fagi.model;
/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Data.java
 */

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.utility.JsonFileOperations;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.PasswordError;
import com.fagi.responses.Response;
import com.fagi.responses.UserExists;
import com.fagi.responses.UserOnline;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;

/**
 * TODO: Add description, maybe think about .fagi file ending
 * Contains and update information on users.
 */
public class Data {
    private final Map<String, OutputAgent> OUTPUT_AGENT_MAP = new ConcurrentHashMap<>();
    private final Map<String, InputAgent> INPUT_AGENT_MAP = new ConcurrentHashMap<>();
    private final Map<String, User> registeredUsers = new ConcurrentHashMap<>();
    private final Map<Long, Conversation> conversations = new ConcurrentHashMap<>();
    private long nextConversationId = 0;

    /*
        TODO : Create new conversation, store and load conversation list
     */

    public synchronized Conversation createConversation(List<String> participants) {
        ConversationType type = participants.size() > 2 ? ConversationType.Multi : ConversationType.Single;

        String name = participants.stream().reduce("", (a, b) -> a + ", " + b);

        Conversation con = new Conversation(nextConversationId, name, type);
        nextConversationId++;

        participants.forEach(con::addUser);

        conversations.put(con.getId(), con);

        return con;
    }

    public void storeConversation(Conversation con) {
        JsonFileOperations.storeConversation(con);
    }

    public void loadConversations() {
        JsonFileOperations.loadAllConversations().forEach(c -> conversations.put(c.getId(), c));

        Set<Long> keys = conversations.keySet();
        if (keys.size() > 0) {
            setNextConversationId(Collections.max(keys) + 1);
        } else {
            setNextConversationId(0);
        }
    }

    public Conversation getConversation(long id) {
        return conversations.get(id);
    }

    public void setNextConversationId(long id) {
        nextConversationId = id;
    }

    public synchronized Response createUser(String userName, String pass) {
        if (registeredUsers.containsKey(userName)) {
            return new UserExists();
        }

        User user = new User(userName, pass);

        registeredUsers.put(userName, user);

        storeUser(user);

        return new AllIsWell();
    }

    public Response storeUser(User user) {
        JsonFileOperations.storeObjectToFile(user, JsonFileOperations.USERS_FOLDER, user.getUserName());
        return new AllIsWell();
    }

    public void loadUsers() {
        List<User> users = JsonFileOperations.loadAllObjectsInFolder(JsonFileOperations.USERS_FOLDER, User.class);

        for (User user : users) {
            //TODO Display exception to user concerning invalid userIndex file.
            registeredUsers.put(user.getUserName(), user);
        }
    }

    public synchronized InviteCodeContainer loadInviteCodes() {
        return JsonFileOperations.loadObjectFromFile(JsonFileOperations.INVITE_CODES_FILE_PATH, InviteCodeContainer.class);
    }

    public synchronized void storeInviteCodes(InviteCodeContainer codes) {
        JsonFileOperations.storeObjectToFile(codes, JsonFileOperations.CONFIG_FOLDER_PATH, JsonFileOperations.INVITE_CODES_FILE);
    }

    public Response userLogin(String userName, String pass, OutputAgent outputAgent,
                                     InputAgent inputAgent) {
        if (isUserOnline(userName)) {
            return new UserOnline();
        }
        User user = getUser(userName);
        if (user == null) {
            return new NoSuchUser();
        }
        if (!user.getPass().equals(pass)) {
            return new PasswordError();
        }
        OUTPUT_AGENT_MAP.put(userName, outputAgent);
        INPUT_AGENT_MAP.put(userName, inputAgent);
        return new AllIsWell();
    }

    public void userLogout(String userName) {
        if (userName == null) {
            return;
        }
        if (OUTPUT_AGENT_MAP.containsKey(userName)) {
            OUTPUT_AGENT_MAP.remove(userName);
            INPUT_AGENT_MAP.remove(userName);
        } else {
            System.out.println("Couldn't log " + userName + " out");
        }
    }

    public boolean isUserOnline(String userName) {
        return OUTPUT_AGENT_MAP.containsKey(userName);
    }

    public void makeFriends(User first, User second) {
        first.addFriend(second);
        second.addFriend(first);

        storeUser(first);
        storeUser(second);
    }

    public User getUser(String name) {
        if (name == null) {
            return null;
        }
        return registeredUsers.get(name);
    }

    public InputAgent getInputAgent(String username) {
        return INPUT_AGENT_MAP.get(username);
    }

    public OutputAgent getOutputAgent(String userName) {
        return OUTPUT_AGENT_MAP.get(userName);
    }

    public List<String> getUserNames() {
        return registeredUsers.values().stream().map(User::getUserName).collect(Collectors.toCollection(ArrayList::new));
    }
}
