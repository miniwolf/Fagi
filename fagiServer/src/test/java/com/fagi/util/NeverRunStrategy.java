package com.fagi.util;

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
