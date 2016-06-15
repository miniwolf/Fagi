/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Server.java
 *
 * Listening socket for incoming transmissions from clients.
 */

import com.fagi.config.ServerConfig;
import com.fagi.encryption.KeyStorage;
import com.fagi.encryption.RSAKey;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

class Server {
    private final String configFile = "config/serverinfo.config";
    private boolean running = true;
    private final int port;
    private final ConversationHandler handler = new ConversationHandler();
    private Thread conversationHandlerThread;

    public Server(int port) {
        this.port = port;
        try {
            String name = "test";
            String ip =  "localhost"; //getExternalIP();
            PublicKey pk = ((RSAKey) Encryption.getInstance().getRSA().getKey()).getKey().getPublic();
            ServerConfig config = new ServerConfig(name, ip, port, pk);
            config.saveToPath(configFile);
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

        conversationHandlerThread = new Thread(handler);
        conversationHandlerThread.start();
        try {
            Data.loadConversations();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        OutputWorker outWorker = new OutputWorker(socket);
        Thread outputWorker = new Thread(outWorker);
        Thread inputWorker = new Thread(new InputWorker(socket, outWorker));
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