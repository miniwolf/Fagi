/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

import com.fagi.model.messages.message.Message;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.UserOnline;
import com.fagi.model.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author miniwolf
 */
public class InputWorker extends Worker {
    private ObjectInputStream objIn;
    private OutputWorker out;

    public InputWorker(Socket socket, OutputWorker out) throws IOException {
        System.out.println("Starting an input thread");
        objIn = new ObjectInputStream(socket.getInputStream());
        this.out = out;
    }

    @Override
    public void run() {
        while ( running ) {
            System.out.println("Running");
            try {
                Object input = objIn.readObject();
                handleInput(input);
            } catch (EOFException | SocketException eof) {
                running = false;
                System.out.println("Logging out user " + myUserName);
                Data.userLogout(myUserName);
            } catch (Exception e) {
                running = false;
                System.out.println("Something went wrong in a input worker while loop " + e);
                System.out.println("Logging out user " + myUserName);
                Data.userLogout(myUserName);
            }
        }
        System.out.println("Closing");
    }

    private void handleInput(Object input) {
        if ( input instanceof Message ) {
            Message arg = (Message) input;
            out.addResponse(handleMessage(arg));
        } else if ( input instanceof Login ) {
            Login arg = (Login) input;
            out.addResponse(handleLogin(arg));
        } else if ( input instanceof Logout ) {
            out.addResponse(handleLogout());
        } else if ( input instanceof CreateUser ) {
            CreateUser arg = (CreateUser) input;
            out.addResponse(handleCreateUser(arg));
        } else if ( input instanceof FriendRequest ) {
            FriendRequest arg = (FriendRequest) input;
            out.addResponse(handleFriendRequest(arg));
        } else if ( input instanceof DeleteFriendRequest ) {
            DeleteFriendRequest arg = (DeleteFriendRequest) input;
            out.addResponse(handleDeleteFriendRequest(arg));
        } else if ( input instanceof DeleteFriend ) {
            DeleteFriend arg = (DeleteFriend) input;
            out.addResponse(handleDeleteFriend(arg));
        } else {
            System.out.println("Unknown handle: " + input.getClass().toString());
        }
    }

    private Object handleDeleteFriend(DeleteFriend arg) {
        System.out.println("Delete Friend");
        return Data.getUser(myUserName).removeFriend(arg.getFriendUsername());
    }

    private Object handleDeleteFriendRequest(DeleteFriendRequest arg) {
        System.out.println("DeleteFriendRequest");
        return Data.getUser(myUserName).removeFriendRequest(arg.getFriendUsername());
    }

    private Object handleMessage(Message arg) {
        System.out.println("Message");

        if ( Data.isUserOnline(arg.getSender()) && Data.isUserOnline(arg.getReceiver()) ) {
            Data.getWorker(arg.getReceiver()).addMessage(arg);
            return new AllIsWell();
        }
        return new UserOnline();
    }

    private Object handleLogin(Login arg) {
        System.out.println("Login");
        myUserName = arg.getUsername();
        return Data.userLogin(arg.getUsername(), arg.getPassword(), out);
    }

    private Object handleLogout() {
        System.out.println("Logout");
        Data.userLogout(myUserName);
        running = false;
        return new AllIsWell();
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
}
