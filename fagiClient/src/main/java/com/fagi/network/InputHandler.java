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
import com.fagi.responses.Response;
import com.fagi.utility.Logger;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Handling every incoming object from the server.
 * TODO: Write description
 */
public class InputHandler implements Runnable {
    private final Queue<Response> inputs = new LinkedBlockingQueue<>();
    private final ObjectInputStream in;
    private final EncryptionAlgorithm encryption;
    private Thread distributorThread;
    private boolean running = true;
    private InputDistributor distributor;

    public InputHandler(ObjectInputStream in, EncryptionAlgorithm encryption) {
        this.in = in;
        this.encryption = encryption;
    }

    public void setupDistributor() {
        distributor = new InputDistributor();
        distributorThread = new Thread(distributor);
        distributorThread.start();
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
                        System.err.println("inputhandler ioe: " + ioe.toString());
                        ioe.printStackTrace(); // DEBUG need to terminate before closing socket.
                        Logger.logStackTrace(ioe);
                    }
                    running = false;
                    ChatManager.closeCommunication();
                    Platform.runLater(() -> ChatManager.getApplication().showLoginScreen());
                } catch (ClassNotFoundException cnfe) {
                    // Shared files are not the same on both side of the server
                    System.err.println(cnfe.getMessage());
                    Logger.logStackTrace(cnfe);
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
        addIngoingMessage((InGoingMessages) input);
    }

    public void addIngoingMessage(InGoingMessages input) {
        distributor.addMessage(input);
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

    public void close() {
        running = false;
        distributor.stop();
        distributorThread.interrupt();
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