/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * InputHandler.java
 */

package com.fagi.network;

import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.model.messages.lists.FriendRequestList;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.model.messages.message.VoiceMessage;
import com.fagi.network.handlers.Container;
import com.fagi.responses.Response;
import com.fagi.encryption.Conversion;
import com.fagi.encryption.EncryptionAlgorithm;
import com.fagi.model.FriendList;
import com.fagi.model.FriendRequestList;
import com.fagi.model.Message;
import com.fagi.model.VoiceMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Handling every incoming object from the server.
 * TODO: Write description
 */
public class InputHandler implements Runnable {
    private final LinkedBlockingDeque<Object> inputs = new LinkedBlockingDeque<>();
    private static final Map<Class, Container> containers =
            new ConcurrentHashMap<>();
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
        if ( object instanceof FriendList ) {
            containers.get(FriendList.class).addObject((InGoingMessages) object);
        } else if ( object instanceof FriendRequestList ) {
            containers.get(FriendRequestList.class).addObject((FriendRequestList) object);
        } else if ( object instanceof TextMessage ) {
            containers.get(TextMessage.class).addObject((TextMessage) object);
        } else if ( object instanceof VoiceMessage ) {
            containers.get(VoiceMessage.class).addObject((VoiceMessage) object);
        } else {
            inputs.add(object);
        }
    }

    /**
     * The server will return response objects to answer requests, or tell the client of an error
     * occurring on the server.
     *
     * @return Response object describing the error otherwise AllIsWellException
     */
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

    public static void register(Class clazz, Container handler) {
        containers.put(clazz, handler);
    }

    public void close() {
        running = false;
    }
}