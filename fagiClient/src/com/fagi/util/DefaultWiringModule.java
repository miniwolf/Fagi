package com.fagi.util;

import com.fagi.config.ServerConfig;
import com.fagi.network.Communication;
import com.fagi.utility.Logger;
import com.google.inject.AbstractModule;

import java.io.IOException;

public class DefaultWiringModule extends AbstractModule {
    @Override
    protected void configure() {
        String configLocation = "config/serverinfo.config";
        try {
            ServerConfig config = ServerConfig.pathToServerConfig(configLocation);
            bind(Communication.class).toInstance(
                    new Communication(config.getName(), config.getIp(), config.getPort(),
                                      config.getServerKey()));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Logger.logStackTrace(e);
        }
    }
}
