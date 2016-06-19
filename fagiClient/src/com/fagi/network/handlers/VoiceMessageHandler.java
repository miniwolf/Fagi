/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.message.VoiceMessage;
import com.fagi.network.InputHandler;
import com.fagi.network.VoiceReceiver;

/**
 * @author miniwolf
 */
public class VoiceMessageHandler implements Handler {
    private Container container = new DefaultContainer();
    private Runnable runnable = new DefaultThreadHandler(container, this);

    public VoiceMessageHandler() {
        InputHandler.register(VoiceMessage.class, container);
    }

    @Override
    public void handle(InGoingMessages voiceMessage) {
        VoiceReceiver.playAudio((VoiceMessage) voiceMessage);
    }

    @Override
    public Runnable getRunnable() {
        return runnable;
    }
}
