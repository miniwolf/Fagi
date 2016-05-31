package com.fagi.network;/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Communication.java
 *
 * Handling in and output
 */

import com.fagi.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * TODO: Add description
 */
public class Communication {
    private ObjectOutputStream out;
    private static final String host = "localhost";
    private static final int port = 4242;
    private InputHandler inputHandler;
    private Socket socket;
    private Thread inputThread;

    public Communication() throws IOException {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            inputHandler = new InputHandler(new ObjectInputStream(socket.getInputStream()));
            inputThread = new Thread(inputHandler);
            inputThread.setDaemon(true);
            inputThread.start();
        } catch (UnknownHostException Uhe) {
            System.err.println("c Uhe: " + Uhe);
        } catch (IOException ioe) {
            throw new IOException("c ioe: " + ioe.toString());
        }
    }

    public Message receiveMessage() {
        Message message = inputHandler.containsMessage();
        if ( message == null ) {
            return inputHandler.containsVoice();
        }
        return message;
    }

    public void sendObject(Object obj) {
        try {
            // TODO : Convert object to byte[] and encrypt with server public key
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            System.err.println("cso ioe: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Do not use this method anymore.
     * This might give you the wrong object or even none
     * if it has not reached the input yet.
     *
     * @return Current object in the bottom of queue. (Newest Object)
     * @deprecated use contains"Object" methods instead.
     */
    public Object handleObjects() {
        Object object = null;
        while ( object == null ) {
            object = inputHandler.getLast();
        }
        return object;
    }

    /**
     * Getting the friend/friend request list from the server.
     * Using object to pass the correct get requests to the server.
     *
     * @return FriendRequestList which contains all of our requests as strings.
     */
    public FriendRequestList getRequests() {
        sendObject(new GetFriendRequests());
        return inputHandler.containsRequests();
    }

    /**
     * Getting the friend list from the server.
     *
     * @return FriendList which contains all of our friends as strings.
     */
    public FriendList getFriends() {
        sendObject(new GetFriends());
        return inputHandler.containsFriends();
    }

    // TODO: Need to close correctly?
    public void close() {
        inputHandler.close();
        inputThread.interrupt();
        try {
            socket.close();
        } catch (IOException ioe) {
            System.err.println("cc ioe: " + ioe.toString());
        }
    }

    public Exception getNextException() {
        return inputHandler.containsException();
    }
}