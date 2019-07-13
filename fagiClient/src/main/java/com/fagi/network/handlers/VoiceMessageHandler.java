/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import com.fagi.model.messages.message.VoiceMessage;
import com.fagi.network.InputDistributor;
import com.fagi.network.VoiceReceiver;
import com.fagi.network.VoiceRecorder;
import com.fagi.network.handlers.container.Container;
import com.fagi.network.handlers.container.DefaultContainer;

/**
 * @author miniwolf
 */
public class VoiceMessageHandler implements Handler<VoiceMessage> {
    private Container<VoiceMessage> container = new DefaultContainer<VoiceMessage>();
    private Runnable runnable = new DefaultThreadHandler<>(container, this);

    public VoiceMessageHandler(InputDistributor<VoiceMessage> inputDistributor) {
        container.setThread(runnable);
        inputDistributor.register(VoiceMessage.class, container);
    }

    @Override
    public void handle(VoiceMessage voiceMessage) {
        VoiceReceiver.playAudio(voiceMessage);
    }

    @Override
    public Runnable getRunnable() {
        return runnable;
    }
}
