/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * InputHandler.java
 */

package com.fagi.network;

import com.fagi.encryption.Conversion;
import com.fagi.encryption.EncryptionAlgorithm;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.network.handlers.container.Container;
import com.fagi.responses.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
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

    private void handleInput(Object input) {
        if ( input instanceof Response ) {
            inputs.add((Response) input);
            return;
        }
        while ( containers.size() < 4 ) { // TODO: Magic number hack
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Container container = InputHandler.containers.get(input.getClass());
        if ( container == null ) {
            System.err.println("Missing handler: " + input.getClass());
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
}