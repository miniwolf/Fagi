package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.message.VideoMessage;
import com.fagi.network.InputHandler;
import com.fagi.network.handlers.container.Container;
import com.fagi.network.handlers.container.DefaultContainer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Sidheag on 2016-07-07.
 */
public class VideoMessageHandler implements Handler {
    private Container container = new DefaultContainer();
    private Runnable runnable = new DefaultThreadHandler(container, this);
    private final MainScreen mainScreen;

    public VideoMessageHandler(MainScreen mainScreen){
        container.setThread(runnable);
        InputHandler.register(VideoMessage.class, container);
        this.mainScreen = mainScreen;
    }

    @Override
    public void handle(InGoingMessages inMessage) {
        VideoMessage videoChunk = (VideoMessage) inMessage;
        ArrayList<BufferedImage> imageList = videoChunk.getData();
        System.out.println("Received images count: " + imageList.size());
    }

    @Override
    public Runnable getRunnable() { return runnable; }
}
