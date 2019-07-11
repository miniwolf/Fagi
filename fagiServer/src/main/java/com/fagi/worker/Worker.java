package com.fagi.worker;/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * com.fagi.worker.Worker.java
 *
 * com.fagi.worker.Worker thread for each client.
 */

abstract class Worker implements Runnable {
    boolean running = true;
}