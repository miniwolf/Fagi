package com.fagi.worker;
/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

import com.fagi.encryption.AESKey;
import com.fagi.encryption.Conversion;
import com.fagi.encryption.Encryption;
import com.fagi.encryption.EncryptionAlgorithm;
import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author miniwolf
 */
public class InputWorker extends Worker implements InputAgent {
    private final InputHandler inputHandler;
    private final Data data;
    private ObjectInputStream objIn;
    private OutputWorker out;
    private String myUserName = null;

    private EncryptionAlgorithm<AESKey> aes;
    private boolean sessionCreated = false;

    public InputWorker(
            Socket socket,
            OutputWorker out,
            ConversationHandler handler,
            Data data) throws IOException {
        this.data = data;
        System.out.println("Starting an input thread");
        objIn = new ObjectInputStream(socket.getInputStream());
        this.out = out;
        this.inputHandler = new InputHandler(this, out, handler, data);
    }

    @Override
    public void run() {
        while (running) {
            System.out.println("Running");
            try {
                Object input = objIn.readObject();
                if (input instanceof byte[]) {
                    input = decryptAndConvertToObject((byte[]) input);
                }
                inputHandler.handleInput(input);
            } catch (EOFException | SocketException eof) {
                running = false;
                System.out.println("Logging out user " + myUserName);
                out.running = false;
                data.userLogout(myUserName);
            } catch (Exception e) {
                running = false;
                out.running = false;
                System.out.println("Something went wrong in a input worker while loop " + e);
                e.printStackTrace();
                System.out.println("Logging out user " + myUserName);
                data.userLogout(myUserName);
            }
        }
        System.out.println("Closing input");
    }

    private Object decryptAndConvertToObject(byte[] input) {
        input = sessionCreated
                ? aes.decrypt(input)
                : Encryption
                        .getInstance()
                        .getRSA()
                        .decrypt(input);
        try {
            return Conversion.convertFromBytes(input);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setUsername(String username) {
        myUserName = username;
    }

    @Override
    public void setAes(EncryptionAlgorithm<AESKey> aes) {
        this.aes = aes;
    }

    @Override
    public void setSessionCreated(boolean sessionCreated) {
        this.sessionCreated = sessionCreated;
    }

    @Override
    public String getUsername() {
        return myUserName;
    }

    @Override
    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public InputHandler getInputHandler() {
        return inputHandler;
    }
}
