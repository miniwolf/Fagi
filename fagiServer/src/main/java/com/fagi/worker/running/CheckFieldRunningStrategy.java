package com.fagi.worker.running;

import com.fagi.worker.Worker;

public class CheckFieldRunningStrategy implements IsWorkerRunningStrategy {
    private final Worker worker;

    public CheckFieldRunningStrategy(Worker worker) {
        this.worker = worker;
    }

    @Override
    public boolean isRunning() {
        return worker.isRunning();
    }
}
