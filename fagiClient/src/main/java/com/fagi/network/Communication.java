/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Communication.java
 *
 * Handling in and output
 */

package com.fagi.network;

import com.fagi.conversation.Conversation;
import com.fagi.encryption.AESKey;
import com.fagi.encryption.Conversion;
import com.fagi.encryption.EncryptionAlgorithm;
import com.fagi.encryption.RSA;
import com.fagi.encryption.RSAKey;
import com.fagi.logging.FagiLogger;
import com.fagi.logging.FagiLoggerFactory;
import com.fagi.model.HistoryUpdates;
import com.fagi.model.Session;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.Response;
import com.fagi.threads.ThreadPool;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.PublicKey;

/**
 * TODO: Add description
 */
public class Communication {
    private static final FagiLogger LOGGER = FagiLoggerFactory.createLogger(Communication.class);
    private ObjectOutputStream out;
    private InputHandler inputHandler;
    private Socket socket;
    private String name;
    private String host;
    private int port;
    private PublicKey serverKey;
    private ObjectInputStream inputStream;
    private EncryptionAlgorithm encryption;

    public Communication() {
    }

    public Communication(
            String name,
            String host,
            int port,
            PublicKey serverKey) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.serverKey = serverKey;
    }

    public void connect(
            EncryptionAlgorithm encryption,
            ThreadPool threadPool) throws IOException {
        this.encryption = encryption;
        try {
            socket = new Socket(
                    host,
                    port
            );
            out = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            setupInputHandler(
                    encryption,
                    inputStream,
                    threadPool
            );
            createSession(
                    encryption,
                    serverKey
            );
        } catch (UnknownHostException uhe) {
            LOGGER.error(
                    uhe,
                    () -> "Failed to connect to the following host: " + host
            );
        }
    }

    public void setupInputHandler(
            EncryptionAlgorithm encryption,
            ObjectInputStream in,
            ThreadPool threadPool) {
        inputHandler = new InputHandler(
                in,
                encryption
        );
        inputHandler.setupDistributor(threadPool);
        threadPool.startThread(
                inputHandler,
                "InputHandler"
        );
    }

    private void createSession(
            EncryptionAlgorithm encryption,
            PublicKey serverKey) throws IOException {
        RSA rsa = new RSA(new RSAKey(new KeyPair(
                serverKey,
                null
        )));
        out.writeObject(rsa.encrypt(Conversion.convertToBytes(new Session((AESKey) encryption.getKey()))));
        out.flush();

        Response obj;
        while ((obj = inputHandler.containsResponse()) == null) {
        }
        if (obj instanceof AllIsWell) {

        }
    }

    public void sendObject(Object obj) {
        try {
            out.writeObject(encryption.encrypt(Conversion.convertToBytes(obj)));
            out.flush();
        } catch (IOException e) {
            LOGGER.error(
                    e,
                    () -> "Lost connection to server. Crashing now."
            );
            System.exit(1);
        }
    }

    // TODO: Need to close correctly?
    public void close() {
        inputHandler.close();
        try {
            socket.close();
        } catch (IOException ioe) {
            LOGGER.error(
                    ioe,
                    () -> "Failed to close Communication gracefully."
            );
        }
    }

    public Response getNextResponse() {
        Response response;
        while ((response = inputHandler.containsResponse()) == null) {
        }
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

    public String getName() {
        return name;
    }

    public InputDistributor getInputDistributor() {
        return inputHandler.getDistributor();
    }
}