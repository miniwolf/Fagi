package com.fagi.main;
/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Main.java
 */

import com.fagi.logging.FagiLogger;
import com.fagi.logging.FagiLoggerFactory;
import com.fagi.model.Data;
import com.fagi.server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;

/**
 * Handling server start.
 */
class Main {
    private static final FagiLogger LOGGER = FagiLoggerFactory.createLogger(Main.class);

    public static void main(String[] args) {
        Data data = new Data();
        data.loadUsers();

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 4242;

        Server server = new Server(
                port,
                data
        );

        if (!FagiLoggerFactory.isCustomConfigurationAvailable()) {
            FagiLoggerFactory.setupDefaultConfiguration(Path.of("server.log"));
            LOGGER.info(() -> "No log config file specified. Using default log config instead.");
        }

        try {
            var serverSocket = new ServerSocket(port);

            server.start(serverSocket);
        } catch (IOException e) {
            LOGGER.error(
                    e,
                    () -> "Error while creating socket, are you sure you can use port " + port + " on you system?"
            );
        }
    }
}