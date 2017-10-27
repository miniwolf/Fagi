/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Communication.java
 *
 * Handling in and output
 */

package com.fagi.network;

import com.fagi.conversation.Conversation;
import com.fagi.encryption.*;
import com.fagi.model.HistoryUpdates;
import com.fagi.model.Session;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.Response;
import com.fagi.utility.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.PublicKey;

/**
 * TODO: Add description
 */
public class Communication {
    private ObjectOutputStream out;
    private InputHandler inputHandler;
    private Socket socket;
    private Thread inputThread;
    private EncryptionAlgorithm encryption;
    private ObjectInputStream inputStream;

    public Communication() {
    }

    public Communication(String host, int port, EncryptionAlgorithm encryption, PublicKey serverKey) throws IOException {
        this.encryption = encryption;
        try {
            socket = new Socket(InetAddress.getLocalHost(), port);
            out = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            setupInputHandler(encryption, inputStream);
            createSession(encryption, serverKey);
        } catch (UnknownHostException Uhe) {
            System.err.println("c Uhe: " + Uhe);
            Logger.logStackTrace(Uhe);
        } catch (IOException ioe) {
            Logger.logStackTrace(ioe);
            throw new IOException("c ioe: " + ioe.toString());
        }
    }

    public void setupInputHandler(EncryptionAlgorithm encryption, ObjectInputStream in) {
        inputHandler = new InputHandler(in, encryption);
        inputHandler.setupDistributor();
        inputThread = new Thread(inputHandler);
        inputThread.setDaemon(true);
        inputThread.start();
    }

    private void createSession(EncryptionAlgorithm encryption, PublicKey serverKey) throws IOException {
        RSA rsa = new RSA();
        rsa.setEncryptionKey(new RSAKey(new KeyPair(serverKey, null)));
        out.writeObject(rsa.encrypt(Conversion.convertToBytes(new Session((AESKey) encryption.getKey()))));
        out.flush();

        Response obj;
        while ((obj = inputHandler.containsResponse()) == null) {}
        if (obj instanceof AllIsWell) {

        }
    }

    public void sendObject(Object obj) {
        try {
            out.writeObject(encryption.encrypt(Conversion.convertToBytes(obj)));
            out.flush();
        } catch (IOException e) {
            System.err.println("cso ioe: " + e.toString());
            e.printStackTrace();
            Logger.logStackTrace(e);
            System.exit(1);
        }
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

    public Response getNextResponse() {
        Response response;
        while ( (response = inputHandler.containsResponse()) == null ) {}
        return response;
    }

    public Conversation getConversation() {
        return inputHandler.containsConversation();
    }

    public HistoryUpdates getHistoryUpdates() {
        return inputHandler.containsHistoryUpdates();
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public void setEncryption(EncryptionAlgorithm encryption) {
        this.encryption = encryption;
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }
}