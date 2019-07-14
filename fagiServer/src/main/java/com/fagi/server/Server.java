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
import com.fagi.model.Data;
import com.fagi.model.InviteCodeContainer;
import com.fagi.utility.JsonFileOperations;
import com.fagi.worker.InputWorker;
import com.fagi.worker.OutputWorker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;

public class Server {
    private final String configFile = "config/serverinfo.config";
    private final Data data;
    private boolean running = true;
    private final int port;
    private final ConversationHandler handler;

    public Server(int port, Data data) {
        this.port = port;
        this.data = data;
        handler = new ConversationHandler(data);
        try {
            String name = "test";
            String ip = "127.0.0.1"; //getExternalIP();
            PublicKey pk = ((RSAKey) Encryption.getInstance().getRSA().getKey()).getKey().getPublic();
            ServerConfig config = new ServerConfig(name, ip, port, pk);
            config.saveToPath(configFile);
            File inviteCodesFile = new File(JsonFileOperations.INVITE_CODES_FILE_PATH);
            if (!inviteCodesFile.exists()) {
                data.storeInviteCodes(new InviteCodeContainer(new ArrayList<>()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Starting Server");
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Error while creating socket, are you sure you can use port " + port + " on you system?");
            running = false;
        }

        Thread conversationHandlerThread = new Thread(handler);
        conversationHandlerThread.setDaemon(true);
        conversationHandlerThread.start();
        data.loadConversations();

        while ( running ) {
            try {
                workerCreation(ss);
            } catch (IOException e) {
                System.out.println("Error in server loop exception = " + e);
                running = false;
            }
        }

        conversationHandlerThread.interrupt();

        System.out.println("Stopping Server");

        if(ss != null) {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void workerCreation(ServerSocket serverSocket) throws IOException {
        Socket socket = serverSocket.accept();
        OutputWorker outWorker = new OutputWorker(socket, data);
        Thread outputWorker = new Thread(outWorker);
        Thread inputWorker = new Thread(new InputWorker(socket, outWorker, handler, data));
        outputWorker.start();
        inputWorker.start();
    }

    private String getExternalIP() {
        String ip = "";

        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            ip = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ip;
    }
}