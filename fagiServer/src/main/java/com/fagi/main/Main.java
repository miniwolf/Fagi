package com.fagi.main;
/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Main.java
 */

import com.fagi.model.Data;
import com.fagi.server.Server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Handling server start.
 */
class Main {
    public static void main(String[] args) {
        Data data = new Data();
        data.loadUsers();

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 4242;

        Server server = new Server(port, data);

        try {
            var serverSocket = new ServerSocket(port);

            server.start(serverSocket);
        } catch (IOException e) {
            System.out.println("Error while creating socket, are you sure you can use port " + port + " on you system?");
        }
    }
}