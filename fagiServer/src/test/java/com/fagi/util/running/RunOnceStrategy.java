package com.fagi.util.running;

import com.fagi.running.IsRunningStrategy;

/**
 * This strategy is used to test Input/Output workers and Server. This allows unit tests to make the run method of the
 * worker or server to run once, after which it will break out of its while loop.
 */
public class RunOnceStrategy implements IsRunningStrategy {
    private boolean hasRun = false;

    @Override
    public boolean isRunning() {
        if (hasRun) {
            return false;
        }
        hasRun = true;
        return true;
    }

    @Override
    public void stop() {
        hasRun = true;
    }
}
