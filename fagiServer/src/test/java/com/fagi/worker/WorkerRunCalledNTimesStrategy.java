package com.fagi.worker;

import com.fagi.running.IsRunningStrategy;

public class WorkerRunCalledNTimesStrategy implements IsRunningStrategy {
    private final int timesToRun;
    private int timesCalled = 0;

    public WorkerRunCalledNTimesStrategy(int timesToRun) {
        this.timesToRun = timesToRun;
    }

    @Override
    public boolean isRunning() {
        return timesToRun > timesCalled++;
    }

    @Override
    public void stop() {
        timesCalled = timesToRun;
    }
}
