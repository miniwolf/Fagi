/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * Worker.java
 *
 * Worker thread for each client.
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class Worker extends Thread {
    private final Queue<Message> incMessages = new ConcurrentLinkedQueue<Message>();
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
        try {
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
            } else if ( input instanceof CreateUser ) {
                CreateUser arg = (CreateUser) input;
                return handleCreateUser(arg);
            } else if ( input instanceof FriendRequest ) {
                FriendRequest arg = (FriendRequest) input;
                return handleFriendRequest(arg);
            } else {
                return handleUnknownObject(input);
            }
        } catch (Exception e) {
            return e;
        }
    }

    private Object handleGetFriends() {
        try {
            return getOnlineFriends();
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

    private Object handleCreateUser(CreateUser arg) throws IOException {
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

        List<String> list = new ArrayList<String>();
        for ( String friendName : me.getFriends() ) if ( Data.isUserOnline(friendName) )
            list.add(friendName);
        return list;
    }

    synchronized void addMessage(Message m) {
        incMessages.add(m);
    }
}