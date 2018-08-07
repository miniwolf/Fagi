/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Data.java
 */

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.utility.JsonFileOperations;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.PasswordError;
import com.fagi.responses.Response;
import com.fagi.responses.UserExists;
import com.fagi.responses.UserOnline;

/**
 * TODO: Add description, maybe think about .fagi file ending
 * Contains and update information on users.
 */
class Data {
    private static final Map<String, OutputWorker> OUTPUT_WORKER_MAP = new ConcurrentHashMap<>();
    private static final Map<String, InputWorker> INPUT_WORKER_MAP = new ConcurrentHashMap<>();
    private static final Map<String, User> registeredUsers = new ConcurrentHashMap<>();
    private static final Map<Long, Conversation> conversations = new ConcurrentHashMap<>();
    private static long nextConversationId = 0;

    /*
        TODO : Create new conversation, store and load conversation list
     */

    public static synchronized Conversation createConversation(List<String> participants) {
        ConversationType type = participants.size() > 2 ? ConversationType.Multi : ConversationType.Single;

        String name = participants.stream().reduce("", (a, b) -> a + ", " + b);

        Conversation con = new Conversation(nextConversationId, name, type);
        nextConversationId++;

        participants.forEach(con::addUser);

        conversations.put(con.getId(), con);

        return con;
    }

    public static void storeConversation(Conversation con) {
        JsonFileOperations.storeConversation(con);
    }

    public static void loadConversations() {
        JsonFileOperations.loadAllConversations().forEach(c -> conversations.put(c.getId(), c));

        Set<Long> keys = conversations.keySet();
        if ( keys.size() > 0 ) {
            setNextConversationId(Collections.max(keys) + 1);
        } else {
            setNextConversationId(0);
        }
    }

    public static Conversation getConversation(long id) {
        return conversations.get(id);
    }

    public static void setNextConversationId(long id) {
        nextConversationId = id;
    }

    public static synchronized Object createUser(String userName, String pass) {
        if ( registeredUsers.containsKey(userName) ) {
            return new UserExists();
        }

        User user = new User(userName, pass);

        registeredUsers.put(userName, user);

        storeUser(user);

        return new AllIsWell();
    }

    public static Response storeUser(User user) {
        try {
            Gson gson = new Gson();

            String json = gson.toJson(user);

            File file = new File("users/" + user.getUserName());
            File folder = new File("users/");
            if ( !folder.exists() && !folder.mkdir() ) {
                System.err.println("Couldn't create folder");
            }
            if ( !file.exists() && !file.createNewFile() ) {
                System.err.println("Can't create file");
            }

            PrintWriter out = new PrintWriter(new FileWriter(file, false));
            out.println(json);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AllIsWell();
    }

    public static void loadUsers() {
        try {
            File folder = new File("users/");
            if (!folder.exists()) return;

            File[] files = folder.listFiles();
            if (files == null) return;

            for (File file : files) {
                StringBuilder json = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                while (reader.ready()) {
                    json.append(reader.readLine());
                }

                Gson gson = new Gson();

                User user = gson.fromJson(json.toString(), User.class);
                //TODO Display exception to user concerning invalid userIndex file.
                registeredUsers.put(user.getUserName(), user);
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public static synchronized InviteCodeContainer loadInviteCodes() {
        return JsonFileOperations.loadObjectFromFile(JsonFileOperations.INVITE_CODES_FILE_PATH, InviteCodeContainer.class);
    }

    public static synchronized void storeInviteCodes(InviteCodeContainer codes) {
        JsonFileOperations.storeObjectToFile(codes, JsonFileOperations.CONFIG_FOLDER_PATH, JsonFileOperations.INVITE_CODES_FILE);
    }

    public static Response userLogin(String userName, String pass, OutputWorker worker,
                                     InputWorker inputWorker) {
        if ( OUTPUT_WORKER_MAP.containsKey(userName) ) {
            return new UserOnline();
        }
        User user = registeredUsers.get(userName);
        if ( user == null ) {
            return new NoSuchUser();
        }
        if ( !user.getPass().equals(pass) ) {
            return new PasswordError();
        }
        OUTPUT_WORKER_MAP.put(userName, worker);
        INPUT_WORKER_MAP.put(userName, inputWorker);
        return new AllIsWell();
    }

    public static void userLogout(String userName) {
        if ( userName == null ) {
            return;
        }
        if ( OUTPUT_WORKER_MAP.containsKey(userName) ) {
            OUTPUT_WORKER_MAP.remove(userName);
            INPUT_WORKER_MAP.remove(userName);
        } else {
            System.out.println("Couldn't log " + userName + " out");
        }
    }

    public static boolean isUserOnline(String userName) {
        return OUTPUT_WORKER_MAP.containsKey(userName);
    }

    public static void makeFriends(User first, User second) {
        first.addFriend(second);
        second.addFriend(first);

        storeUser(first);
        storeUser(second);
    }

    public static User getUser(String name) {
        if ( name == null ) {
            return null;
        }
        return registeredUsers.get(name);
    }

    public static InputWorker getInputWorker(String username) {
        return INPUT_WORKER_MAP.get(username);
    }

    public static OutputWorker getOutputWorker(String userName) {
        return OUTPUT_WORKER_MAP.get(userName);
    }

    public static List<String> getUserNames() {
        return registeredUsers.values().stream().map(User::getUserName).collect(Collectors.toCollection(ArrayList::new));
    }
}
