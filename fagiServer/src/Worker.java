/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Worker.java
 *
 * Worker thread for each client.
 */

import com.fagi.encryption.AESKey;
import com.fagi.encryption.Conversion;
import com.fagi.encryption.EncryptionAlgorithm;
import com.fagi.exceptions.AllIsWellException;
import com.fagi.exceptions.NoSuchUserException;
import com.fagi.exceptions.UserOnlineException;
import com.fagi.model.*;

import java.io.EOFException;
import java.io.IOException;
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
    private boolean logedin = false;
    private String myUserName;
    private EncryptionAlgorithm<AESKey> aes;

    public Worker(Socket socket) throws IOException {
        System.out.println("Starting a worker thread");
        oIn = new ObjectInputStream(socket.getInputStream());
        oOut = new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        while ( running ) {
            System.out.println("Running");
            try {
                sendIncMessages();
                Object input = oIn.readObject();

                if (input instanceof byte[]) {
                    input = decryptAndConvertToObject((byte[])input);
                }

                oOut.writeObject(handleInput(input));

                oOut.reset();
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
        System.out.println("Closing");
    }

    private Object decryptAndConvertToObject(byte[] input) {
        if (logedin) {
            input = aes.decrypt(input);
        } else {
            input = Encryption.getInstance().getRSA().decrypt(input);
        }
        try {
            return Conversion.convertFromBytes(input);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendIncMessages() throws Exception {
        while ( incMessages.size() > 0 ) {
            oOut.writeObject(incMessages.remove());
        }
    }

    private Object handleInput(Object input) {
        if ( input instanceof Message ) {
            Message arg = (Message) input;
            return handleMessage(arg);
        } else if ( input instanceof Login ) {
            Login arg = (Login) input;
            return handleLogin(arg);
        } else if ( input instanceof Logout ) {
            Logout arg = (Logout) input;
            return handleLogout();
        } else if ( input instanceof GetFriends ) {
            return handleGetFriends();
        } else if ( input instanceof GetFriendRequests) {
            return handleGetRequests();
        } else if ( input instanceof CreateUser ) {
            CreateUser arg = (CreateUser) input;
            return handleCreateUser(arg);
        } else if ( input instanceof FriendRequest ) {
            FriendRequest arg = (FriendRequest) input;
            return handleFriendRequest(arg);
        } else if ( input instanceof DeleteFriendRequest ) {
            DeleteFriendRequest arg = (DeleteFriendRequest) input;
            return handleDeleteFriendRequest(arg);
        } else if ( input instanceof DeleteFriend ) {
            DeleteFriend arg = (DeleteFriend) input;
            return handleDeleteFriend(arg);
        } else {
            return handleUnknownObject(input);
        }
        // else if ( input instanceof GetSound) {
        // TODO: Implement or at least think about it
        //}
    }

    private Object handleDeleteFriend(DeleteFriend arg) {
        System.out.println("Delete Friend");
        return Data.getUser(myUserName).removeFriend(arg.getFriendUsername());
    }

    private Object handleDeleteFriendRequest(DeleteFriendRequest arg) {
        System.out.println("DeleteFriendRequest");
        return Data.getUser(myUserName).removeFriendRequest(arg.getFriendUsername());
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

    private Object handleLogout() {
        System.out.println("Logout");
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
        return Data.getUser(myUserName).requestFriend(arg.getFriendUsername());
    }

    private Object handleUnknownObject(Object input) {
        System.out.println(input.getClass().toString());
        return new Exception();
    }

    private Object getOnlineFriends() {
        User me = Data.getUser(myUserName);
        if ( me == null ) {
            return new NoSuchUserException();
        }
        return new FriendList(me.getFriends().stream().filter(Data::isUserOnline)
                                .collect(Collectors.toList()));
    }

    private Object getFriendRequests() {
        User me = Data.getUser(myUserName);
        if ( me == null ) {
            return new NoSuchUserException();
        }
        //return new FriendRequestList(me.getFriendReq().stream().collect(Collectors.toList()));
        return new FriendRequestList(me.getFriendReq());
    }

    synchronized void addMessage(Message message) {
        incMessages.add(message);
    }
}