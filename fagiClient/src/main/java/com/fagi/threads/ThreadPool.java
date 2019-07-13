package com.fagi.threads;

import java.util.ArrayList;
import java.util.List;

public class ThreadPool {
    private static final Object lock = new Object();
    private List<Thread> threads = new ArrayList<>();

    public Thread startThread(Runnable runnable, String name) {
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
