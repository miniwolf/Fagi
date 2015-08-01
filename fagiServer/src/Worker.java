/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Worker.java
 *
 * Worker thread for each client.
 */

import com.fagi.exceptions.AllIsWellException;
import com.fagi.exceptions.NoSuchUserException;
import com.fagi.exceptions.UserOnlineException;
import com.fagi.model.*;

import java.io.IOException;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

class Worker implements Runnable {
    private final Queue<Message> incMessages = new ConcurrentLinkedQueue<>();
    private ObjectInputStream oIn;
    private ObjectOutputStream oOut;
    private boolean running = true;
    private String myUserName;

    public Worker(Socket s) throws IOException {
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
                System.out.println("Logging out user " + myUserName);
                Data.userLogout(myUserName);
            } catch (Exception e) {
                running = false;
                System.out.println("Something went wrong in a worker while loop " + e);
                e.printStackTrace();
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
        } else if ( input instanceof FriendDelete ) {
            FriendDelete arg = (FriendDelete) input;
            return handleFriendDelete(arg);
        }/* else if ( input instanceof GetSound) {
// TODO: Implement or at least think about it
        }*/ else {
            return handleUnknownObject(input);
        }
    }

    private Object handleFriendDelete(FriendDelete arg) {
        return Data.removeFriendFromUserFile(myUserName, arg.getFriendUsername());
    }

    private Object handleGetFriends() {
        return getOnlineFriends();
    }

    private Object handleGetRequests() {
        return getFriendRequests();
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
        return Data.userLogin(arg.getUsername(), arg.getPassword(), this);
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
            return Data.createUser(arg.getUsername(), arg.getPassword());
        } catch (Exception e) {
            return e;
        }
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

    private Object getOnlineFriends() {
        User me = Data.getUser(myUserName);
        if ( me == null )
            return new NoSuchUserException();

        return me.getFriends().stream().filter(Data::isUserOnline).collect(Collectors.toList());
    }

    private Object getFriendRequests() {
        User me = Data.getUser(myUserName);
        if ( me == null )
            return new NoSuchUserException();

        return me.getFriendReq();
    }

    synchronized void addMessage(Message m) {
        incMessages.add(m);
    }
}