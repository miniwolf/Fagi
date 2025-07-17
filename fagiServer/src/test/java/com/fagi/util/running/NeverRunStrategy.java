package com.fagi.util.running;

import com.fagi.running.IsRunningStrategy;

/**
 * An IsRunningStrategy that always returns false
 */
public class NeverRunStrategy implements IsRunningStrategy {
    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void stop() {
    }
}
