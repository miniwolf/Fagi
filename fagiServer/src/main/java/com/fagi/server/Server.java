package com.fagi.server;
/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Server.java
 *
 * Listening socket for incoming transmissions from clients.
 */

import com.fagi.config.ServerConfig;
import com.fagi.encryption.Encryption;
import com.fagi.encryption.RSAKey;
import com.fagi.handler.ConversationHandler;
import com.fagi.logging.FagiLogger;
import com.fagi.logging.FagiLoggerFactory;
import com.fagi.model.Data;
import com.fagi.model.InviteCodeContainer;
import com.fagi.running.CheckFieldRunningStrategy;
import com.fagi.running.IsRunningStrategy;
import com.fagi.utility.JsonFileOperations;
import com.fagi.worker.InputWorker;
import com.fagi.worker.OutputWorker;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    private static final FagiLogger LOGGER = FagiLoggerFactory.createLogger(Server.class);
    static final String CONFIG_FILE = "config/serverinfo.config";
    private final Data data;
    private IsRunningStrategy isRunningStrategy = new CheckFieldRunningStrategy();
    private final ConversationHandler handler;
    private Thread conversationHandlerThread;
    private final List<Thread> inputWorkerThreads = Collections.synchronizedList(new ArrayList<>());
    private final List<Thread> outputWorkerThreads = Collections.synchronizedList(new ArrayList<>());

    public Server(
            int port,
            Data data) {
        this.data = data;
        handler = new ConversationHandler(data);
        try {
            String name = "test";
            String ip = "127.0.0.1"; //getExternalIP();
            PublicKey pk = ((RSAKey) Encryption
                    .getInstance()
                    .getRSA()
                    .getKey())
                    .key()
                    .getPublic();
            ServerConfig config = new ServerConfig(
                    name,
                    ip,
                    port,
                    pk
            );
            config.saveToPath(CONFIG_FILE);
            File inviteCodesFile = new File(JsonFileOperations.INVITE_CODES_FILE_PATH);
            if (!inviteCodesFile.exists()) {
                data.storeInviteCodes(new InviteCodeContainer(new ArrayList<>()));
            }
        } catch (IOException e) {
            LOGGER.error(
                    e,
                    () -> "Could not create server."
            );
        }
    }

    public void start(ServerSocket serverSocket) {
        LOGGER.info(() -> "Starting Server");

        conversationHandlerThread = new Thread(handler);
        conversationHandlerThread.setDaemon(true);
        conversationHandlerThread.start();
        data.loadConversations();

        while (isRunningStrategy.isRunning()) {
            try {
                workerCreation(serverSocket);
            } catch (IOException e) {
                LOGGER.error(
                        e,
                        () -> "Error in server loop exception"
                );
                isRunningStrategy.stop();
            }
        }

        conversationHandlerThread.interrupt();

        LOGGER.info(() -> "Stopping Server");

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.error(
                        e,
                        () -> "Failed to close server socket gracefully."
                );
            }
        }
    }

    private void workerCreation(ServerSocket serverSocket) throws IOException {
        Socket socket = serverSocket.accept();
        OutputWorker outWorker = new OutputWorker(
                new ObjectOutputStream(socket.getOutputStream()),
                data
        );
        Thread outputWorker = new Thread(outWorker);
        outputWorker.setDaemon(true);
        Thread inputWorker = new Thread(new InputWorker(
                new ObjectInputStream(socket.getInputStream()),
                outWorker,
                handler,
                data
        ));
        inputWorker.setDaemon(true);
        outputWorkerThreads.add(outputWorker);
        inputWorkerThreads.add(inputWorker);
        outputWorker.start();
        inputWorker.start();
    }

    public boolean isRunning() {
        return isRunningStrategy.isRunning();
    }

    public void setIsRunningStrategy(IsRunningStrategy isRunningStrategy) {
        this.isRunningStrategy = isRunningStrategy;
    }

    Thread getConversationHandlerThread() {
        return conversationHandlerThread;
    }

    public ConversationHandler getHandler() {
        return handler;
    }

    List<Thread> getInputWorkerThreads() {
        return inputWorkerThreads;
    }

    List<Thread> getOutputWorkerThreads() {
        return outputWorkerThreads;
    }
}