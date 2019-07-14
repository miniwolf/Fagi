package com.fagi.main;
/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Main.java
 */

import com.fagi.model.Data;
import com.fagi.server.Server;

/**
 * Handling server start.
 */
class Main {
    public static void main(String[] args) {
        Data data = new Data();
        data.loadUsers();

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 4242;

        Server server = new Server(port, data);
        server.start();
    }
}