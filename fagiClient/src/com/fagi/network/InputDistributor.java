package com.fagi.network;

import com.fagi.model.messages.InGoingMessages;
import com.fagi.network.handlers.container.Container;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Marcus on 09-07-2016.
 */
public class InputDistributor implements Runnable {
    private final Map<Class, Container> containers;
    private LinkedBlockingQueue<InGoingMessages> messages = new LinkedBlockingQueue<>();
    private boolean running = true;

    public InputDistributor(Map<Class, Container> containers) {
        this.containers = containers;
    }

    @Override
    public void run() {
        try {
            while (running) {
                InGoingMessages input = messages.take();
                Container container = containers.get(input.getClass());
                if ( container == null ) {
                    System.err.println("Missing handler: " + input.getClass());
                    messages.put(input);
                    continue;
                }
                container.addObject(input);
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
            stop();
        }
    }

    public void addMessage(InGoingMessages msg) {
        messages.add(msg);
    }

    public void stop() {
        this.running = false;
    }
}
