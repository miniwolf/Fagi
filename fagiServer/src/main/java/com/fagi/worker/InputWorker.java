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
import com.fagi.handler.InputHandlerFactory;
import com.fagi.logging.FagiLogger;
import com.fagi.logging.FagiLoggerFactory;
import com.fagi.model.Data;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;

/**
 * @author miniwolf
 */
public class InputWorker extends Worker implements InputAgent {
    private static final FagiLogger LOGGER = FagiLoggerFactory.createLogger(InputWorker.class);
    private final InputHandler inputHandler;
    private final Data data;
    private final ObjectInputStream objIn;
    private final OutputWorker out;
    private String myUserName = null;

    private EncryptionAlgorithm<AESKey> aes;
    private boolean sessionCreated = false;

    public InputWorker(
            ObjectInputStream objIn,
            OutputWorker out,
            ConversationHandler handler,
            Data data) {
        this.data = data;
        this.objIn = objIn;
        this.out = out;
        this.inputHandler = InputHandlerFactory.createInputHandler(
                this,
                out,
                handler,
                data
        );
    }

    @Override
    public void run() {
        LOGGER.info(() -> "Starting an input thread");
        while (isRunningStrategy.isRunning()) {
            LOGGER.info(() -> "Running");
            try {
                Object input = objIn.readObject();

                // TODO: We should only accept encrypted objects. This will be fixed in https://trello.com/c/8ieB7CSV
                if (input instanceof byte[]) {
                    input = decryptAndConvertToObject((byte[]) input);
                }
                // TODO: We should either verify that the contained sender property matches the user in our session or we should not look at the sender property at all and trust the session
                // TODO: This will be fixed with https://trello.com/c/KBmf0o1U/54
                inputHandler.handleInput(input);
            } catch (EOFException | SocketException eof) {
                stop();
                LOGGER.info(() -> "Logging out user " + myUserName);
                out.stop();
                data.userLogout(myUserName);
            } catch (Exception e) {
                stop();
                out.stop();
                LOGGER.error(
                        e,
                        () -> "Something went wrong in a input worker while loop."
                );
                LOGGER.info(() -> "Logging out user " + myUserName);
                data.userLogout(myUserName);
            }
        }
        LOGGER.info(() -> "Closing input.");
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
            LOGGER.error(
                    e,
                    () -> "Failed to decrypt or deserialize object."
            );
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
    public void stop() {
        this.isRunningStrategy.stop();
    }

    @Override
    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public boolean isSessionCreated() {
        return sessionCreated;
    }

    public EncryptionAlgorithm<AESKey> getAes() {
        return aes;
    }
}
