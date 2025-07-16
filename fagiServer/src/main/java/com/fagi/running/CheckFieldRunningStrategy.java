package com.fagi.running;

/**
 * Checks a field to determine if it's running.
 */
public class CheckFieldRunningStrategy implements IsRunningStrategy {
    private boolean running = true;

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void stop() {
        running = false;
    }
}
