/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Main.java
 */

import com.fagi.encryption.RSA;

import java.io.File;

/**
 * Handling server start.
 */
class Main {
    public static void main(String[] args) throws Exception {
        Data.loadUsers();

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 4242;

        Server server = new Server(port);
        server.start();
    }
}