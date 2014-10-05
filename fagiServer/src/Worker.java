/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Worker.java
 *
 * Worker thread for each client.
 */

import exceptions.AllIsWellException;
import exceptions.NoSuchUserException;
import exceptions.UserOnlineException;
import model.*;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

class Worker extends Thread {
    private final Queue<Message> incMessages = new ConcurrentLinkedQueue<>();
    private ObjectInputStream oIn;
    private ObjectOutputStream oOut;
    private boolean running = true;
    private String myUserName;

    public Worker(Socket s) throws Exception {
        System.out.println("Starting a worker thread");
        oIn = new ObjectInputStream(s.getInputStream());
        oOut = new ObjectOutputStream(s.getOutputStream());
    }

    @Override
    public void run() {
        while ( running ) {
            try {
                sendIncMessages();
                Object input = oIn.readObject();
                oOut.writeObject(handleInput(input));
            } catch (EOFException eof) {
                running = false;
            } catch (Exception e) {
                running = false;
                System.out.println("Something went wrong in a worker while loop " + e);
                e.printStackTrace();
            } finally {
                System.out.println("Logging out user " + myUserName);
                Data.userLogout(myUserName);
            }
        }
    }

    private void sendIncMessages() throws Exception {
        while ( incMessages.size() > 0 )
            oOut.writeObject(incMessages.remove());
    }

    private Object handleInput(Object input) {
        if ( input instanceof Message ) {
            Message arg = (Message) input;
            return handleMessage(arg);
        } else if ( input instanceof Login) {
            Login arg = (Login) input;
            return handleLogin(arg);
        } else if ( input instanceof Logout) {
            Logout arg = (Logout) input;
            return handleLogout(arg);
        } else if ( input instanceof GetFriends ) {
            return handleGetFriends();
        } else if ( input instanceof GetRequests) {
            return handleGetRequests();
        } else if ( input instanceof CreateUser ) {
            CreateUser arg = (CreateUser) input;
            return handleCreateUser(arg);
        } else if ( input instanceof FriendRequest ) {
            FriendRequest arg = (FriendRequest) input;
            return handleFriendRequest(arg);
        } else {
            return handleUnknownObject(input);
        }
    }

    private Object handleGetFriends() {
        try {
            return getOnlineFriends();
        } catch (Exception e) {
            return e;
        }
    }

    private Object handleGetRequests() {
        try {
            return getFriendRequests();
        } catch (Exception e) {
            return e;
        }
    }

    private Object handleMessage(Message arg) {
        System.out.println("Message");

        if ( Data.isUserOnline(arg.getSender()) && Data.isUserOnline(arg.getReceiver()) ) {
            Data.getWorker(arg.getReceiver()).addMessage(arg);
            return new AllIsWellException();
        }
        return new UserOnlineException();
    }

    private Object handleLogin(Login arg) {
        System.out.println("Login");
        myUserName = arg.getUsername();
        try {
            Data.userLogin(arg.getUsername(), arg.getPassword(), this);
        } catch (Exception e) {
            return e;
        }
        return new AllIsWellException();
    }

    private Object handleLogout(Logout arg) {
        System.out.println("Logout");
        myUserName = arg.getUsername();
        Data.userLogout(myUserName);
        running = false;
        return new AllIsWellException();
    }

    private Object handleCreateUser(CreateUser arg) {
        System.out.println("CreateUser");
        try {
            Data.createUser(arg.getUsername(), arg.getPassword());
        } catch (Exception e) {
            return e;
        }
        return new AllIsWellException();
    }

    private Object handleFriendRequest(FriendRequest arg) {
        System.out.println("FriendRequest");
        try {
            Data.getUser(myUserName).requestFriend(arg.getFriendUsername());
        } catch (Exception e) {
            return e;
        }
        return new AllIsWellException();
    }

    private Object handleUnknownObject(Object input) {
        System.out.println(input.getClass().toString());
        return new Exception();
    }

    private List<String> getOnlineFriends() throws Exception {
        User me = Data.getUser(myUserName);
        if ( me == null )
            throw new NoSuchUserException();

        return me.getFriends().stream().filter(Data::isUserOnline).collect(Collectors.toList());
    }

    private List<String> getFriendRequests() throws NoSuchUserException {
        User me = Data.getUser(myUserName);
        if ( me == null )
            throw new NoSuchUserException();

        return me.getFriendReq();
    }

    synchronized void addMessage(Message m) {
        incMessages.add(m);
    }
}