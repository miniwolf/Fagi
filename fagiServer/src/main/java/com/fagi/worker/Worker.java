package com.fagi.worker;

/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Worker.java
 *
 * Worker thread for each client.
 */

import com.fagi.running.CheckFieldRunningStrategy;
import com.fagi.running.IsRunningStrategy;

public abstract class Worker implements Runnable {
    protected IsRunningStrategy isRunningStrategy = new CheckFieldRunningStrategy();

    public boolean isRunning() {
        return isRunningStrategy.isRunning();
    }

    /**
     * Used by tests to change the strategy used to check if worker is running
     * @param strategy the strategy to set
     */
    void setIsRunningStrategy(IsRunningStrategy strategy) {
        isRunningStrategy = strategy;
    }
}