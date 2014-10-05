/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Data.java
 *
 * Contains and update information on users.
 */

import exceptions.NoSuchUserException;
import exceptions.PasswordException;
import exceptions.UserOnlineException;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * TODO: Add description, maybe think about .fagi file ending
 */
class Data {
    private static final Map<String, Worker> onlineUsers = new HashMap<>();
    private static final Map<String, User> registeredUsers = new HashMap<>();
    private static final String separator = "\",\"";
    private static final String indexFilePath = "users/userIndex.fagi";

    public static void createUser(String userName, String pass) throws IOException {
        if ( registeredUsers.containsKey(userName) ) return;

        registeredUsers.put(userName, new User(userName, pass));

        PrintWriter out = new PrintWriter(new FileWriter(indexFilePath, true));
        out.println("\"" + userName + separator + pass + "\"");
        out.flush();
        out.close();

        File file = new File("users/" + userName);

        if ( !file.createNewFile() )
            System.out.println(userName + ". File already exists, supposed bug. Report please!");
    }

    public static void userLogin(String userName, String pass, Worker w) throws Exception {
        if ( onlineUsers.containsKey(userName) )
            throw new UserOnlineException();
        if ( !registeredUsers.containsKey(userName) )
            throw new NoSuchUserException();
        if ( !registeredUsers.get(userName).getPass().equals(pass) )
            throw new PasswordException();
        onlineUsers.put(userName, w);
    }

    public static void userLogout(String userName) {
        if ( onlineUsers.containsKey(userName) )
            onlineUsers.remove(userName);
        else
            System.out.println("Couldn't logout");
    }

    public static boolean isUserOnline(String userName) {
        return onlineUsers.containsKey(userName);
    }

    public static void makeFriends(User a, User b) throws Exception {
        a.addFriend(b);
        b.addFriend(a);

        appendFriendToUserFile(a.getUserName(), b.getUserName());
        appendFriendToUserFile(b.getUserName(), a.getUserName());
    }

    public static User getUser(String name) {
        return registeredUsers.get(name);
    }

    public static Worker getWorker(String userName) {
        return onlineUsers.get(userName);
    }

    public static void readInData() {
        try {
            File f = new File("users/");
            File indexFile = new File(indexFilePath);
            if ( !f.exists() || !indexFile.exists() ) {
                if ( !f.mkdir() )
                    System.err.println("Couldn't create folder");
                if ( !indexFile.createNewFile() )
                    System.err.println("Can't create file");
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
        for (String s : tmpUsernameList) {
            User user = registeredUsers.get(s);
            System.out.println("Now parsing: " + s);
            try {
                List<List<String>> tmp = parseUserFile(s);
                user.addFriends(tmp.get(0));
                user.addFriendReqs(tmp.get(1));
            } catch (Exception ignored) {
            }
        }
    }

    private static void appendFriendToUserFile(String username, String friendName) throws Exception {
        appendToUserFile(username, friendName, 0);
        removeFromUserFile(username, friendName, 1);
    }

    public static void appendFriendReqToUserFile(String username, String friendName) throws Exception {
        appendToUserFile(username, friendName, 1);
    }

    private static void appendToUserFile(String username, String friendName, int index) throws Exception {
        List<List<String>> wholeFile = parseUserFile(username);
        List<String> stringList = wholeFile.get(index);
        stringList.add(friendName);
        wholeFile.set(index, stringList);
        writeUserFile(username, wholeFile);
    }

    private static void removeFromUserFile(String username, String friendName, int index) throws Exception {
        List<List<String>> wholeFile = parseUserFile(username);
        List<String> stringList = wholeFile.get(index);
        stringList.remove(friendName);
        wholeFile.set(index, stringList);
        writeUserFile(username, wholeFile);
    }

    private static List<List<String>> parseUserFile(String userName) throws Exception {
        File userFile = new File("users/" + userName);

        if ( !userFile.exists() ) {
            System.out.println(userName + " doesn't exist");
            throw new Exception();
        }

        BufferedReader reader = new BufferedReader(new FileReader(userFile));
        String friendLine = reader.readLine();
        List<String> friends = new ArrayList<>();

        if ( null != friendLine && !friendLine.isEmpty() ) {
            friendLine = friendLine.substring(1, friendLine.length() - 1);
            try {
                friends = new ArrayList<>(Arrays.asList(friendLine.split(separator)));
            } catch (Exception e) {
                System.out.println("Error while loading friends for " + userName + " maybe he just doesn't have any friends" + e);
            }
        }

        String requestLine = reader.readLine();
        List<String> requests = new ArrayList<>();

        if ( null != requestLine && !requestLine.isEmpty() ) {
            requestLine = requestLine.substring(1, requestLine.length() - 1);
            try {
                requests = new ArrayList<>(Arrays.asList(requestLine.split(separator)));
            } catch (Exception e) {
                System.out.println("Error while loading friend requests for " + userName + " maybe he just doesn't have any friends " + e);
            }
        }

        List<List<String>> result = new ArrayList<>();
        result.add(friends);
        result.add(requests);

        reader.close();
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
        if ( list.size() == 0 ) return "";

        StringBuilder res = new StringBuilder("\"");
        for ( String s : list )
            res.append(s).append(separator);
        String result = res.toString();

        return result.substring(0, result.length() - 2);
    }

    public static void printList(List<String> l) {
        for ( String s : l )
            System.out.print(s + " ");
        System.out.println();
    }
}
