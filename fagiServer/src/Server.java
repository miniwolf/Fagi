/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Server.java
 *
 * Listening socket for incoming transmissions from clients.
 */

import com.fagi.encryption.KeyStorage;
import com.fagi.encryption.RSAKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

class Server {
    private final String configFile = "config/server.config";
    private boolean running = true;
    private final int port;

    public Server(int port) {
        this.port = port;
        File f = new File(configFile);
        File dir = new File("config");

        if (!f.exists()) {
            if (!dir.exists()) {
                dir.mkdir();
            }

            try {
                f.createNewFile();
                String ip = "127.0.0.1";
                PublicKey pk = ((RSAKey) Encryption.getInstance().getRSA().getKey()).getKey().getPublic();
                X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(pk.getEncoded());
                String s = "ip:"+ ip + "\npk:";
                FileOutputStream fos = new FileOutputStream(configFile);
                fos.write(s.getBytes());
                fos.write(x509EncodedKeySpec.getEncoded());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        while ( running ) {
            try {
                workerCreation(ss);
            } catch (IOException e) {
                System.out.println("Error in server loop exception = " + e);
                running = false;
            }
        }

        System.out.println("Stopping Server");

        if(ss != null) {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void workerCreation(ServerSocket ss) throws IOException {
        Socket s = ss.accept();
        Thread workerThread = new Thread(new Worker(s));
        workerThread.start();
    }
}