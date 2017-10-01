package com.fagi.network;

import com.fagi.model.messages.InGoingMessages;
import com.fagi.network.handlers.container.Container;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Marcus on 09-07-2016.
 */
public class InputDistributor implements Runnable {
    private LinkedBlockingQueue<InGoingMessages> messages = new LinkedBlockingQueue<>();
    private boolean running = true;

    @Override
    public void run() {
        try {
            while (running) {
                InGoingMessages input = messages.take();
                Container container = InputHandler.getContainers().get(input.getClass());
                if ( container == null ) {
                    messages.put(input);
                    continue;
                }
                container.addObject(input);
            }
        } catch(InterruptedException e) {
            running = false;
        }
    }

    public void addMessage(InGoingMessages msg) {
        messages.add(msg);
    }

    public void stop() {
        this.running = false;
    }
}
