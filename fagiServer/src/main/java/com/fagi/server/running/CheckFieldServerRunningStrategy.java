package com.fagi.server.running;

import com.fagi.running.IsRunningStrategy;
import com.fagi.server.Server;

/**
 * This strategy simply returns the value of {@link Server#isRunning()}
 */
public class CheckFieldServerRunningStrategy implements IsRunningStrategy {
    private final Server server;

    /**
     * @param server the server the strategy should check on
     */
    public CheckFieldServerRunningStrategy(Server server) {
        this.server = server;
    }

    @Override
    public boolean isRunning() {
        return server.isRunning();
    }
}
