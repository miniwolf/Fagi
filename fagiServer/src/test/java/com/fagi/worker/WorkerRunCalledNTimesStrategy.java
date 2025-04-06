package com.fagi.worker;

import com.fagi.worker.running.IsWorkerRunningStrategy;

public class WorkerRunCalledNTimesStrategy implements IsWorkerRunningStrategy {
    private final int timesToRun;
    private int timesCalled = 0;

    public WorkerRunCalledNTimesStrategy(int timesToRun) {
        this.timesToRun = timesToRun;
    }

    @Override
    public boolean isRunning() {
        return timesToRun > timesCalled++;
    }
}
