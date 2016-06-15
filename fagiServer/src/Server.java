/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Server.java
 *
 * Listening socket for incoming transmissions from clients.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Server {
    private boolean running = true;
    private final int port;

    public Server(int port) {
        this.port = port;
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
    }

    private void workerCreation(ServerSocket serverSocket) throws IOException {
        Socket socket = serverSocket.accept();
        OutputWorker outWorker = new OutputWorker(socket);
        Thread outputWorker = new Thread(outWorker);
        Thread inputWorker = new Thread(new InputWorker(socket, outWorker));
        outputWorker.start();
        inputWorker.start();
    }
}