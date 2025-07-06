package com.fagi.running;

/**
 * This strategy tells our workers or server whether they are running or not. This is introduced to make testing easier
 * and potentially make more complex checks on whether a worker or server should be running or not
 */
public interface IsRunningStrategy {
    /**
     * Indicates whether a given worker or server is running or not
     *
     * @return true if the worker is running, false if not
     */
    boolean isRunning();
}
