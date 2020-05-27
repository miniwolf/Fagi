package com.fagi.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class ThreadPool {
    private final Object lock = new Object();
    private final List<Thread> threads = new ArrayList<>();
    private final Timer timer;

    public ThreadPool() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                var finishedThreads = threads
                        .stream()
                        .filter(thread -> !thread.isAlive())
                        .collect(Collectors.toList());
                threads.removeAll(finishedThreads);
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 60000);
    }

    public Thread startThread(
            Runnable runnable,
            String name) {
        var thread = new Thread(runnable, name);
        thread.setDaemon(true);
        synchronized (lock) {
            threads.add(thread);
        }
        thread.start();
        return thread;
    }

    public void stopThreads() {
        threads.forEach(Thread::interrupt);
        threads.clear();
    }

    public void printThreads() {
        threads.forEach(thread -> System.out.println("Thread: " + thread.getName() + " alive: " + thread.isAlive()));
    }
}
