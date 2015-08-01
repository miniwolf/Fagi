/*
 * Copyright (c) 2015. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network;

import com.fagi.model.VoiceMessage;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * Created by miniwolf on 29-07-2015.
 */
public class VoiceRecorder implements VoiceSystem, Runnable {
    private static TargetDataLine line_in;
    DataLine.Info info_in = new DataLine.Info(TargetDataLine.class, format);
    private boolean running = false;
    private Communication communication;

    public VoiceRecorder(Communication communication) {
        this.communication = communication;
    }

    @Override
    public void run() {
        try {
            line_in = (TargetDataLine) AudioSystem.getLine(info_in);
            line_in.open(format);
            line_in.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        while ( running ) {
            byte[] buffer = new byte[blockSize * 2];
            line_in.read(buffer, 0, blockSize);
            communication.sendObject(new VoiceMessage(buffer, "test", "humus"));
        }
    }

    public void close() {
        running = false;
    }
}
