package com.fagi.worker;

/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Worker.java
 *
 * Worker thread for each client.
 */
import com.fagi.worker.running.CheckFieldWorkerRunningStrategy;
import com.fagi.running.IsRunningStrategy;

public abstract class Worker implements Runnable {
    // TODO: Rework using running and the IsWorkerRunningStrategy
    // Trello issue: https://trello.com/c/8cEhrobt
    boolean running = true;
    protected IsRunningStrategy isWorkerRunningStrategy = new CheckFieldWorkerRunningStrategy(this);

    public boolean isRunning() {
        return running;
    }

    /**
     * Used by tests to change the strategy used to check if worker is running
     * @param strategy the strategy to set
     */
    void setIsWorkerRunningStrategy(IsRunningStrategy strategy) {
        isWorkerRunningStrategy = strategy;
    }
}