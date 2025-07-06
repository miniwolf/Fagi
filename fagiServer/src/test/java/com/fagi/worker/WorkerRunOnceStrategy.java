package com.fagi.worker;

import com.fagi.running.IsRunningStrategy;

/**
 * This strategy is used to test Input/Output workers. This allows unit tests to make the run method of the worker to
 * run once, after which it will break out of its while loop.
 */
public class WorkerRunOnceStrategy implements IsRunningStrategy {
    private boolean workerHasRun = false;

    @Override
    public boolean isRunning() {
        if (workerHasRun) {
            return false;
        }
        workerHasRun = true;
        return true;
    }
}
