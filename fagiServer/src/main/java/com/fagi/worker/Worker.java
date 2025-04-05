package com.fagi.worker;

/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Worker.java
 *
 * Worker thread for each client.
 */
import com.fagi.worker.running.CheckFieldRunningStrategy;
import com.fagi.worker.running.IsWorkerRunningStrategy;

public abstract class Worker implements Runnable {
    // TODO: Rework using running and the IsWorkerRunningStrategy
    // Trello issue: https://trello.com/c/8cEhrobt
    boolean running = true;
    protected IsWorkerRunningStrategy isWorkerRunningStrategy = new CheckFieldRunningStrategy(this);

    public boolean isRunning() {
        return running;
    }

    /**
     * Used by tests to change the strategy used to check if worker is running
     * @param strategy the strategy to set
     */
    void setIsWorkerRunningStrategy(IsWorkerRunningStrategy strategy) {
        isWorkerRunningStrategy = strategy;
    }
}