package com.fagi.worker.running;

/**
 * This strategy tells our workers whether they are running or not. This is introduced to make testing easier and
 * potentially make more complex ways of testing if a worker should be running than a field
 */
public interface IsWorkerRunningStrategy {
    boolean isRunning();
}
