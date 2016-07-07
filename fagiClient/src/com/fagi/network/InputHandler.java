/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * InputHandler.java
 */

package com.fagi.network;

import com.fagi.conversation.Conversation;
import com.fagi.encryption.Conversion;
import com.fagi.encryption.EncryptionAlgorithm;
import com.fagi.model.HistoryUpdates;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.network.handlers.container.Container;
import com.fagi.responses.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Handling every incoming object from the server.
 * TODO: Write description
 */
public class InputHandler implements Runnable {
    private final Queue<Response> inputs = new LinkedBlockingQueue<>();
    private final List<Object> unhandledObjects = new ArrayList<>();
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

                    for ( int i = 0; i < unhandledObjects.size(); i++ ) {
                        Object obj = unhandledObjects.remove(i);
                        handleInput(obj);
                    }
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

    private void handleInput(Object input) {
        if ( input instanceof Response ) {
            inputs.add((Response) input);
            return;
        }
        Container container = InputHandler.containers.get(input.getClass());
        if ( container == null ) {
            System.err.println("Missing handler: " + input.getClass());
            unhandledObjects.add(input);
            return;
        }
        container.addObject((InGoingMessages) input);
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

    public Conversation containsConversation() {
        Conversation con;
        for (Object object : inputs) {
            if ( !(object instanceof Conversation )) {
                continue;
            }

            con = (Conversation) object;
            inputs.remove(object);
            return con;
        }
        return null;
    }

    public HistoryUpdates containsHistoryUpdates() {
        HistoryUpdates updates;
        for (Object object : inputs) {
            if ( !(object instanceof HistoryUpdates )) {
                continue;
            }

            updates = (HistoryUpdates) object;
            inputs.remove(object);
            return updates;
        }
        return null;
    }
}