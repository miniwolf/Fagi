package com.fagi.worker.running;

import com.fagi.worker.Worker;

/**
 * This strategy simply returns the value of {@link Worker#isRunning()}
 */
public class CheckFieldRunningStrategy implements IsWorkerRunningStrategy {
    private final Worker worker;

    /**
     * @param worker the worker the strategy should check on
     */
    public CheckFieldRunningStrategy(Worker worker) {
        this.worker = worker;
    }

    @Override
    public boolean isRunning() {
        return worker.isRunning();
    }
}
