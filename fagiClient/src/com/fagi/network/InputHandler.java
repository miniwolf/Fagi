package com.fagi.network;
/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * InputHandler.java
 */

import com.fagi.model.FriendList;
import com.fagi.model.FriendRequestList;
import com.fagi.model.Message;
import com.fagi.model.VoiceMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Handling every incoming object from the server.
 * TODO: Write description
 */
class InputHandler implements Runnable {
    private final LinkedBlockingDeque<Object> inputs = new LinkedBlockingDeque<>();
    private final ObjectInputStream in;
    private boolean running = true;

    // TODO : Get RSA object from Communication
    public InputHandler(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        Object object = null;
        while ( running ) {
            while ( object == null && running ) {
                try {
                    object = in.readObject();
                    // TODO : Check if byte[], decrypt array, convert to object
                    inputs.add(object);
                } catch (IOException ioe) {
                    if ( running ) {
                        System.err.println("i ioe: " + ioe.toString());
                        ioe.printStackTrace(); // DEBUG need to terminate before closing socket.
                    }
                    return;
                } catch (ClassNotFoundException cnfe) {
                    // Shared files are not the same on both side of the server
                    System.err.println(cnfe.getMessage());
                    // TODO: This will be a bitch when having to update the server
                    // Fix: could be to implement JSON
                }
            }
            object = null;
        }
    }

    public Object getLast() {
        if ( inputs.isEmpty() ) {
            return null;
        }
        return inputs.pollLast();
    }

    /**
     * Checking our queue for a list from the server,
     * if the list contains something not a string,
     * we will throw an IllegalArgumentException.
     *
     * @return FriendRequestList containing all the requests
     * @throws IllegalArgumentException meaning the server
     *         has given some illegal list
     */
    public FriendRequestList containsRequests() throws IllegalArgumentException {
        for ( Object object : inputs ) {
            if ( !(object instanceof FriendRequestList) ) {
                continue;
            }
            inputs.remove(object);
            return (FriendRequestList) object;
        }
        return new FriendRequestList(new ArrayList<>());
    }

    /**
     * Checking our queue for a list from the server,
     * if the list contains something not a string,
     * we will throw an IllegalArgumentException.
     *
     * @return FriendList containing all the friends
     * @throws IllegalArgumentException meaning the server
     *         has given some illegal list
     */
    public FriendList containsFriends() throws IllegalArgumentException {
        for ( Object object : inputs ) {
            if ( !(object instanceof FriendList) ) {
                continue;
            }
            inputs.remove(object);
            return (FriendList) object;
        }
        return new FriendList(new ArrayList<>());
    }

    public Message containsMessage() {
        Message message;
        for ( Object object : inputs ) {
            if ( !(object instanceof Message) ) {
                continue;
            }
            message = (Message) object;
            if ( !message.isSystemMessage() ) {
                inputs.remove(object);
                return message;
            }
        }
        return null;
    }

    public void close() {
        running = false;
    }

    public Message containsVoice() {
        Message message;
        for ( Object object : inputs ) {
            if ( !(object instanceof VoiceMessage) ) {
                continue;
            }

            message = (VoiceMessage) object;
            inputs.remove(object);
            return message;
        }
        return null;
    }

    public Exception containsException() {
        Exception exception;
        for ( Object object : inputs ) {
            if ( !(object instanceof Exception ) ) {
                continue;
            }

            exception = (Exception) object;
            inputs.remove(object);
            return exception;
        }
        return null;
    }
}