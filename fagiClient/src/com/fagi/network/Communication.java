package com.fagi.network;/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Communication.java
 *
 * Handling in and output
 */

import com.fagi.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

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
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            System.err.println("cso ioe: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Object handleObjects() {
        Object object = null;
        while ( object == null )
            object = inputHandler.getLast();
        return object;
    }

    /**
     * Getting the friend/friend request list from the server.
     * Using object to pass the correct get requests to the server.
     *
     * @return List<String> which contains all of our friends as strings.
     */
    public List<String> getList(Object object) {
        List<String> list = null;
        sendObject(object);
        try {
            list = inputHandler.containsList();
        } catch (IllegalArgumentException e) {
            System.err.println("c iae: " + e.toString());
        }
        return list;
    }

    // TODO: Need to close correctly?
    public void close() {
        inputHandler.close();
        try {
            socket.close();
        } catch (IOException ioe) {
            System.err.println("cc ioe: " + ioe.toString());
        }
    }
}