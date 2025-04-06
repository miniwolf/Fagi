package com.fagi.worker.running;

/**
 * This strategy tells our workers whether they are running or not. This is introduced to make testing easier and
 * potentially make more complex checks on whether a worker should be running or not
 */
public interface IsWorkerRunningStrategy {
    /**
     * Indicates whether a given worker is running or not
     * @return true if the worker is running, false if not
     */
    boolean isRunning();
}
