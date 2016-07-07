/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Data.java
 */

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.fagi.conversation.Conversation;
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
    private static final Map<String, OutputWorker> onlineUsers = new ConcurrentHashMap<>();
    private static final Map<String, User> registeredUsers = new ConcurrentHashMap<>();
    private static final Map<Long, Conversation> conversations = new ConcurrentHashMap<>();
    private static long nextConversationId = 0;
    private static final String separator = "\",\"";
    private static final String indexFilePath = "users/userIndex.fagi";
    private static final String conversationsFolderPath = "conversations/";

    /*
        TODO : Create new conversation, store and load conversation list
     */

    public static synchronized Conversation createConversation(List<String> participants) {
        Conversation con = new Conversation(nextConversationId);
        nextConversationId++;

        participants.forEach(con::addUser);

        conversations.put(con.getId(), con);

        return con;
    }

    public static void storeConversation(Conversation con) throws IOException {
        File folder = new File(conversationsFolderPath);
        File file = new File(conversationsFolderPath + con.getId() + ".fagi");
        if ( !folder.exists() ) {
            folder.mkdir();
        }
        if ( !file.exists() ) {
            file.createNewFile();
        }

        Gson gson = new Gson();

        PrintWriter out = new PrintWriter(new FileWriter(conversationsFolderPath + con.getId() + ".fagi", false));
        out.println(gson.toJson(con));
        out.flush();
        out.close();
    }

    public static void loadConversations() throws IOException {
        File folder = new File(conversationsFolderPath);
        if ( !folder.exists() ) {
            return;
        }

        File[] files = folder.listFiles();
        if ( files == null ) {
            return;
        }

        for ( File file : files ) {
            String json = "";
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ( reader.ready() ) {
                json += reader.readLine();
            }

            Gson gson = new Gson();

            Conversation con = gson.fromJson(json, Conversation.class);
            conversations.put(con.getId(), con);
        }

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

    public static Object createUser(String userName, String pass) throws IOException {
        if ( registeredUsers.containsKey(userName) ) {
            return new UserExists();
        }

        registeredUsers.put(userName, new User(userName, pass));

        PrintWriter out = new PrintWriter(new FileWriter(indexFilePath, true));
        out.println("\"" + userName + separator + pass + "\"");
        out.flush();
        out.close();

        File file = new File("users/" + userName);

        if ( !file.createNewFile() ) {
            System.out.println(userName + ". File already exists, supposed bug. Report please!");
        }
        return new AllIsWell();
    }

    public static Response userLogin(String userName, String pass, OutputWorker worker) {
        if ( onlineUsers.containsKey(userName) ) {
            return new UserOnline();
        }
        User user = registeredUsers.get(userName);
        if ( user == null ) {
            return new NoSuchUser();
        }
        if ( !user.getPass().equals(pass) ) {
            return new PasswordError();
        }
        onlineUsers.put(userName, worker);
        return new AllIsWell();
    }

    public static void userLogout(String userName) {
        if ( userName == null ) {
            return;
        }
        if ( onlineUsers.containsKey(userName) ) {
            onlineUsers.remove(userName);
        } else {
            System.out.println("Couldn't log " + userName + " out");
        }
    }

    public static boolean isUserOnline(String userName) {
        return onlineUsers.containsKey(userName);
    }

    public static void makeFriends(User first, User second) {
        first.addFriend(second);
        second.addFriend(first);

        appendFriendToUserFile(first.getUserName(), second.getUserName());
        appendFriendToUserFile(second.getUserName(), first.getUserName());
    }

    public static User getUser(String name) {
        if ( name == null ) {
            return null;
        }
        return registeredUsers.get(name);
    }

    public static OutputWorker getWorker(String userName) {
        return onlineUsers.get(userName);
    }

    public static void readInData() {
        try {
            File file = new File("users/");
            File indexFile = new File(indexFilePath);
            if ( !file.exists() || !indexFile.exists() ) {
                if ( !file.mkdir() ) {
                    System.err.println("Couldn't create folder");
                }
                if ( !indexFile.createNewFile() ) {
                    System.err.println("Can't create file");
                }
            } else {
                List<String> tmpUsernameList = readIndexFile(indexFile);
                readUsers(tmpUsernameList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> readIndexFile(File indexFile) throws Exception {
        BufferedReader indexReader = new BufferedReader(new FileReader(indexFile));
        List<String> tmpUsernameList = new ArrayList<>();
        while ( indexReader.ready() ) {
            String line = indexReader.readLine();
            int sep = line.indexOf(separator);
            String username = line.substring(1, sep);
            String pass = line.substring(sep + separator.length(), line.length() - 1);
            System.out.println(username + " " + pass);
            User tmpUser = new User(username, pass);
            registeredUsers.put(username, tmpUser);
            tmpUsernameList.add(username);
        }
        return tmpUsernameList;
    }

    private static void readUsers(List<String> tmpUsernameList) {
        for ( String s : tmpUsernameList ) {
            User user = registeredUsers.get(s);
            System.out.println("Now parsing: " + s);
            List<List<String>> tmp = (List<List<String>>) parseUserFile(s);
            user.addFriends(tmp.get(0));
            user.addFriendReqs(tmp.get(1));
        }
    }

    private static Response appendFriendToUserFile(String username, String friendName) {
        Response object = appendToUserFile(username, friendName, 0);
        if ( object != null ) {
            return object;
        }
        return removeFromUserFile(username, friendName, 1);
    }

    public static Response appendFriendReqToUserFile(String username, String friendName) {
        return appendToUserFile(username, friendName, 1);
    }

    public static Response removeFriendFromUserFile(String username, String friendName) {
        Response object = removeFromUserFile(username, friendName, 0);
        if ( object != null ) {
            return object;
        }
        return removeFromUserFile(username, friendName, 1);
    }

    public static Response removeFriendRequestFromUserFile(String username, String friendName) {
        Response object = removeFromUserFile(username, friendName, 1);
        if ( object != null ) {
            return object;
        }
        return new AllIsWell();
    }

    private static Response appendToUserFile(String username, String friendName, int index) {
        Object object = parseUserFile(username);
        if ( object instanceof NoSuchUser ) {
            return (NoSuchUser) object;
        }
        List<List<String>> wholeFile = (List<List<String>>) object;
        List<String> stringList = wholeFile.get(index);
        stringList.add(friendName);
        wholeFile.set(index, stringList);
        writeUserFile(username, wholeFile);
        return new AllIsWell();
    }

    public static boolean findInUserFile(String username, String friendName, int index) {
        Object object = parseUserFile(username);
        if ( object instanceof NoSuchUser ) {
            return false;
        }
        List<List<String>> wholeFile = (List<List<String>>) object;
        List<String> stringList = wholeFile.get(index);
        return stringList.contains(friendName);
    }

    /**
     * Using this to remove data from the user file
     *
     * @param username   username file to delete from
     * @param friendName string to delete
     * @param index      0 is friend list. 1 is friend requests.
     * @return ExceptionObject from handlers.
     */
    private static Response removeFromUserFile(String username, String friendName, int index) {
        Object object = parseUserFile(username);
        if ( object instanceof Response ) {
            return (Response) object;
        }
        List<List<String>> wholeFile = (List<List<String>>) object;

        List<String> stringList = wholeFile.get(index);
        stringList.remove(friendName);
        wholeFile.set(index, stringList);
        writeUserFile(username, wholeFile);
        return new AllIsWell();
    }

    private static Object parseUserFile(String userName) {
        File userFile = new File("users/" + userName);

        if ( !userFile.exists() ) {
            System.out.println(userName + " doesn't exist");
            return new NoSuchUser();
        }

        BufferedReader reader = null;
        String friendLine = null;
        try {
            reader = new BufferedReader(new FileReader(userFile));
            friendLine = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> friends = new ArrayList<>();

        if ( null != friendLine && !friendLine.isEmpty() ) {
            friendLine = friendLine.substring(1, friendLine.length() - 1);
            try {
                friends = new ArrayList<>(Arrays.asList(friendLine.split(separator)));
            } catch (Exception e) {
                System.out.println("Error while loading friends for " + userName
                                   + " maybe he just doesn't have any friends" + e);
            }
        }

        String requestLine = null;
        try {
            if ( reader != null ) {
                requestLine = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> requests = new ArrayList<>();

        if ( null != requestLine && !requestLine.isEmpty() ) {
            requestLine = requestLine.substring(1, requestLine.length() - 1);
            try {
                requests = new ArrayList<>(Arrays.asList(requestLine.split(separator)));
            } catch (Exception e) {
                System.out.println("Error while loading friend requests for " + userName
                                   + " maybe he just doesn't have any friends " + e);
            }
        }

        List<List<String>> result = new ArrayList<>();
        result.add(friends);
        result.add(requests);

        try {
            if ( reader != null ) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // TODO: This doesn't work
    private static void writeUserFile(String userName, List<List<String>> in) {
        File file = new File("users/" + userName);
        System.out.println(file.exists());
        Path path = FileSystems.getDefault().getPath("users/" + userName);
        try {
            Files.delete(path);
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        /*if ( !file.delete() )
            System.out.println("Deleting file that doesn't exist. users/" + userName);*/

        file = new File("users/" + userName);

        List<String> friends = in.get(0);
        List<String> friendReqs = in.get(1);
        String friendStr = listToString(friends);
        String friendReqStr = listToString(friendReqs);

        PrintWriter out;
        try {
            out = new PrintWriter(new FileWriter(file, true));

            out.println(friendStr);
            out.println(friendReqStr);

            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToString(List<String> list) {
        if ( list.size() == 0 ) {
            return "";
        }

        StringBuilder res = new StringBuilder("\"");
        for ( String s : list ) {
            res.append(s).append(separator);
        }
        String result = res.toString();

        return result.substring(0, result.length() - 2);
    }
}
