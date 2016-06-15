package com.fagi.network;
/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * InputHandler.java
 */

import com.fagi.model.FriendList;
import com.fagi.model.FriendRequestList;
import com.fagi.model.Message;
import com.fagi.model.VoiceMessage;
import com.fagi.responses.Response;
import com.fagi.encryption.Conversion;
import com.fagi.encryption.EncryptionAlgorithm;
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
    private final EncryptionAlgorithm encryption;
    private boolean running = true;

    public InputHandler(ObjectInputStream in, EncryptionAlgorithm encryption) {
        this.in = in;
        this.encryption = encryption;
    }

    @Override
    public void run() {
        byte[] input = null;
        while ( running ) {
            while ( input == null && running ) {
                try {
                    input = (byte[])in.readObject();
                    handleInput(Conversion.convertFromBytes(encryption.decrypt(input)));
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
            input = null;
        }
    }

    private void handleInput(Object object) {
        if ( object instanceof ResponseList ) {

        } else if ( object instanceof Message ) {

        } else {
            inputs.add(object);
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
        return null;
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
        return null;
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

    public Response containsResponse() {
        Response response;
        for ( Object object : inputs ) {
            if ( !(object instanceof Response ) ) {
                continue;
            }

            response = (Response) object;
            inputs.remove(object);
            return response;
        }
        return null;
    }
}